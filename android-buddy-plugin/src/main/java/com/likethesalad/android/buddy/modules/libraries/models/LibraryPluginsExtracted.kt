package com.likethesalad.android.buddy.modules.libraries.models

import java.io.File

data class LibraryPluginsExtracted(
    val pluginNames: Set<String>,
    val jarsContainingPlugins: List<File>,
    val extraJars: List<File>
) {
    companion object {
        val EMPTY = LibraryPluginsExtracted(emptySet(), emptyList(), emptyList())
    }
}
