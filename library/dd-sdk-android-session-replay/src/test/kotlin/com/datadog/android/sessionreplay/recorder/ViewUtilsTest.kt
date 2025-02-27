/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.sessionreplay.recorder

import android.os.Build
import android.view.View
import android.view.ViewStub
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.ActionBarContextView
import androidx.appcompat.widget.Toolbar
import com.datadog.android.sessionreplay.forge.ForgeConfigurator
import com.datadog.tools.unit.annotations.TestTargetApi
import com.datadog.tools.unit.extensions.ApiLevelExtension
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(ApiLevelExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(ForgeConfigurator::class)
internal class ViewUtilsTest {

    lateinit var testedViewUtils: ViewUtils

    @BeforeEach
    fun `set up`() {
        testedViewUtils = ViewUtils()
    }

    // region Visibility

    @Test
    fun `M return true W checkIfNotVisible(view isShown false)`(forge: Forge) {
        // Given
        val mockView = forge.aMockView<View>().apply {
            whenever(this.isShown).thenReturn(false)
        }

        // When
        assertThat(testedViewUtils.checkIfNotVisible(mockView)).isTrue
    }

    @Test
    fun `M return true W checkIfNotVisible(view width is 0)`(forge: Forge) {
        // Given
        val mockView = forge.aMockView<View>().apply {
            whenever(this.width).thenReturn(0)
        }

        // When
        assertThat(testedViewUtils.checkIfNotVisible(mockView)).isTrue
    }

    @Test
    fun `M return true W checkIfNotVisible(view height is 0)`(forge: Forge) {
        // Given
        val mockView: View = forge.aMockView<View>().apply {
            whenever(this.height).thenReturn(0)
        }

        // When
        assertThat(testedViewUtils.checkIfNotVisible(mockView)).isTrue
    }

    @Test
    fun `M return false W checkIfNotVisible(view is visible)`(forge: Forge) {
        // Given
        val mockView = forge.aMockView<View>().apply {
            whenever(this.isShown).thenReturn(true)
        }

        // When
        assertThat(testedViewUtils.checkIfNotVisible(mockView)).isFalse
    }

    // endregion

    // region System Noise

    @Test
    @TestTargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun `M return true W checkIfSystemNoise(){view the navigationBarBackground }`(forge: Forge) {
        // Given
        val mockView = forge
            .aMockView<View>()
            .apply {
                whenever(this.id).thenReturn(android.R.id.navigationBarBackground)
            }

        // Then
        assertThat(testedViewUtils.checkIfSystemNoise(mockView)).isTrue
    }

    @Test
    @TestTargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun `M return true W checkIfSystemNoise(){view is statusBarBackground }`(forge: Forge) {
        // Given
        val mockView = forge
            .aMockView<View>()
            .apply {
                whenever(this.id).thenReturn(android.R.id.statusBarBackground)
            }

        // Then
        assertThat(testedViewUtils.checkIfSystemNoise(mockView)).isTrue
    }

    @Test
    fun `M return true W checkIfSystemNoise(){view is ViewStub }`() {
        // Given
        val mockView: ViewStub = mock()

        // Then
        assertThat(testedViewUtils.checkIfSystemNoise(mockView)).isTrue
    }

    @Test
    fun `M return true W checkIfSystemNoise(){view is ActionBarContextView }`() {
        // Given
        val mockView: ActionBarContextView = mock()

        // Then
        assertThat(testedViewUtils.checkIfSystemNoise(mockView)).isTrue
    }

    @Test
    fun `M return false W checkIfSystemNoise(){view is not system noise }`(forge: Forge) {
        // Given
        val mockView = forge.aMockView<View>()

        // Then
        assertThat(testedViewUtils.checkIfSystemNoise(mockView)).isFalse
    }

    // endregion

    // region Toolbar

    @Test
    fun `M return true W checkIsToolbar(){view androidx Toolbar }`(
        forge: Forge
    ) {
        // Given
        val mockToolBar: Toolbar = forge.aMockView<Toolbar>().apply {
            whenever(this.id).thenReturn(androidx.appcompat.R.id.action_bar)
        }

        // Then
        assertThat(testedViewUtils.checkIsToolbar(mockToolBar)).isTrue
    }

    @TestTargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    fun `M return true W checkIsToolbar(){view android Toolbar }`(
        forge: Forge
    ) {
        // Given
        val mockToolBar: android.widget.Toolbar = forge.aMockView()

        // Then
        assertThat(testedViewUtils.checkIsToolbar(mockToolBar)).isTrue
    }

    @Test
    fun `M return false W checkIsToolbar(){view is not toolbar }`(forge: Forge) {
        // Given
        val mockView = forge.aMockView<View>()

        // Then
        assertThat(testedViewUtils.checkIsToolbar(mockView)).isFalse
    }

    // endregion
}
