/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.rum.internal.monitor

import com.datadog.android.core.configuration.Configuration
import com.datadog.android.rum.RumErrorSource
import com.datadog.android.rum.RumMonitor
import com.datadog.android.rum.RumPerformanceMetric
import com.datadog.android.rum.internal.debug.RumDebugListener
import com.datadog.android.rum.internal.domain.event.ResourceTiming
import com.datadog.android.rum.model.ViewEvent
import com.datadog.tools.annotation.NoOpImplementation

@SuppressWarnings("ComplexInterface", "TooManyFunctions")
@NoOpImplementation
internal interface AdvancedRumMonitor : RumMonitor {

    fun resetSession()

    fun sendWebViewEvent()

    fun waitForResourceTiming(key: String)

    fun updateViewLoadingTime(key: Any, loadingTimeInNs: Long, type: ViewEvent.LoadingType)

    fun addResourceTiming(key: String, timing: ResourceTiming)

    fun addLongTask(durationNs: Long, target: String)

    fun addCrash(
        message: String,
        source: RumErrorSource,
        throwable: Throwable
    )

    fun eventSent(viewId: String, event: StorageEvent)

    fun eventDropped(viewId: String, event: StorageEvent)

    fun setDebugListener(listener: RumDebugListener?)

    fun sendDebugTelemetryEvent(message: String)

    fun sendErrorTelemetryEvent(message: String, throwable: Throwable?)

    fun sendErrorTelemetryEvent(message: String, stack: String?, kind: String?)

    @Suppress("FunctionMaxLength")
    fun sendConfigurationTelemetryEvent(configuration: Configuration)

    fun notifyInterceptorInstantiated()

    fun updatePerformanceMetric(metric: RumPerformanceMetric, value: Double)
}
