/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.webview.internal.rum

import com.datadog.android.core.internal.utils.internalLogger
import com.datadog.android.rum.internal.RumFeature
import com.datadog.android.rum.internal.domain.RumContext
import com.datadog.android.v2.api.InternalLogger
import com.datadog.android.v2.api.context.DatadogContext

internal class WebViewRumEventContextProvider {

    private var rumFeatureDisabled = false

    @Suppress("ComplexCondition")
    fun getRumContext(datadogContext: DatadogContext): RumContext? {
        if (rumFeatureDisabled) {
            return null
        }
        val rumContext = datadogContext.featuresContext[RumFeature.RUM_FEATURE_NAME]
        val rumApplicationId = rumContext?.get("application_id") as? String
        val rumSessionId = rumContext?.get("session_id") as? String
        return if (rumApplicationId == null ||
            rumApplicationId == RumContext.NULL_UUID ||
            rumSessionId == null ||
            rumSessionId == RumContext.NULL_UUID
        ) {
            rumFeatureDisabled = true
            internalLogger.log(
                InternalLogger.Level.WARN,
                InternalLogger.Target.USER,
                RUM_NOT_INITIALIZED_WARNING_MESSAGE
            )
            internalLogger.log(
                InternalLogger.Level.ERROR,
                InternalLogger.Target.MAINTAINER,
                RUM_NOT_INITIALIZED_ERROR_MESSAGE
            )
            null
        } else {
            RumContext(applicationId = rumApplicationId, sessionId = rumSessionId)
        }
    }

    companion object {
        const val RUM_NOT_INITIALIZED_WARNING_MESSAGE = "You are trying to use the WebView " +
            "tracking API but the RUM feature was not properly initialized."
        const val RUM_NOT_INITIALIZED_ERROR_MESSAGE = "Trying to consume a bundled rum event" +
            " but the RUM feature was not yet initialized. Missing the RUM context."
    }
}
