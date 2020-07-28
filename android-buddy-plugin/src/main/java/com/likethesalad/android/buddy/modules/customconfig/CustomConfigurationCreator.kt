package com.likethesalad.android.buddy.modules.customconfig

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationsHolder
import com.likethesalad.android.buddy.modules.customconfig.utils.ConfigurationNamesGeneratorFactory
import com.likethesalad.android.buddy.modules.customconfig.utils.CustomConfigurationNamesGenerator
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.buddy.utils.AndroidExtensionDataProvider
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@AppScope
class CustomConfigurationCreator
@Inject constructor(
    private val gradleConfigurationsProvider: GradleConfigurationsProvider,
    private val configurationNamesGeneratorFactory: ConfigurationNamesGeneratorFactory,
    private val androidExtensionDataProvider: AndroidExtensionDataProvider
) {

    private val configurationContainer by lazy { gradleConfigurationsProvider.getConfigurationContainer() }

    companion object {
        private val AS_BUCKET = Action<Configuration> {
            it.isCanBeResolved = false
            it.isCanBeConsumed = false
        }
        private val AS_RESOLVABLE = Action<Configuration> {
            it.isCanBeConsumed = false
            it.isCanBeResolved = true
        }
    }

    fun createAndroidBuddyConfigurations() {
        val mainApiBucket = createConfigurationsFor(ConfigurationGroup.COMPILE_GROUP).bucket
        val mainImplementationBucket = createConfigurationsFor(ConfigurationGroup.RUNTIME_GROUP).bucket
        val buildTypeNames = androidExtensionDataProvider.getBuildTypeNames()

        buildTypeNames.forEach { name ->
            createConfigurationsFor(ConfigurationGroup.COMPILE_GROUP, name, mainApiBucket)
            createConfigurationsFor(ConfigurationGroup.RUNTIME_GROUP, name, mainImplementationBucket)
        }
    }

    private fun createConfigurationsFor(
        configurationGroup: ConfigurationGroup,
        buildTypeName: String = "",
        parentBucket: Configuration? = null
    ): ConfigurationsHolder {
        val generator = configurationNamesGeneratorFactory.create(configurationGroup, buildTypeName)
        val bucket = createBucket(generator.getAndroidBuddyBucketName())
        val resolvable = if (buildTypeName.isNotEmpty()) {
            createResolvable(generator.getAndroidBuddyResolvableName())
        } else {
            null
        }

        if (parentBucket != null) {
            bucket.extendsFrom(parentBucket)
        }
        resolvable?.extendsFrom(bucket)

        attachToAndroidConfigurations(bucket, generator)
        return ConfigurationsHolder(configurationGroup, bucket, resolvable)
    }

    private fun attachToAndroidConfigurations(
        customBucket: Configuration,
        namesGeneratorCustom: CustomConfigurationNamesGenerator
    ) {
        configurationContainer.named(namesGeneratorCustom.getAndroidBucketName()) {
            it.extendsFrom(customBucket)
        }
    }

    private fun createBucket(name: String): Configuration {
        return configurationContainer.create(name, AS_BUCKET)
    }

    private fun createResolvable(name: String): Configuration {
        return configurationContainer.create(name, AS_RESOLVABLE).apply {
            isTransitive = false
        }
    }
}