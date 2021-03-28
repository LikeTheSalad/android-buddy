package com.likethesalad.android.common.models.libinfo

data class AndroidBuddyLibraryInfo(
    val id: String,
    val group: String,
    val name: String,
    val version: String,
    val pluginNames: Set<String>
)
