/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.core.internal.net

import com.datadog.android.v2.api.InternalLogger

@Suppress("StringLiteralDuplication")
internal enum class UploadStatus(val shouldRetry: Boolean) {
    SUCCESS(shouldRetry = false),
    NETWORK_ERROR(shouldRetry = true),
    REQUEST_CREATION_ERROR(shouldRetry = false),
    INVALID_TOKEN_ERROR(shouldRetry = false),
    HTTP_REDIRECTION(shouldRetry = false),
    HTTP_CLIENT_ERROR(shouldRetry = false),
    HTTP_SERVER_ERROR(shouldRetry = true),
    HTTP_CLIENT_RATE_LIMITING(shouldRetry = true),
    UNKNOWN_ERROR(shouldRetry = false);

    @SuppressWarnings("LongMethod")
    fun logStatus(
        context: String,
        byteSize: Int,
        logger: InternalLogger,
        requestId: String? = null
    ) {
        val batchInfo = if (requestId == null) {
            "Batch [$byteSize bytes] ($context)"
        } else {
            "Batch $requestId [$byteSize bytes] ($context)"
        }
        when (this) {
            NETWORK_ERROR -> logger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                "$batchInfo failed because of a network error; we will retry later."
            )
            INVALID_TOKEN_ERROR -> logger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                "$batchInfo failed because your token is invalid. " +
                    "Make sure that the provided token still exists " +
                    "and you're targeting the relevant Datadog site."
            )
            HTTP_REDIRECTION -> logger.log(
                InternalLogger.Level.WARN,
                InternalLogger.Target.USER,
                "$batchInfo failed because of a network redirection; the batch was dropped."
            )
            HTTP_CLIENT_ERROR -> {
                val message = "$batchInfo failed because of a processing error or invalid data; " +
                    "the batch was dropped."
                logger.log(
                    InternalLogger.Level.ERROR,
                    targets = listOf(InternalLogger.Target.USER, InternalLogger.Target.TELEMETRY),
                    message
                )
            }
            HTTP_CLIENT_RATE_LIMITING -> {
                val message = "$batchInfo failed because of a request error; we will retry later."
                logger.log(
                    InternalLogger.Level.ERROR,
                    targets = listOf(InternalLogger.Target.USER, InternalLogger.Target.TELEMETRY),
                    message
                )
            }
            HTTP_SERVER_ERROR -> logger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                "$batchInfo failed because of a server processing error; we will retry later."
            )
            UNKNOWN_ERROR -> logger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                "$batchInfo failed because of an unknown error; the batch was dropped."
            )
            REQUEST_CREATION_ERROR -> logger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.USER,
                "$batchInfo failed because of an error when creating the request; " +
                    "the batch was dropped."
            )
            SUCCESS -> logger.log(
                InternalLogger.Level.VERBOSE,
                InternalLogger.Target.USER,
                "$batchInfo sent successfully."
            )
        }
    }
}
