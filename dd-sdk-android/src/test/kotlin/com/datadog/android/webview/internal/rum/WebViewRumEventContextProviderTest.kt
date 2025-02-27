/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.webview.internal.rum

import com.datadog.android.rum.internal.RumFeature
import com.datadog.android.rum.internal.domain.RumContext
import com.datadog.android.utils.config.InternalLoggerTestConfiguration
import com.datadog.android.utils.forge.Configurator
import com.datadog.android.v2.api.InternalLogger
import com.datadog.android.v2.api.context.DatadogContext
import com.datadog.tools.unit.annotations.TestConfigurationsProvider
import com.datadog.tools.unit.extensions.TestConfigurationExtension
import com.datadog.tools.unit.extensions.config.TestConfiguration
import com.nhaarman.mockitokotlin2.verify
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
internal class WebViewRumEventContextProviderTest {

    lateinit var testedContextProvider: WebViewRumEventContextProvider

    @Forgery
    lateinit var fakeDatadogContext: DatadogContext

    @Forgery
    lateinit var fakeRumContext: RumContext

    @BeforeEach
    fun `set up`() {
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(
                    RumFeature.RUM_FEATURE_NAME,
                    mapOf(
                        "application_id" to fakeRumContext.applicationId,
                        "session_id" to fakeRumContext.sessionId,
                        "view_id" to fakeRumContext.viewId,
                        "action_id" to fakeRumContext.actionId
                    )
                )
            }
        )
        testedContextProvider = WebViewRumEventContextProvider()
    }

    @Test
    fun `M return the active context W getRumContext()`() {
        // When
        val rumContext = testedContextProvider.getRumContext(fakeDatadogContext)

        // Then
        assertThat(rumContext?.applicationId)
            .isEqualTo(fakeRumContext.applicationId)
        assertThat(rumContext?.sessionId)
            .isEqualTo(fakeRumContext.sessionId)
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M return null W getRumContext(){ applicationId was null }`(
        missingType: RumContextValueMissingType
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "session_id" to fakeRumContext.sessionId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["application_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["application_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // Then
        assertThat(testedContextProvider.getRumContext(fakeDatadogContext)).isNull()
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M return null W getRumContext(){ sessionId was null }`(
        missingType: RumContextValueMissingType
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "application_id" to fakeRumContext.applicationId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["session_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["session_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // Then
        assertThat(testedContextProvider.getRumContext(fakeDatadogContext)).isNull()
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M log a dev warning log W getRumContext(){ applicationId is null }`(
        missingType: RumContextValueMissingType
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "session_id" to fakeRumContext.sessionId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["application_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["application_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // When
        testedContextProvider.getRumContext(fakeDatadogContext)

        // Then
        verify(logger.mockInternalLogger).log(
            InternalLogger.Level.WARN,
            InternalLogger.Target.USER,
            WebViewRumEventContextProvider.RUM_NOT_INITIALIZED_WARNING_MESSAGE
        )
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M log an sdk error log W getRumContext(){ application is null }`(
        missingType: RumContextValueMissingType
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "session_id" to fakeRumContext.sessionId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["application_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["application_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // When
        testedContextProvider.getRumContext(fakeDatadogContext)

        // Then
        verify(logger.mockInternalLogger).log(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.MAINTAINER,
            WebViewRumEventContextProvider.RUM_NOT_INITIALIZED_ERROR_MESSAGE
        )
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M log a dev warning log W getRumContext(){ sessionId is null }`(
        missingType: RumContextValueMissingType
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "application_id" to fakeRumContext.applicationId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["session_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["session_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // When
        testedContextProvider.getRumContext(fakeDatadogContext)

        // Then
        verify(logger.mockInternalLogger).log(
            InternalLogger.Level.WARN,
            InternalLogger.Target.USER,
            WebViewRumEventContextProvider.RUM_NOT_INITIALIZED_WARNING_MESSAGE
        )
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M log an sdk error log W getRumContext(){ sessionId is null }`(
        missingType: RumContextValueMissingType
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "application_id" to fakeRumContext.applicationId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["session_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["session_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // When
        testedContextProvider.getRumContext(fakeDatadogContext)

        // Then
        verify(logger.mockInternalLogger).log(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.MAINTAINER,
            WebViewRumEventContextProvider.RUM_NOT_INITIALIZED_ERROR_MESSAGE
        )
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M return without internal logging when retrying { sessionId is null }`(
        missingType: RumContextValueMissingType,
        forge: Forge
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "application_id" to fakeRumContext.applicationId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["session_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["session_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // When
        repeat(forge.anInt(min = 1, max = 10)) {
            testedContextProvider.getRumContext(fakeDatadogContext)
        }

        // Then
        verify(logger.mockInternalLogger).log(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.MAINTAINER,
            WebViewRumEventContextProvider.RUM_NOT_INITIALIZED_ERROR_MESSAGE
        )
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M return without internal logging when retrying { applicationId is null }`(
        missingType: RumContextValueMissingType,
        forge: Forge
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "session_id" to fakeRumContext.sessionId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["application_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["application_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // When
        repeat(forge.anInt(min = 1, max = 10)) {
            testedContextProvider.getRumContext(fakeDatadogContext)
        }

        // Then
        verify(logger.mockInternalLogger).log(
            InternalLogger.Level.ERROR,
            InternalLogger.Target.MAINTAINER,
            WebViewRumEventContextProvider.RUM_NOT_INITIALIZED_ERROR_MESSAGE
        )
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M return without dev logging when retrying { sessionId is null }`(
        missingType: RumContextValueMissingType,
        forge: Forge
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "application_id" to fakeRumContext.applicationId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["session_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["session_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // When
        repeat(forge.anInt(min = 1, max = 10)) {
            testedContextProvider.getRumContext(fakeDatadogContext)
        }

        // Then
        verify(logger.mockInternalLogger).log(
            InternalLogger.Level.WARN,
            InternalLogger.Target.USER,
            WebViewRumEventContextProvider.RUM_NOT_INITIALIZED_WARNING_MESSAGE
        )
    }

    @ParameterizedTest
    @EnumSource(RumContextValueMissingType::class)
    fun `M return without dev logging when retrying { applicationId is null }`(
        missingType: RumContextValueMissingType,
        forge: Forge
    ) {
        // Given
        val rumContext = mutableMapOf<String, Any?>(
            "session_id" to fakeRumContext.sessionId,
            "view_id" to fakeRumContext.viewId,
            "action_id" to fakeRumContext.actionId
        )
        when (missingType) {
            RumContextValueMissingType.NULL -> rumContext["application_id"] = null
            RumContextValueMissingType.NULL_UUID -> rumContext["application_id"] =
                RumContext.NULL_UUID
            RumContextValueMissingType.NOT_REGISTERED -> {
                // no-op
            }
        }
        fakeDatadogContext = fakeDatadogContext.copy(
            featuresContext = fakeDatadogContext.featuresContext.toMutableMap().apply {
                put(RumFeature.RUM_FEATURE_NAME, rumContext)
            }
        )

        // When
        repeat(forge.anInt(min = 1, max = 10)) {
            testedContextProvider.getRumContext(fakeDatadogContext)
        }

        // Then
        verify(logger.mockInternalLogger).log(
            InternalLogger.Level.WARN,
            InternalLogger.Target.USER,
            WebViewRumEventContextProvider.RUM_NOT_INITIALIZED_WARNING_MESSAGE
        )
    }

    companion object {
        val logger = InternalLoggerTestConfiguration()

        @TestConfigurationsProvider
        @JvmStatic
        fun getTestConfigurations(): List<TestConfiguration> {
            return listOf(logger)
        }
    }

    enum class RumContextValueMissingType {
        NOT_REGISTERED,
        NULL,
        NULL_UUID
    }
}
