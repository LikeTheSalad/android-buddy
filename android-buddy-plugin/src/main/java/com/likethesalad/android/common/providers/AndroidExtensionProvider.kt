package com.likethesalad.android.common.providers

import com.android.build.gradle.BaseExtension

interface AndroidExtensionProvider {

    fun getAndroidExtension(): BaseExtension
}