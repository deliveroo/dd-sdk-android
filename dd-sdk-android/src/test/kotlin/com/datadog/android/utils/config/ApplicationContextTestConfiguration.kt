/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2016-Present Datadog, Inc.
 */

package com.datadog.android.utils.config

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.datadog.tools.unit.extensions.config.MockTestConfiguration
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import fr.xgouchet.elmyr.Forge
import java.io.File
import java.nio.file.Files

internal open class ApplicationContextTestConfiguration<T : Context>(klass: Class<T>) :
    MockTestConfiguration<T>(klass) {

    lateinit var fakePackageName: String
    lateinit var fakeVersionName: String
    var fakeVersionCode: Int = 0
    lateinit var fakeVariant: String

    lateinit var fakePackageInfo: PackageInfo
    lateinit var fakeAppInfo: ApplicationInfo
    lateinit var mockPackageManager: PackageManager

    lateinit var fakeSandboxDir: File
    lateinit var fakeCacheDir: File
    lateinit var fakeFilesDir: File

    // region ApplicationContextTestConfiguration

    override fun setUp(forge: Forge) {
        super.setUp(forge)

        createFakeInfo(forge)
        mockPackageManager()

        whenever(mockInstance.applicationContext) doReturn mockInstance
        whenever(mockInstance.packageManager) doReturn mockPackageManager
        whenever(mockInstance.packageName) doReturn fakePackageName
        whenever(mockInstance.applicationInfo) doReturn fakeAppInfo

        // ???
        whenever(mockInstance.getSystemService(Context.ACTIVITY_SERVICE)) doReturn mock()
        whenever(mockInstance.getSharedPreferences(any(), any())) doReturn mock()

        // Filesystem
        fakeSandboxDir = Files.createTempDirectory("app-context").toFile()
        fakeCacheDir = File(fakeSandboxDir, "cache")
        fakeFilesDir = File(fakeSandboxDir, "files")
        whenever(mockInstance.cacheDir) doReturn fakeCacheDir
        whenever(mockInstance.filesDir) doReturn fakeFilesDir
    }

    override fun tearDown(forge: Forge) {
        super.tearDown(forge)
        fakeSandboxDir.deleteRecursively()
    }

    // endregion

    // region Internal

    private fun createFakeInfo(forge: Forge) {
        fakePackageName = forge.aStringMatching("[a-z]{2,4}(\\.[a-z]{3,8}){2,4}")
        fakeVersionName = forge.aStringMatching("[0-9](\\.[0-9]{1,3}){2,3}")
        fakeVersionCode = forge.anInt(1, 65536)
        fakeVariant = forge.anElementFrom(forge.anAlphabeticalString(), "")

        fakePackageInfo = PackageInfo()
        fakePackageInfo.packageName = fakePackageName
        fakePackageInfo.versionName = fakeVersionName
        @Suppress("DEPRECATION")
        fakePackageInfo.versionCode = fakeVersionCode
        fakePackageInfo.longVersionCode = fakeVersionCode.toLong()

        fakeAppInfo = ApplicationInfo()
    }

    private fun mockPackageManager() {
        mockPackageManager = mock()
        whenever(
            mockPackageManager.getPackageInfo(
                fakePackageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        ) doReturn fakePackageInfo
        @Suppress("DEPRECATION")
        whenever(mockPackageManager.getPackageInfo(fakePackageName, 0)) doReturn fakePackageInfo
    }

    // endregion
}
