package com.likethesalad.android.buddy.providers.impl

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import org.gradle.api.DomainObjectSet
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.junit.Before
import org.junit.Test
import java.io.File

class AppAndroidPluginDataProviderTest : BaseMockable() {

    @MockK
    lateinit var appExtension: AppExtension

    @MockK
    lateinit var logger: Logger

    private lateinit var appAndroidPluginDataProvider: AppAndroidPluginDataProvider

    @Before
    fun setUp() {
        appAndroidPluginDataProvider = AppAndroidPluginDataProvider(appExtension, logger)
    }

    @Test
    fun `Get android booth classpath`() {
        val bootClasspath = mutableListOf<File>(mockk())
        every {
            appExtension.bootClasspath
        }.returns(bootClasspath)

        Truth.assertThat(appAndroidPluginDataProvider.getBootClasspath()).isEqualTo(bootClasspath.toSet())
    }

    @Test
    fun `Get variant java target compatibility version`() {
        val appVariants = mockk<DomainObjectSet<ApplicationVariant>>()
        val javaCompileProvider = mockk<TaskProvider<JavaCompile>>()
        val javaCompile = mockk<JavaCompile>()
        val variantName = "someName"
        val targetCompatibility = "1.7"
        val variant = createApplicationVariantMock(variantName)
        every { javaCompileProvider.hint(JavaCompile::class).get() }.returns(javaCompile)
        every { appExtension.applicationVariants }.returns(appVariants)
        every { appVariants.iterator() }.returns(getVariantsIterator(variant, "otherName"))
        every { variant.javaCompileProvider }.returns(javaCompileProvider)
        every { javaCompile.targetCompatibility }.returns(targetCompatibility)

        val result = appAndroidPluginDataProvider.getJavaTargetCompatibilityVersion(variantName)

        Truth.assertThat(result).isEqualTo(7)
        verify {
            logger.i("Using java target version {}", 7)
        }
    }

    @Test
    fun `Fallback to current JVM target compatibility version when variant not found`() {
        val appVariants = mockk<DomainObjectSet<ApplicationVariant>>()
        val variantName = "someName"
        val currentJdkJavaVersion = JavaVersion.VERSION_1_7
        mockkStatic(JavaVersion::class)
        every { JavaVersion.current() }.returns(currentJdkJavaVersion)
        every { appExtension.applicationVariants }.returns(appVariants)
        every { appVariants.iterator() }.returns(getVariantsIterator(null, "otherName"))

        val result = appAndroidPluginDataProvider.getJavaTargetCompatibilityVersion(variantName)

        Truth.assertThat(result).isEqualTo(7)
        verify {
            logger.w("Java target version for android variant {} not found, falling back to JVM's", variantName)
            logger.i("Using java target version {}", 7)
        }
    }

    private fun getVariantsIterator(
        chosen: ApplicationVariant?,
        vararg variantNames: String
    ): MutableIterator<ApplicationVariant> {
        val list = mutableListOf<ApplicationVariant>()
        if (chosen != null) {
            list.add(chosen)
        }
        val others = variantNames.map {
            createApplicationVariantMock(it)
        }
        list.addAll(others)
        return list.listIterator()
    }

    private fun createApplicationVariantMock(name: String): ApplicationVariant {
        val mock = mockk<ApplicationVariant>()
        every { mock.name }.returns(name)
        return mock
    }
}