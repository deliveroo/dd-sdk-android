/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.sessionreplay.processor

import com.datadog.android.sessionreplay.model.MobileSegment
import com.datadog.android.sessionreplay.recorder.Node
import java.util.LinkedList
import java.util.Stack

internal class NodeFlattener(private val wireframeUtils: WireframeUtils = WireframeUtils()) {

    internal fun flattenNode(root: Node): List<MobileSegment.Wireframe> {
        val stack = Stack<Node>()
        val list = LinkedList<MobileSegment.Wireframe>()
        stack.push(root)
        while (stack.isNotEmpty()) {
            val node = stack.pop()
            var wireframe = node.wireframe
            wireframeUtils.resolveWireframeClip(wireframe, node.parents)?.let { clip ->
                wireframe = wireframe.copy(clip = clip)
            }
            list.add(wireframe)

            for (i in node.children.count() - 1 downTo 0) {
                stack.push(node.children[i])
            }
        }
        return filterOutInvalidWireframes(list)
    }

    private fun filterOutInvalidWireframes(wireframes: List<MobileSegment.Wireframe>):
        List<MobileSegment.Wireframe> {
        return wireframes.filterIndexed { index, wireframe ->
            wireframeUtils.checkIsValidWireframe(wireframe, wireframes.drop(index + 1))
        }
    }
}
