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
        println("merged files: $mergedFiles")

        return mergedFiles
    }

    private fun extractArtifactFiles(custom: Configuration): FileCollection {
        return custom.incoming.artifactView {
            it.lenient(true)
            it.attributes.attribute(ANDROID_ARTIFACT_TYPE, AndroidArtifacts.ArtifactType.CLASSES.type)
        }.artifacts.artifactFiles
    }
}