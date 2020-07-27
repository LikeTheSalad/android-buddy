package com.likethesalad.android.buddy.modules.customconfig

import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.utils.AndroidVariantDataProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.gradle.api.file.FileCollection
import java.io.File

@Suppress("UnstableApiUsage")
@AutoFactory
class CustomConfigurationLibrariesJarsProvider(
    @Provided customConfigurationResolverFactory: CustomConfigurationResolverFactory,
    androidVariantDataProvider: AndroidVariantDataProvider
) : LibrariesJarsProvider {

    companion object {
        private val ANDROID_ARTIFACT_TYPE = Attribute.of("artifactType", String::class.java)
    }

    private val customConfigurationResolver by lazy {
        customConfigurationResolverFactory.create(androidVariantDataProvider)
    }

    override fun getLibrariesJars(): Set<File> {
        val implementations = customConfigurationResolver.getImplementationConfiguration()
        val apis = customConfigurationResolver.getApiConfiguration()

        val apiFiles = extractArtifactFiles(apis)
        val implementationFiles = extractArtifactFiles(implementations)

        return apiFiles.plus(implementationFiles).files
    }

    private fun extractArtifactFiles(configuration: Configuration): FileCollection {
        return configuration.incoming.artifactView {
            it.lenient(false)
            it.attributes.attribute(ANDROID_ARTIFACT_TYPE, AndroidArtifacts.ArtifactType.CLASSES.type)
        }.artifacts.artifactFiles
    }
}