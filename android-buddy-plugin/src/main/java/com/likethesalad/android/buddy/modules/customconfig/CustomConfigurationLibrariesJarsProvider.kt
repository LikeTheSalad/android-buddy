package com.likethesalad.android.buddy.modules.customconfig

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.utils.AndroidVariantDataProvider
import java.io.File

@AutoFactory
class CustomConfigurationLibrariesJarsProvider(
    @Provided customConfigurationResolverFactory: CustomConfigurationResolverFactory,
    androidVariantDataProvider: AndroidVariantDataProvider
) : LibrariesJarsProvider {

    private val customConfigurationResolver by lazy {
        customConfigurationResolverFactory.create(androidVariantDataProvider)
    }

    override fun getLibrariesJars(): Set<File> {
        val implementations = customConfigurationResolver.getImplementationConfiguration()
        val apis = customConfigurationResolver.getApiConfiguration()

        return apis.plus(implementations).files
    }
}