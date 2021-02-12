package com.likethesalad.android.buddy.configuration.libraries

sealed class LibrariesPolicy {
    object UseAll : LibrariesPolicy()
    object IgnoreAll : LibrariesPolicy()
    data class UseOnly(val libraryIds: Set<String>) : LibrariesPolicy()
}
