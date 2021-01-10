package com.likethesalad.android.common.utils.android

import com.android.build.gradle.api.BaseVariant
import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.common.utils.Logger
import org.gradle.api.JavaVersion

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

        logger.info("Using java target version {}", javaVersion)
        return javaVersion
    }

    fun getVariantPath(): List<String> {
        val resolver = androidVariantPathResolverFactory.create(
            variantName, variant.flavorName, variant.buildType.name, variant.productFlavors.map { it.name }
        )

        return resolver.getTopBottomPath()
    }

    private fun javaVersionToInt(javaVersion: JavaVersion): Int {
        return javaVersion.majorVersion.toInt()
    }
}