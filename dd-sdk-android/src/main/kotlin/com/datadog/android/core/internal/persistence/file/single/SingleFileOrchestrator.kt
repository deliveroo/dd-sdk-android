/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.core.internal.persistence.file.single

import androidx.annotation.WorkerThread
import com.datadog.android.core.internal.persistence.file.FileOrchestrator
import com.datadog.android.core.internal.persistence.file.mkdirsSafe
import java.io.File

internal class SingleFileOrchestrator(
    private val file: File
) : FileOrchestrator {

    // region FileOrchestrator

    /**
     * @param forceNewFile Does not have any effect on this method and it is only used to comply
     * with the [FileOrchestrator] interface.
     */
    @WorkerThread
    override fun getWritableFile(forceNewFile: Boolean): File? {
        file.parentFile?.mkdirsSafe()
        return file
    }

    @WorkerThread
    override fun getReadableFile(excludeFiles: Set<File>): File? {
        file.parentFile?.mkdirsSafe()
        return if (file in excludeFiles) {
            null
        } else {
            file
        }
    }

    @WorkerThread
    override fun getAllFiles(): List<File> {
        file.parentFile?.mkdirsSafe()
        return listOf(file)
    }

    @WorkerThread
    override fun getRootDir(): File? {
        return null
    }

    @WorkerThread
    override fun getFlushableFiles(): List<File> {
        return getAllFiles()
    }

    @WorkerThread
    override fun getMetadataFile(file: File): File? {
        return null
    }

    // endregion
}
