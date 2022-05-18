package com.likethesalad.android.buddy.providers.impl

import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.io.File

class DefaultLibrariesJarsProvider @AssistedInject
constructor(@Assisted private val jarFiles: Set<File>) : LibrariesJarsProvider {

    @AssistedFactory
    interface Factory {
        fun create(jarFiles: Set<File>): DefaultLibrariesJarsProvider
    }

    override fun getLibrariesJars(): Set<File> = jarFiles
}