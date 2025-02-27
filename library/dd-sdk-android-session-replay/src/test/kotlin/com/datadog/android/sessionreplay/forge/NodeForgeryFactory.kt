/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.sessionreplay.forge

import com.datadog.android.sessionreplay.recorder.Node
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactory

internal class NodeForgeryFactory : ForgeryFactory<Node> {

    override fun getForgery(forge: Forge): Node {
        return Node(
            wireframe = forge.getForgery(),
            children = forge.aList {
                Node(
                    wireframe = getForgery(),
                    parents = aList { getForgery() }
                )
            },
            parents = forge.aList { getForgery() }
        )
    }
}
