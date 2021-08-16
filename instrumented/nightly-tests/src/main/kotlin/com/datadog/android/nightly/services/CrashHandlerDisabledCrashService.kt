/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.nightly.services

import android.content.Intent

internal class CrashHandlerDisabledCrashService : JvmCrashService() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return super.onStartCommand(
            intent.apply { action = CRASH_HANDLER_DISABLED_SCENARIO },
            flags,
            startId
        )
    }
}
