package com.likethesalad.android.buddy.providers

import java.io.File

interface AndroidPluginDataProvider {

    fun getBootClasspath(): Set<File>

    fun getTargetCompatibility(variantName: String): Int
}