package com.likethesalad.android.buddy.providers.impl

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.utils.CustomConfigurationResolverFactory
import com.likethesalad.android.buddy.utils.android.AndroidPluginDataProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import java.io.File

@AutoFactory
class CustomConfigurationLibrariesJarsProvider(
    @Provided customConfigurationResolverFactory: CustomConfigurationResolverFactory,
    androidPluginDataProvider: AndroidPluginDataProvider
) : LibrariesJarsProvider {

    private val customConfigurationResolver by lazy {
        customConfigurationResolverFactory.create(androidPluginDataProvider)
    }

    override fun getLibrariesJars(): Set<File> {
        val implementations = customConfigurationResolver.getImplementationConfigurations()
        val apis = customConfigurationResolver.getApiConfigurations()
        val allImplementationsFileCollection = mergeConfigurationFiles(implementations)
        val allApisFileCollection = mergeConfigurationFiles(apis)

        return allApisFileCollection.plus(allImplementationsFileCollection).files
    }

    private fun mergeConfigurationFiles(configurations: List<Configuration>): FileCollection {
        val first = configurations.first() as FileCollection
        return configurations.drop(1).foldRight(first) { currentItem, accumulated ->
            accumulated.plus(currentItem)
        }
    }
}