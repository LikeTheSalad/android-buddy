package com.likethesalad.android.buddy.utils

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.modules.customconfig.AndroidVariantPathResolverFactory
import com.likethesalad.android.buddy.providers.AndroidExtensionProvider
import com.likethesalad.android.common.utils.Logger
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.Configuration
import java.io.File

@Suppress("UnstableApiUsage")
@AutoFactory
class AndroidVariantDataProvider(
    @Provided private val androidExtensionProvider: AndroidExtensionProvider,
    @Provided private val androidVariantPathResolverFactory: AndroidVariantPathResolverFactory,
    @Provided private val logger: Logger,
    private val variantName: String
) {

    private val androidExtension: BaseExtension by lazy {
        androidExtensionProvider.getAndroidExtension()
    }
    private val variant: BaseVariant by lazy {
        getVariantByName(variantName)
    }

    fun getBootClasspath(): Set<File> {
        return androidExtension.bootClasspath.toSet()
    }

    fun getJavaTargetCompatibilityVersion(): Int {
        val targetCompatibilityStr = variant.javaCompileProvider.get().targetCompatibility
        val javaVersion = javaVersionToInt(JavaVersion.toVersion(targetCompatibilityStr))

        logger.i("Using java target version {}", javaVersion)
        return javaVersion
    }

    fun getJavaClassPath(): Set<File> {
        return variant.javaCompileProvider.get().classpath.files
    }

    fun getVariantPath(): List<String> {
        val resolver = androidVariantPathResolverFactory.create(
            variantName, variant.flavorName, variant.buildType.name, variant.productFlavors.map { it.name }
        )

        return resolver.getTopBottomPath()
    }

    fun getVariantBuildTypeName(): String {
        return variant.buildType.name
    }

    fun getVariantCompileConfiguration(): Configuration {
        return variant.compileConfiguration
    }

    fun getVariantRuntimeConfiguration(): Configuration {
        return variant.runtimeConfiguration
    }

    private fun javaVersionToInt(javaVersion: JavaVersion): Int {
        return javaVersion.majorVersion.toInt()
    }

    private fun getVariantByName(name: String): BaseVariant {
        val androidExtension = this.androidExtension
        if (androidExtension is AppExtension) {
            return androidExtension.applicationVariants.find {
                it.name == name
            }!!
        }

        throw UnsupportedOperationException()
    }
}