/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.rum.internal

import com.datadog.android.sessionreplay.internal.SessionReplayFeature
import com.datadog.android.v2.api.context.DatadogContext

internal class FeaturesContextResolver {

    fun resolveHasReplay(context: DatadogContext): Boolean {
        val sessionReplayContext =
            context.featuresContext[SessionReplayFeature.SESSION_REPLAY_FEATURE_NAME]
        return (
            sessionReplayContext?.get(SessionReplayFeature.IS_RECORDING_CONTEXT_KEY)
                as? Boolean
            ) ?: false
    }
}
