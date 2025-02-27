/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.v2.core.internal.storage

import com.datadog.android.v2.api.EventBatchWriter

internal class NoOpEventBatchWriter : EventBatchWriter {

    override fun currentMetadata(): ByteArray? {
        return null
    }

    override fun write(
        event: ByteArray,
        newMetadata: ByteArray?
    ): Boolean = true
}
