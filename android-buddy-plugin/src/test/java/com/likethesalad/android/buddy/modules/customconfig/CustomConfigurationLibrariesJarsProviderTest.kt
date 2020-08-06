package com.likethesalad.android.buddy.modules.customconfig

import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.common.utils.android.AndroidVariantDataProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.Action
import org.gradle.api.artifacts.ArtifactCollection
import org.gradle.api.artifacts.ArtifactView
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.file.FileCollection
import org.junit.Before
import org.junit.Test
import java.io.File

class CustomConfigurationLibrariesJarsProviderTest : BaseMockable() {

    @MockK
    lateinit var customConfigurationResolverFactory: CustomConfigurationResolverFactory

    @MockK
    lateinit var customConfigurationResolver: CustomConfigurationResolver

    @MockK
    lateinit var androidVariantDataProvider: AndroidVariantDataProvider

    @MockK
    lateinit var logger: Logger

    private lateinit var customConfigurationLibrariesJarsProvider: CustomConfigurationLibrariesJarsProvider

    @Before
    fun setUp() {
        every {
            customConfigurationResolverFactory.create(androidVariantDataProvider)
        }.returns(customConfigurationResolver)
        customConfigurationLibrariesJarsProvider =
            CustomConfigurationLibrariesJarsProvider(
                customConfigurationResolverFactory,
                logger,
                androidVariantDataProvider
            )
    }

    @Test
    fun `Provide concatenated files`() {
        val compileFileCollection = mockk<FileCollection>()
        val runtimeFileCollection = mockk<FileCollection>()
        val compileResolvable = createResolvableCustomConfig(compileFileCollection)
        val runtimeResolvable = createResolvableCustomConfig(runtimeFileCollection)
        val mergedConfigurations = mockk<FileCollection>()
        val file1 = mockk<File>()
        val file2 = mockk<File>()
        val file3 = mockk<File>()
        val file4 = mockk<File>()
        val allFiles = setOf(file1, file2, file3, file4)
        every {
            customConfigurationResolver.getAndroidBuddyCompileConfiguration()
        }.returns(compileResolvable.configuration)
        every {
            customConfigurationResolver.getAndroidBuddyRuntimeConfiguration()
        }.returns(runtimeResolvable.configuration)
        every { compileFileCollection.plus(runtimeFileCollection) }.returns(mergedConfigurations)
        every { mergedConfigurations.files }.returns(allFiles)

        val result = customConfigurationLibrariesJarsProvider.getLibrariesJars()

        Truth.assertThat(result).containsExactlyElementsIn(allFiles)
        verifyArtifactViewConfiguration(compileResolvable)
        verifyArtifactViewConfiguration(runtimeResolvable)
        verify {
            compileFileCollection.plus(runtimeFileCollection)
            logger.debug("Library files from custom config: {}", allFiles)
        }
    }

    private fun verifyArtifactViewConfiguration(resolvableConfig: ResolvableConfig) {
        val config = mockk<ArtifactView.ViewConfiguration>()
        val attributeContainer = mockk<AttributeContainer>()
        val artifactType = Attribute.of("artifactType", String::class.java)
        every { config.attributes }.returns(attributeContainer)
        every { attributeContainer.attribute(any<Attribute<Any>>(), any<Any>()) }.returns(attributeContainer)
        every { config.lenient(any()) }.returns(config)

        resolvableConfig.viewConfigActionCaptor.captured.execute(config)

        verify {
            config.lenient(true)
            attributeContainer.attribute(artifactType, "android-classes")
        }
    }

    private fun createResolvableCustomConfig(fileCollection: FileCollection): ResolvableConfig {
        val configuration = mockk<Configuration>()
        val incoming = mockk<ResolvableDependencies>()
        val artifactView = mockk<ArtifactView>()
        val artifactCollection = mockk<ArtifactCollection>()
        val viewConfigActionCaptor = slot<Action<ArtifactView.ViewConfiguration>>()
        every { configuration.incoming }.returns(incoming)
        every { incoming.artifactView(capture(viewConfigActionCaptor)) }.returns(artifactView)
        every { artifactView.artifacts }.returns(artifactCollection)
        every { artifactCollection.artifactFiles }.returns(fileCollection)

        return ResolvableConfig(configuration, viewConfigActionCaptor)
    }

    class ResolvableConfig(
        val configuration: Configuration,
        val viewConfigActionCaptor: CapturingSlot<Action<ArtifactView.ViewConfiguration>>
    )
}