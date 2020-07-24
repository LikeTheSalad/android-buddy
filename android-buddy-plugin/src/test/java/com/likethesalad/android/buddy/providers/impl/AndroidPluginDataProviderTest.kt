package com.likethesalad.android.buddy.providers.impl

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.model.BuildType
import com.android.builder.model.ProductFlavor
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.providers.AndroidExtensionProvider
import com.likethesalad.android.buddy.utils.android.AndroidPluginDataProvider
import com.likethesalad.android.buddy.utils.android.AndroidVariantPathResolver
import com.likethesalad.android.buddy.utils.android.AndroidVariantPathResolverFactory
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

class AndroidPluginDataProviderTest : BaseMockable() {

    @MockK
    lateinit var androidAppExtension: AppExtension

    @MockK
    lateinit var androidExtensionProvider: AndroidExtensionProvider

    @MockK
    lateinit var androidVariantPathResolverFactory: AndroidVariantPathResolverFactory

    @MockK
    lateinit var logger: Logger

    private val variantName = "someVariantName"
    private lateinit var androidPluginDataProvider: AndroidPluginDataProvider

    @Before
    fun setUp() {
        every { androidExtensionProvider.getAndroidExtension() }.returns(androidAppExtension)
        androidPluginDataProvider =
            AndroidPluginDataProvider(
                androidExtensionProvider,
                androidVariantPathResolverFactory,
                logger,
                variantName
            )
    }

    @Test
    fun `Get android booth classpath`() {
        val bootClasspath = mutableListOf<File>(mockk())
        every {
            androidAppExtension.bootClasspath
        }.returns(bootClasspath)

        Truth.assertThat(androidPluginDataProvider.getBootClasspath()).isEqualTo(bootClasspath.toSet())
    }

    @Test
    fun `Get variant java target compatibility version`() {
        val javaCompileProvider = mockk<TaskProvider<JavaCompile>>()
        val javaCompile = mockk<JavaCompile>()
        val targetCompatibility = "1.7"
        val variant = createAndSetMainVariantMock("otherName")
        every { javaCompileProvider.hint(JavaCompile::class).get() }.returns(javaCompile)
        every { javaCompile.targetCompatibility }.returns(targetCompatibility)
        every { variant.javaCompileProvider }.returns(javaCompileProvider)

        val result = androidPluginDataProvider.getJavaTargetCompatibilityVersion()

        Truth.assertThat(result).isEqualTo(7)
        verify {
            logger.i("Using java target version {}", 7)
        }
    }

    @Test
    fun `Fallback to current JVM target compatibility version when variant not found`() {
        val appVariants = mockk<DomainObjectSet<ApplicationVariant>>()
        val currentJdkJavaVersion = JavaVersion.VERSION_1_7
        mockkStatic(JavaVersion::class)
        every { JavaVersion.current() }.returns(currentJdkJavaVersion)
        every { androidAppExtension.applicationVariants }.returns(appVariants)
        every { appVariants.iterator() }.returns(getVariantsIterator(null, "otherName"))

        val result = androidPluginDataProvider.getJavaTargetCompatibilityVersion()

        Truth.assertThat(result).isEqualTo(7)
        verify {
            logger.w("Java target version for android variant {} not found, falling back to JVM's", variantName)
            logger.i("Using java target version {}", 7)
        }
    }

    @Test
    fun `Get variant path`() {
        val variant = createAndSetMainVariantMock()
        val flavorName = "someFlavorName"
        val androidVariantPathResolver = mockk<AndroidVariantPathResolver>()
        val buildType = mockk<BuildType>()
        val buildTypeName = "someBuildTypeName"
        val flavor1 = mockk<ProductFlavor>()
        val flavor2 = mockk<ProductFlavor>()
        val flavorName1 = "someFlavor"
        val flavorName2 = "anotherFlavor"
        val flavors = listOf(flavor1, flavor2)
        val expectedPath = listOf("some", "path")
        every { flavor1.name }.returns(flavorName1)
        every { flavor2.name }.returns(flavorName2)
        every { variant.flavorName }.returns(flavorName)
        every { variant.buildType }.returns(buildType)
        every { buildType.name }.returns(buildTypeName)
        every { variant.productFlavors }.returns(flavors)
        every {
            androidVariantPathResolverFactory.create(
                variantName, flavorName, buildTypeName,
                listOf(flavorName1, flavorName2)
            )
        }.returns(androidVariantPathResolver)
        every { androidVariantPathResolver.getTopBottomPath() }.returns(expectedPath)

        val result = androidPluginDataProvider.getVariantPath()

        Truth.assertThat(result).isEqualTo(expectedPath)
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
        every { androidAppExtension.applicationVariants }.returns(appVariants)
        every { appVariants.iterator() }.returns(getVariantsIterator(variant, *otherVariantNames))
        return variant
    }
}