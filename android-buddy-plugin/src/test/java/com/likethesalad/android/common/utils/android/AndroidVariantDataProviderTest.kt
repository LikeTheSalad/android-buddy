package com.likethesalad.android.common.utils.android

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.gradle.api.DomainObjectSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.junit.Before
import org.junit.Test

class AndroidVariantDataProviderTest : BaseMockable() {

    @MockK
    lateinit var androidAppExtension: AppExtension

    @MockK
    lateinit var androidExtensionDataProvider: AndroidExtensionDataProvider

    @MockK
    lateinit var logger: Logger

    private val variantName = "someVariantName"
    private lateinit var androidVariantDataProvider: AndroidVariantDataProvider

    @Before
    fun setUp() {
        androidVariantDataProvider = AndroidVariantDataProvider(
            androidExtensionDataProvider,
            logger,
            variantName
        )
    }

    @Test
    fun `Get variant java target compatibility version`() {
        createAndSetMainVariantMock()
        val javaCompileProvider = mockk<TaskProvider<JavaCompile>>()
        val javaCompile = mockk<JavaCompile>()
        val targetCompatibility = "1.7"
        val variant = createAndSetMainVariantMock("otherName")
        every { javaCompileProvider.hint(JavaCompile::class).get() }.returns(javaCompile)
        every { javaCompile.targetCompatibility }.returns(targetCompatibility)
        every { variant.javaCompileProvider }.returns(javaCompileProvider)

        val result = androidVariantDataProvider.getJavaTargetCompatibilityVersion()

        Truth.assertThat(result).isEqualTo(7)
        verify {
            logger.info("Using java target version {}", 7)
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

    private fun createAndSetMainVariantMock(vararg otherVariantNames: String): ApplicationVariant {
        val appVariants = mockk<DomainObjectSet<ApplicationVariant>>()
        val variant = createApplicationVariantMock(variantName)
        every { androidExtensionDataProvider.getVariantByName(variantName) }.returns(variant)
        every { androidAppExtension.applicationVariants }.returns(appVariants)
        every { appVariants.iterator() }.returns(getVariantsIterator(variant, *otherVariantNames))
        return variant
    }
}