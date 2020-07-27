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
    private val variant: BaseVariant? by lazy {
        getVariantByName(variantName)
    }

    fun getBootClasspath(): Set<File> {
        return androidExtension.bootClasspath.toSet()
    }

    fun getJavaTargetCompatibilityVersion(): Int {
        val variant = this.variant
        val javaVersion = if (variant == null) {
            logger.w("Java target version for android variant {} not found, falling back to JVM's", variantName)
            getLocalJvmTargetCompatibility()
        } else {
            val targetCompatibilityStr = variant.javaCompileProvider.get().targetCompatibility
            javaVersionToInt(JavaVersion.toVersion(targetCompatibilityStr))
        }

        logger.i("Using java target version {}", javaVersion)
        return javaVersion
    }

    fun getJavaClassPath(): Set<File> {
        val variant = this.variant
        if (variant == null) {
            logger.w("Could not find variant '{}' - returning empty java classpath", variantName)
            return emptySet()
        }
        return variant.javaCompileProvider.get().classpath.files
    }

    fun getVariantPath(): List<String> {
        val variant = this.variant
        if (variant == null) {
            logger.w("Could not find variant path for {}, returning empty", variantName)
            return emptyList()
        }
        val resolver = androidVariantPathResolverFactory.create(
            variantName, variant.flavorName, variant.buildType.name, variant.productFlavors.map { it.name }
        )

        return resolver.getTopBottomPath()
    }

    fun getVariantBuildTypeName(): String {
        val variant = this.variant
        if (variant == null) {
            logger.w("Could not find build type name for variant {}, returning empty", variantName)
            return ""
        }
        return variant.buildType.name
    }

    private fun getLocalJvmTargetCompatibility(): Int {
        return javaVersionToInt(JavaVersion.current())
    }

    private fun javaVersionToInt(javaVersion: JavaVersion): Int {
        return javaVersion.majorVersion.toInt()
    }

    private fun getVariantByName(name: String): BaseVariant? {
        val androidExtension = this.androidExtension
        if (androidExtension is AppExtension) {
            return androidExtension.applicationVariants.find {
                it.name == name
            }
        }

        throw UnsupportedOperationException()
    }
}