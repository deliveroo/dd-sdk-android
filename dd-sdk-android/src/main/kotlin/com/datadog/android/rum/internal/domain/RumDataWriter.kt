/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.rum.internal.domain

import androidx.annotation.WorkerThread
import com.datadog.android.core.internal.persistence.Serializer
import com.datadog.android.core.internal.persistence.file.FileWriter
import com.datadog.android.core.internal.persistence.file.existsSafe
import com.datadog.android.core.internal.persistence.serializeToByteArray
import com.datadog.android.rum.GlobalRum
import com.datadog.android.rum.internal.monitor.AdvancedRumMonitor
import com.datadog.android.rum.internal.monitor.StorageEvent
import com.datadog.android.rum.model.ActionEvent
import com.datadog.android.rum.model.ErrorEvent
import com.datadog.android.rum.model.LongTaskEvent
import com.datadog.android.rum.model.ResourceEvent
import com.datadog.android.rum.model.ViewEvent
import com.datadog.android.v2.api.EventBatchWriter
import com.datadog.android.v2.api.InternalLogger
import com.datadog.android.v2.core.internal.storage.DataWriter
import java.io.File
import java.util.Locale

internal class RumDataWriter(
    private val serializer: Serializer<Any>,
    private val fileWriter: FileWriter,
    private val internalLogger: InternalLogger,
    private val lastViewEventFile: File
) : DataWriter<Any> {

    // region DataWriter

    @WorkerThread
    override fun write(writer: EventBatchWriter, element: Any): Boolean {
        val byteArray = serializer.serializeToByteArray(element, internalLogger) ?: return false

        synchronized(this) {
            val result = writer.write(byteArray, null)
            if (result) {
                onDataWritten(element, byteArray)
            }
            return result
        }
    }

    // endregion

    // region Internal

    @WorkerThread
    internal fun onDataWritten(data: Any, rawData: ByteArray) {
        when (data) {
            is ViewEvent -> persistViewEvent(rawData)
            is ActionEvent -> notifyEventSent(
                data.view.id,
                StorageEvent.Action(data.action.frustration?.type?.size ?: 0)
            )
            is ResourceEvent -> notifyEventSent(data.view.id, StorageEvent.Resource)
            is ErrorEvent -> {
                if (data.error.isCrash != true) {
                    notifyEventSent(data.view.id, StorageEvent.Error)
                }
            }
            is LongTaskEvent -> {
                if (data.longTask.isFrozenFrame == true) {
                    notifyEventSent(data.view.id, StorageEvent.FrozenFrame)
                } else {
                    notifyEventSent(data.view.id, StorageEvent.LongTask)
                }
            }
        }
    }

    @WorkerThread
    private fun persistViewEvent(data: ByteArray) {
        // directory structure may not exist: currently it is a file which is located in NDK reports
        // folder, so if NDK reporting plugin is not initialized, this NDK reports dir won't exist
        // as well (and no need to write).
        if (lastViewEventFile.parentFile?.existsSafe() == true) {
            fileWriter.writeData(lastViewEventFile, data, false)
        } else {
            internalLogger.log(
                InternalLogger.Level.INFO,
                InternalLogger.Target.MAINTAINER,
                LAST_VIEW_EVENT_DIR_MISSING_MESSAGE.format(Locale.US, lastViewEventFile.parent)
            )
        }
    }

    private fun notifyEventSent(viewId: String, storageEvent: StorageEvent) {
        val rumMonitor = GlobalRum.get()
        if (rumMonitor is AdvancedRumMonitor) {
            rumMonitor.eventSent(viewId, storageEvent)
        }
    }

    companion object {
        const val LAST_VIEW_EVENT_DIR_MISSING_MESSAGE = "Directory structure %s for writing" +
            " last view event doesn't exist."
    }

    // endregion
}
