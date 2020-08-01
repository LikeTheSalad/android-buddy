package com.likethesalad.android.buddy.utils

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.providers.AndroidExtensionProvider
import java.io.File
import javax.inject.Inject

@AppScope
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
}

