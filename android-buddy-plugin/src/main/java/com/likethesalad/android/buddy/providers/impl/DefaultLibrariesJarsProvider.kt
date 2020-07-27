package com.likethesalad.android.buddy.providers.impl

import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import java.io.File

@AutoFactory
class DefaultLibrariesJarsProvider(private val jarFiles: Set<File>) : LibrariesJarsProvider {

    override fun getLibrariesJars(): Set<File> = jarFiles
}