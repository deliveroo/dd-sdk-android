/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.rum.internal.domain.scope

import com.datadog.android.rum.model.ActionEvent
import com.datadog.android.rum.model.ErrorEvent
import com.datadog.android.rum.model.LongTaskEvent
import com.datadog.android.rum.model.ResourceEvent
import com.datadog.android.rum.model.ViewEvent
import com.datadog.android.utils.config.InternalLoggerTestConfiguration
import com.datadog.android.utils.forge.Configurator
import com.datadog.android.utils.forge.aStringNotMatchingSet
import com.datadog.android.v2.api.InternalLogger
import com.datadog.tools.unit.annotations.TestConfigurationsProvider
import com.datadog.tools.unit.extensions.TestConfigurationExtension
import com.datadog.tools.unit.extensions.config.TestConfiguration
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import java.util.Locale

@Extensions(
    ExtendWith(ForgeExtension::class),
    ExtendWith(TestConfigurationExtension::class)
)
@ForgeConfiguration(Configurator::class)
internal class RumEventSourceExtTest {

    private lateinit var fakeInvalidSource: String
    private lateinit var fakeValidRumSource: String

    @BeforeEach
    fun `set up`(forge: Forge) {
        // we are using the ViewEvent.Source here as the source enum for all the events is
        // generated from the same _common-schema.json
        fakeInvalidSource = forge.aStringNotMatchingSet(
            ViewEvent.Source.values()
                .map {
                    it.toJson().asString
                }.toSet()
        )
        fakeValidRumSource = forge.aValueFrom(ViewEvent.Source::class.java).toJson().asString
    }

    // region ViewEvent

    @Test
    fun `M resolve the ViewEvent source W viewEventSource`() {
        assertThat(ViewEvent.Source.tryFromSource(fakeValidRumSource)?.toJson()?.asString)
            .isEqualTo(fakeValidRumSource)
    }

    @Test
    fun `M return null W viewEventSource { unknown source }`() {
        assertThat(ViewEvent.Source.tryFromSource(fakeInvalidSource)).isNull()
    }

    @Test
    fun `M send an error dev log W viewEventSource { unknown source }`() {
        // When
        ViewEvent.Source.tryFromSource(fakeInvalidSource)

        // Then
        verify(logger.mockInternalLogger).log(
            eq(InternalLogger.Level.ERROR),
            eq(InternalLogger.Target.USER),
            eq(
                UNKNOWN_SOURCE_WARNING_MESSAGE_FORMAT
                    .format(Locale.US, fakeInvalidSource)
            ),
            argThat { this is NoSuchElementException }
        )
    }

    // endregion

    // region ActionEvent

    @Test
    fun `M resolve the Action source W actionEventSource`() {
        assertThat(ActionEvent.Source.tryFromSource(fakeValidRumSource)?.toJson()?.asString)
            .isEqualTo(fakeValidRumSource)
    }

    @Test
    fun `M return null W actionEventSource { unknown source }`() {
        assertThat(ActionEvent.Source.tryFromSource(fakeInvalidSource)).isNull()
    }

    @Test
    fun `M send an error dev log W actionEventSource { unknown source }`() {
        // When
        ActionEvent.Source.tryFromSource(fakeInvalidSource)

        // Then
        verify(logger.mockInternalLogger).log(
            eq(InternalLogger.Level.ERROR),
            eq(InternalLogger.Target.USER),
            eq(
                UNKNOWN_SOURCE_WARNING_MESSAGE_FORMAT
                    .format(Locale.US, fakeInvalidSource)
            ),
            argThat { this is NoSuchElementException }
        )
    }

    // endregion

    // region ErrorEvent

    @Test
    fun `M resolve the ErrorEvent source W errorEventSource`() {
        assertThat(
            ErrorEvent.ErrorEventSource.tryFromSource(fakeValidRumSource)?.toJson()?.asString
        )
            .isEqualTo(fakeValidRumSource)
    }

    @Test
    fun `M return null W errorEventSource { unknown source }`() {
        assertThat(ErrorEvent.ErrorEventSource.tryFromSource(fakeInvalidSource)).isNull()
    }

    @Test
    fun `M send an error dev log W errorEventSource { unknown source }`() {
        // When
        ErrorEvent.ErrorEventSource.tryFromSource(fakeInvalidSource)

        // Then
        verify(logger.mockInternalLogger).log(
            eq(InternalLogger.Level.ERROR),
            eq(InternalLogger.Target.USER),
            eq(
                UNKNOWN_SOURCE_WARNING_MESSAGE_FORMAT
                    .format(Locale.US, fakeInvalidSource)
            ),
            argThat { this is NoSuchElementException }
        )
    }

    // endregion

    // region ResourceEvent

    @Test
    fun `M resolve the ResourceEvent source W resourceEventSource`() {
        assertThat(ResourceEvent.Source.tryFromSource(fakeValidRumSource)?.toJson()?.asString)
            .isEqualTo(fakeValidRumSource)
    }

    @Test
    fun `M return null W resourceEventSource { unknown source }`() {
        assertThat(ResourceEvent.Source.tryFromSource(fakeInvalidSource)).isNull()
    }

    @Test
    fun `M send an error dev log W resourceEventSource { unknown source }`() {
        // When
        ResourceEvent.Source.tryFromSource(fakeInvalidSource)

        // Then
        verify(logger.mockInternalLogger).log(
            eq(InternalLogger.Level.ERROR),
            eq(InternalLogger.Target.USER),
            eq(
                UNKNOWN_SOURCE_WARNING_MESSAGE_FORMAT
                    .format(Locale.US, fakeInvalidSource)
            ),
            argThat { this is NoSuchElementException }
        )
    }

    // endregion

    // region LongTaskEvent

    @Test
    fun `M resolve the LongTaskEvent source W longTaskEventSource`() {
        assertThat(LongTaskEvent.Source.tryFromSource(fakeValidRumSource)?.toJson()?.asString)
            .isEqualTo(fakeValidRumSource)
    }

    @Test
    fun `M return null W longTaskEventSource { unknown source }`() {
        assertThat(LongTaskEvent.Source.tryFromSource(fakeInvalidSource)).isNull()
    }

    @Test
    fun `M send an error dev log W longTaskEventSource { unknown source }`() {
        // When
        LongTaskEvent.Source.tryFromSource(fakeInvalidSource)

        // Then
        verify(logger.mockInternalLogger).log(
            eq(InternalLogger.Level.ERROR),
            eq(InternalLogger.Target.USER),
            eq(
                UNKNOWN_SOURCE_WARNING_MESSAGE_FORMAT
                    .format(Locale.US, fakeInvalidSource)
            ),
            argThat { this is NoSuchElementException }
        )
    }

    // endregion

    companion object {
        val logger = InternalLoggerTestConfiguration()

        @TestConfigurationsProvider
        @JvmStatic
        fun getTestConfigurations(): List<TestConfiguration> {
            return listOf(logger)
        }
    }
}
