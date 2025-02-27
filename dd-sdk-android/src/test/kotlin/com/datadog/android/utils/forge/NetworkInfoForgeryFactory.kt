/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.utils.forge

import com.datadog.android.v2.api.context.NetworkInfo
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class NetworkInfoForgeryFactory : ForgeryFactory<NetworkInfo> {

    override fun getForgery(forge: Forge): NetworkInfo {
        return NetworkInfo(
            connectivity = forge.aValueFrom(NetworkInfo.Connectivity::class.java),
            carrierName = forge.anElementFrom(
                forge.anAlphabeticalString(),
                forge.aWhitespaceString(),
                null
            ),
            carrierId = forge.aNullable { forge.aLong(0, 10000) },
            upKbps = forge.aNullable { forge.aLong(1, Long.MAX_VALUE) },
            downKbps = forge.aNullable { forge.aLong(1, Long.MAX_VALUE) },
            strength = forge.aNullable { forge.aLong(-100, -30) }, // dBm for wifi signal
            cellularTechnology = forge.aNullable { anAlphabeticalString() }
        )
    }
}
