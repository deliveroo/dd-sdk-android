/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.core.internal.data.upload

import com.datadog.android.core.internal.net.DataUploader
import com.datadog.android.core.internal.persistence.PayloadDecoration
import com.datadog.android.core.internal.persistence.file.FileMover
import com.datadog.android.core.internal.persistence.file.FileOrchestrator
import com.datadog.android.core.internal.persistence.file.batch.BatchFileReader
import com.datadog.android.utils.forge.Configurator
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.ForgeConfiguration
import fr.xgouchet.elmyr.junit5.ForgeExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import java.io.File

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
@ForgeConfiguration(Configurator::class)
internal class DataFlusherTest {
    lateinit var testedFlusher: DataFlusher

    @Mock
    lateinit var mockDataUploader: DataUploader

    @Mock
    lateinit var mockFileOrchestrator: FileOrchestrator

    lateinit var payloadDecoration: PayloadDecoration

    @Mock
    lateinit var mockFileReader: BatchFileReader

    @Mock
    lateinit var mockFileMover: FileMover

    @StringForgery
    lateinit var fakePrefix: String

    @StringForgery
    lateinit var fakeSuffix: String

    @StringForgery
    lateinit var fakeSeparator: String

    @BeforeEach
    fun `set up`() {
        payloadDecoration = PayloadDecoration(fakePrefix, fakeSuffix, fakeSeparator)
        testedFlusher = DataFlusher(
            mockFileOrchestrator,
            payloadDecoration,
            mockFileReader,
            mockFileMover
        )
    }

    @Test
    fun `M upload all the batches W flush`(
        forge: Forge
    ) {
        // Given
        val fakeFiles = forge.aList { mock<File>() }
        val fakeBatches = forge
            .aList(fakeFiles.size) {
                forge
                    .aList {
                        forge.aString()
                    }
                    .map { it.toByteArray() }
            }
        whenever(mockFileOrchestrator.getFlushableFiles()).thenReturn(fakeFiles)
        fakeFiles.forEachIndexed { index, file ->
            whenever(
                mockFileReader.readData(file)
            ).thenReturn(fakeBatches[index])
        }

        // When
        testedFlusher.flush(mockDataUploader)

        // Then
        fakeBatches.forEach {
            val expectedPayload =
                payloadDecoration.prefixBytes + it.reduce { acc, bytes ->
                    acc + payloadDecoration.separatorBytes + bytes
                } + payloadDecoration.suffixBytes

            verify(mockDataUploader).upload(expectedPayload)
        }
    }

    @Test
    fun `M delete all the batches W flush`(
        forge: Forge
    ) {
        // Given
        val fakeFiles = forge.aList { mock<File>() }
        val fakeMetaFiles =
            forge.aList(fakeFiles.size) { mock<File>().apply { whenever(exists()) doReturn true } }

        val fakeBatches = forge
            .aList(fakeFiles.size) {
                forge
                    .aList {
                        forge.aString()
                    }
                    .map { it.toByteArray() }
            }
        whenever(mockFileOrchestrator.getFlushableFiles()).thenReturn(fakeFiles)
        fakeFiles.forEachIndexed { index, file ->
            whenever(
                mockFileReader.readData(file)
            ).thenReturn(fakeBatches[index])
            whenever(
                mockFileOrchestrator.getMetadataFile(file)
            ).thenReturn(fakeMetaFiles[index])
        }

        // When
        testedFlusher.flush(mockDataUploader)

        // Then
        fakeFiles.forEach {
            verify(mockFileMover).delete(it)
        }
        fakeMetaFiles.forEach {
            verify(mockFileMover).delete(it)
        }
    }

    @Test
    fun `M do nothing W flush { no data available }`() {
        // Given
        whenever(mockFileOrchestrator.getFlushableFiles()).thenReturn(emptyList())

        // When
        testedFlusher.flush(mockDataUploader)

        // Then
        verifyZeroInteractions(mockFileReader)
        verifyZeroInteractions(mockDataUploader)
    }
}
