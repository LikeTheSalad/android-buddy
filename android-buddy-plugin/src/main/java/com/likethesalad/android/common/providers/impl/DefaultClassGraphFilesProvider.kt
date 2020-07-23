package com.likethesalad.android.common.providers.impl

import com.likethesalad.android.common.providers.ClassGraphFilesProvider
import java.io.File

class DefaultClassGraphFilesProvider(private val files: Set<File>) : ClassGraphFilesProvider {

    override fun provideFiles(): Set<File> = files
}