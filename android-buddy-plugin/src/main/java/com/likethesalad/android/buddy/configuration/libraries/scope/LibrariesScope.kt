package com.likethesalad.android.buddy.configuration.libraries.scope

sealed class LibrariesScope {
    object UseAll : LibrariesScope()
    object IgnoreAll : LibrariesScope()
    data class UseOnly(val libraryIds: Set<String>) : LibrariesScope()
}
