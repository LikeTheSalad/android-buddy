package com.likethesalad.android.buddy.modules.libraries.models

import java.io.File

data class LibraryPluginsExtracted(
    val pluginNames: Set<String>,
    val jarsContainingPlugins: Set<File>
) {
    companion object {
        val EMPTY = LibraryPluginsExtracted(emptySet(), emptySet())
    }
}
