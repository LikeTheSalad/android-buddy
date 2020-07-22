package com.likethesalad.android.buddy.providers

import java.io.File

interface AndroidPluginDataProvider {

    fun getBootClasspath(): Set<File>

    fun getJavaTargetCompatibilityVersion(variantName: String): Int

    fun getJavaClassPath(variantName: String): Set<File>
}