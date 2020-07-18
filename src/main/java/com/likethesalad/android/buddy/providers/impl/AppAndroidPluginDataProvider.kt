package com.likethesalad.android.buddy.providers.impl

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.providers.AndroidPluginDataProvider
import com.likethesalad.android.common.utils.Logger
import org.gradle.api.JavaVersion
import java.io.File

@Suppress("UnstableApiUsage")
@AutoFactory
class AppAndroidPluginDataProvider(
    private val appExtension: AppExtension,
    private val logger: Logger
) : AndroidPluginDataProvider {

    override fun getBootClasspath(): Set<File> {
        return appExtension.bootClasspath.toSet()
    }

    override fun getJavaTargetCompatibilityVersion(variantName: String): Int {
        val variant = getVariantByName(variantName)
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

    override fun getJavaClassPath(variantName: String): Set<File> {
        val variant = getVariantByName(variantName) ?: throw IllegalArgumentException()
        return variant.javaCompileProvider.get().classpath.files
    }

    private fun getLocalJvmTargetCompatibility(): Int {
        return javaVersionToInt(JavaVersion.current())
    }

    private fun javaVersionToInt(javaVersion: JavaVersion): Int {
        return javaVersion.majorVersion.toInt()
    }

    private fun getVariantByName(name: String): ApplicationVariant? {
        return appExtension.applicationVariants.find {
            it.name == name
        }
    }
}