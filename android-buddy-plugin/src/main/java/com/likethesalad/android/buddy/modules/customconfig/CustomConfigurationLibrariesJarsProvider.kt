package com.likethesalad.android.buddy.modules.customconfig

import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.utils.AndroidVariantDataProvider
import com.likethesalad.android.common.utils.Logger
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.FileCollection
import java.io.File

@Suppress("UnstableApiUsage")
@AutoFactory
class CustomConfigurationLibrariesJarsProvider(
    @Provided customConfigurationResolverFactory: CustomConfigurationResolverFactory,
    @Provided private val logger: Logger,
    private val androidVariantDataProvider: AndroidVariantDataProvider
) : LibrariesJarsProvider {

    companion object {
        private val ANDROID_ARTIFACT_TYPE = Attribute.of("artifactType", String::class.java)
    }

    private val customConfigurationResolver by lazy {
        customConfigurationResolverFactory.create(androidVariantDataProvider)
    }

    override fun getLibrariesJars(): Set<File> {
        val customRuntimeConfiguration = customConfigurationResolver.getAndroidBuddyRuntimeConfiguration()
        val customCompileConfiguration = customConfigurationResolver.getAndroidBuddyCompileConfiguration()

        val compileFiles = extractArtifactFiles(customCompileConfiguration)
        val runtimeFiles = extractArtifactFiles(customRuntimeConfiguration)

        val mergedFiles = compileFiles.plus(runtimeFiles).files

        logger.debug("Library files from custom config: {}", mergedFiles)

        return mergedFiles
    }

    private fun extractArtifactFiles(custom: Configuration): FileCollection {
        return custom.incoming.artifactView {
            it.lenient(true)
            it.attributes.attribute(ANDROID_ARTIFACT_TYPE, AndroidArtifacts.ArtifactType.CLASSES.type)
        }.artifacts.artifactFiles
    }
}