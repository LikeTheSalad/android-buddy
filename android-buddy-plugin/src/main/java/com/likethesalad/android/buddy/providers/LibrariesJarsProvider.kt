package com.likethesalad.android.buddy.providers

import java.io.File

interface LibrariesJarsProvider {

    fun getLibrariesJars(): Set<File>
}