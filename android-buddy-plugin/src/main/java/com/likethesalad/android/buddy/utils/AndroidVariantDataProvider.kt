package com.likethesalad.android.buddy.utils

import com.android.build.gradle.api.BaseVariant
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.modules.customconfig.utils.AndroidVariantPathResolverFactory
import com.likethesalad.android.common.utils.Logger
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.Configuration
import java.io.File

@Suppress("UnstableApiUsage")
@AutoFactory
class AndroidVariantDataProvider(
    @Provided private val androidExtension: AndroidExtensionDataProvider,
    @Provided private val androidVariantPathResolverFactory: AndroidVariantPathResolverFactory,
    @Provided private val logger: Logger,
    val variantName: String
) {

    private val variant: BaseVariant by lazy {
        androidExtension.getVariantByName(variantName)
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
}