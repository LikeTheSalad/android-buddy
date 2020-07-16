package com.likethesalad.android.buddy.providers.impl

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.providers.AndroidPluginDataProvider
import org.gradle.api.JavaVersion
import java.io.File

@Suppress("UnstableApiUsage")
@AutoFactory
class AppAndroidPluginDataProvider(private val appExtension: AppExtension) : AndroidPluginDataProvider {

    override fun getBootClasspath(): Set<File> {
        return appExtension.bootClasspath.toSet()
    }

    override fun getJavaTargetCompatibilityVersion(variantName: String): Int {
        val variant = getVariantByName(variantName) ?: return getLocalJvmTargetCompatibility()

        val targetCompatibilityStr = variant.javaCompileProvider.get().targetCompatibility

        return javaVersionToInt(JavaVersion.toVersion(targetCompatibilityStr))
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