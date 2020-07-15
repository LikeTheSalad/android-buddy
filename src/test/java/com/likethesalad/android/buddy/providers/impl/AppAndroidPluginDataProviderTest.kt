package com.likethesalad.android.buddy.providers.impl

import com.android.build.gradle.AppExtension
import com.google.common.truth.Truth
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.io.File

class AppAndroidPluginDataProviderTest : BaseMockable() {

    @MockK
    lateinit var appExtension: AppExtension

    private lateinit var appAndroidPluginDataProvider: AppAndroidPluginDataProvider

    @Before
    fun setUp() {
        appAndroidPluginDataProvider = AppAndroidPluginDataProvider(appExtension)
    }

    @Test
    fun `Get android booth classpath`() {
        val bootClasspath = mutableListOf<File>(mockk())
        every {
            appExtension.bootClasspath
        }.returns(bootClasspath)

        Truth.assertThat(appAndroidPluginDataProvider.getBootClasspath()).isEqualTo(bootClasspath.toSet())
    }
}