package com.likethesalad.android.common.utils.android

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.common.providers.AndroidExtensionProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidExtensionDataProvider
@Inject constructor(androidExtensionProvider: AndroidExtensionProvider) {

    private val androidExtension by lazy { androidExtensionProvider.getAndroidExtension() }

    fun getBootClasspath(): List<File> {
        return androidExtension.bootClasspath
    }

    fun getVariantByName(name: String): BaseVariant {
        return when (val extension = this.androidExtension) {
            is AppExtension -> getAppVariantByName(name, extension)
            else -> throw UnsupportedOperationException()
        }
    }

    fun allVariants(onVariantFound: (BaseVariant) -> Unit) {
        when (val extension = androidExtension) {
            is AppExtension -> allApplicationVariants(extension, onVariantFound)
            is LibraryExtension -> allLibraryVariants(extension, onVariantFound)
            else -> throw UnsupportedOperationException()
        }
    }

    private fun getAppVariantByName(name: String, appExtension: AppExtension): ApplicationVariant {
        return appExtension.applicationVariants.find {
            it.name == name
        }!!
    }

    private fun allApplicationVariants(
        appExtension: AppExtension,
        onVariantFound: (BaseVariant) -> Unit
    ) {
        appExtension.applicationVariants.all {
            onVariantFound.invoke(it)
        }
    }

    private fun allLibraryVariants(
        libExtension: LibraryExtension,
        onVariantFound: (BaseVariant) -> Unit
    ) {
        libExtension.libraryVariants.all {
            onVariantFound.invoke(it)
        }
    }
}

