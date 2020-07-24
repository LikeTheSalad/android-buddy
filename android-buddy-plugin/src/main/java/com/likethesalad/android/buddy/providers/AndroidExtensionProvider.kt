package com.likethesalad.android.buddy.providers

import com.android.build.gradle.BaseExtension

interface AndroidExtensionProvider {

    fun getAndroidExtension(): BaseExtension
}