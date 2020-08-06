package com.likethesalad.android.buddy.utils

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.google.common.truth.Truth
import com.likethesalad.android.common.providers.AndroidExtensionProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidExtensionDataProviderTest : BaseMockable() {

    @MockK
    lateinit var androidApplicationExtensionProvider: AndroidExtensionProvider

    @MockK
    lateinit var applicationExtension: AppExtension

    private lateinit var applicationExtensionDataProvider: AndroidExtensionDataProvider

    @Before
    fun setUp() {
        every { androidApplicationExtensionProvider.getAndroidExtension() }.returns(applicationExtension)
        applicationExtensionDataProvider = AndroidExtensionDataProvider(androidApplicationExtensionProvider)
    }

    @Test
    fun `Get android boot classpath`() {
        val expectedBootClasspath = listOf<File>()
        every { applicationExtension.bootClasspath }.returns(expectedBootClasspath)

        Truth.assertThat(applicationExtensionDataProvider.getBootClasspath()).isEqualTo(expectedBootClasspath)
    }

    @Test
    fun `Get application variant by name`() {
        val variantName = "someVariantName"
        val variant = mockk<ApplicationVariant>()
        val iterator = mutableListOf(variant).iterator()
        val appVariants = mockk<DomainObjectSet<ApplicationVariant>>()
        every { variant.name }.returns(variantName)
        every { appVariants.iterator() }.returns(iterator)
        every { applicationExtension.applicationVariants }.returns(appVariants)

        val result = applicationExtensionDataProvider.getVariantByName(variantName)

        Truth.assertThat(result).isEqualTo(variant)
    }

    @Test
    fun `Run on all application variants`() {
        val actionCaptor = slot<Action<ApplicationVariant>>()
        val appVariants = mockk<DomainObjectSet<ApplicationVariant>>()
        val variant = mockk<ApplicationVariant>()
        var capturedVariant: BaseVariant? = null
        every { applicationExtension.applicationVariants }.returns(appVariants)
        every { appVariants.all(capture(actionCaptor)) } just Runs

        applicationExtensionDataProvider.allVariants {
            capturedVariant = it
        }
        actionCaptor.captured.execute(variant)

        Truth.assertThat(capturedVariant).isEqualTo(variant)
    }
}