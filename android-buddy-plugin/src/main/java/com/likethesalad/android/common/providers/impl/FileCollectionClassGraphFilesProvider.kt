package com.likethesalad.android.common.providers.impl

import com.likethesalad.android.common.providers.ClassGraphFilesProvider
import org.gradle.api.file.FileCollection
import java.io.File

class FileCollectionClassGraphFilesProvider(private val fileCollection: FileCollection) : ClassGraphFilesProvider {

    override fun provideFiles(): Set<File> = fileCollection.files
}