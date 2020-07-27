package com.likethesalad.android.buddy.modules.customconfig

import com.android.build.api.attributes.BuildTypeAttr
import com.android.build.api.attributes.VariantAttr
import com.android.build.gradle.internal.dependency.AndroidTypeAttr
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationType
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationsHolder
import com.likethesalad.android.buddy.modules.customconfig.utils.ConfigurationNamesGeneratorFactory
import com.likethesalad.android.buddy.providers.AndroidBuildTypeNamesProvider
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.buddy.providers.ObjectFactoryProvider
import org.gradle.api.Action
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Usage
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@AppScope
class CustomConfigurationCreator
@Inject constructor(
    private val gradleConfigurationsProvider: GradleConfigurationsProvider,
    private val configurationNamesGeneratorFactory: ConfigurationNamesGeneratorFactory,
    private val androidBuildTypeNamesProvider: AndroidBuildTypeNamesProvider,
    objectFactoryProvider: ObjectFactoryProvider
) {

    private val configurationContainer by lazy { gradleConfigurationsProvider.getConfigurationContainer() }
    private val objectFactory by lazy { objectFactoryProvider.getObjectFactory() }

    companion object {
        private val AS_CONSUMABLE = Action<Configuration> {
            it.isCanBeResolved = false
            it.isCanBeConsumed = true
        }
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
        val mainApiNamesGenerator = configurationNamesGeneratorFactory.create(
            ConfigurationGroup.API_GROUP,
            ""
        )
        val mainImplementationNamesGenerator = configurationNamesGeneratorFactory.create(
            ConfigurationGroup.RUNTIME_GROUP,
            ""
        )
        val mainApiBucket = createBucket(mainApiNamesGenerator.getBucketName())
        val mainImplementationBucket = createBucket(mainImplementationNamesGenerator.getBucketName())
        val buildTypeNames = androidBuildTypeNamesProvider.getBuildTypeNames()

        buildTypeNames.forEach { name ->
            createConfigurationsForBuildType(name, ConfigurationGroup.API_GROUP, mainApiBucket)
            createConfigurationsForBuildType(name, ConfigurationGroup.RUNTIME_GROUP, mainImplementationBucket)
        }

        attachToAndroidConfigurations(mainApiBucket, mainImplementationBucket)
    }

    private fun attachToAndroidConfigurations(apiConfig: Configuration, implementationConfig: Configuration) {
        configurationContainer.named(ConfigurationType.API.configurationName) {
            it.extendsFrom(apiConfig)
        }
        configurationContainer.named(ConfigurationType.IMPLEMENTATION.configurationName) {
            it.extendsFrom(implementationConfig)
        }
    }

    private fun createConfigurationsForBuildType(
        buildTypeName: String,
        configurationGroup: ConfigurationGroup,
        mainBucket: Configuration
    ) {
        val buildTypeConfigs = createConfigurationsFor(buildTypeName, configurationGroup)
        buildTypeConfigs.bucket.extendsFrom(mainBucket)
    }

    private fun createConfigurationsFor(
        buildTypeName: String,
        configurationGroup: ConfigurationGroup
    ): ConfigurationsHolder {
        val generator = configurationNamesGeneratorFactory.create(configurationGroup, buildTypeName)
        val bucket = createBucket(generator.getBucketName())
        val consumable = createConsumable(generator.getConsumableName())
        val resolvable = createResolvable(generator.getResolvableName())

        val usage = getJavaUsage(configurationGroup)
        applyCommonAttributes(consumable, buildTypeName, usage)
        applyCommonAttributes(resolvable, buildTypeName, usage)

        consumable.extendsFrom(bucket)
        resolvable.extendsFrom(consumable)

        return ConfigurationsHolder(configurationGroup, bucket, consumable, resolvable)
    }

    private fun getJavaUsage(configurationGroup: ConfigurationGroup): Usage {
        val usageValue = when (configurationGroup) {
            ConfigurationGroup.RUNTIME_GROUP -> Usage.JAVA_RUNTIME
            ConfigurationGroup.API_GROUP -> Usage.JAVA_API
        }

        return objectFactory.named(Usage::class.java, usageValue)
    }

    private fun createBucket(name: String): Configuration {
        return configurationContainer.create(name, AS_BUCKET)
    }

    private fun createConsumable(name: String): Configuration {
        return configurationContainer.create(name, AS_CONSUMABLE)
    }

    private fun createResolvable(name: String): Configuration {
        return configurationContainer.create(name, AS_RESOLVABLE)
    }

    private fun applyCommonAttributes(
        configuration: Configuration, buildTypeName: String, usage: Usage
    ) {
        val attributes = configuration.attributes
        attributes.attribute(Usage.USAGE_ATTRIBUTE, usage)
        attributes.attribute(
            BuildTypeAttr.ATTRIBUTE,
            objectFactory.named(BuildTypeAttr::class.java, buildTypeName)
        )
        attributes.attribute(
            VariantAttr.ATTRIBUTE,
            objectFactory.named(VariantAttr::class.java, buildTypeName)
        )
        attributes.attribute(
            AndroidTypeAttr.ATTRIBUTE,
            objectFactory.named(AndroidTypeAttr::class.java, AndroidTypeAttr.AAR)
        )
        attributes.attribute(
            AndroidArtifacts.ARTIFACT_TYPE,
            AndroidArtifacts.ArtifactType.CLASSES.type
        )
        attributes.attribute(
            KotlinPlatformType.attribute,
            KotlinPlatformType.androidJvm
        )
    }
}