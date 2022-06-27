package com.likethesalad.android.common.utils.android

import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.common.utils.Logger
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.gradle.api.JavaVersion

@Suppress("UnstableApiUsage")
class AndroidVariantDataProvider @AssistedInject constructor(
    private val androidExtension: AndroidExtensionDataProvider,
    private val logger: Logger,
    @Assisted val variantName: String
) {

    @AssistedFactory
    interface Factory {
        fun create(variantName: String): AndroidVariantDataProvider
    }

    private val variant: BaseVariant by lazy {
        androidExtension.getVariantByName(variantName)
    }

    fun getJavaTargetCompatibilityVersion(): Int {
        val targetCompatibilityStr = variant.javaCompileProvider.get().targetCompatibility
        val javaVersion = javaVersionToInt(JavaVersion.toVersion(targetCompatibilityStr))

        logger.info("Using java target version {}", javaVersion)
        return javaVersion
    }

    private fun javaVersionToInt(javaVersion: JavaVersion): Int {
        return javaVersion.majorVersion.toInt()
    }
}