package com.likethesalad.android.buddy.modules.customconfig

import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.buddy.AndroidBuddyPluginConfiguration
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.utils.AndroidVariantPathResolverFactory
import com.likethesalad.android.buddy.modules.customconfig.utils.CustomConfigurationNamesGenerator
import com.likethesalad.android.buddy.modules.customconfig.utils.CustomConfigurationNamesGeneratorFactory
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.buddy.utils.AndroidExtensionDataProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.AttributeContainer
import javax.inject.Inject

@AppScope
class CustomConfigurationVariantSetup
@Inject constructor(
    private val androidExtensionDataProvider: AndroidExtensionDataProvider,
    private val androidVariantPathResolverFactory: AndroidVariantPathResolverFactory,
    private val customConfigurationNamesGeneratorFactory: CustomConfigurationNamesGeneratorFactory,
    private val configurationsProvider: GradleConfigurationsProvider,
    private val pluginConfiguration: AndroidBuddyPluginConfiguration
) {

    private val configurations by lazy {
        configurationsProvider.getConfigurationContainer()
    }

    fun arrangeConfigurationsPerVariant() {
        androidExtensionDataProvider.allVariants {
            val name = it.name
            val path = getVariantPath(it)

            setUpConfigurationGroup(name, path, ConfigurationGroup.COMPILE_GROUP, it.compileConfiguration)
            setUpConfigurationGroup(name, path, ConfigurationGroup.RUNTIME_GROUP, it.runtimeConfiguration)
        }
    }

    private fun setUpConfigurationGroup(
        variantName: String,
        variantPath: List<String>,
        group: ConfigurationGroup,
        androidResolvable: Configuration
    ) {
        val namesGenerator = getNamesGeneratorFor(group)
        val customBuckets = namesToConfigurations(namesGenerator.getSortedBucketConfigNames(variantPath))
        val customResolvableName = namesGenerator.getResolvableConfigurationName(variantName)
        val customResolvable = createResolvableConfiguration(customResolvableName)

        applyBucketsHierarchyOrder(customBuckets)
        customResolvable.extendsFrom(customBuckets.last())
        copyAttributes(androidResolvable.attributes, customResolvable.attributes)
    }

    private fun applyBucketsHierarchyOrder(sortedConfigurations: List<Configuration>) {
        var last: Configuration? = null
        sortedConfigurations.forEach {
            if (last != null) {
                it.extendsFrom(last)
            }
            last = it
        }
    }

    private fun getNamesGeneratorFor(group: ConfigurationGroup): CustomConfigurationNamesGenerator {
        return customConfigurationNamesGeneratorFactory.create(group)
    }

    private fun getVariantPath(variant: BaseVariant): List<String> {
        val resolver = androidVariantPathResolverFactory.create(variant.name,
            variant.flavorName, variant.buildType.name, variant.productFlavors.map { it.name })
        return resolver.getTopBottomPath()
    }

    private fun createResolvableConfiguration(name: String): Configuration {
        return configurations.create(name) {
            it.isCanBeResolved = true
            it.isCanBeConsumed = false
            it.isTransitive = pluginConfiguration.useDependenciesTransitiveTransformations()
        }
    }

    private fun namesToConfigurations(names: List<String>): List<Configuration> {
        val foundConfigurations = mutableListOf<Configuration>()
        names.forEach {
            configurations.findByName(it)?.let { config ->
                foundConfigurations.add(config)
            }
        }
        return foundConfigurations
    }

    @Suppress("UNCHECKED_CAST", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun copyAttributes(from: AttributeContainer, to: AttributeContainer) {
        from.keySet().forEach { key ->
            to.attribute(key as Attribute<Any>, from.getAttribute(key))
        }
    }
}