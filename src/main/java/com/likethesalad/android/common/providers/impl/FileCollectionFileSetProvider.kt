package com.likethesalad.android.common.providers.impl

import com.likethesalad.android.common.providers.FileSetProvider
import org.gradle.api.file.FileCollection
import java.io.File

class FileCollectionFileSetProvider(private val fileCollection: FileCollection) : FileSetProvider {

    override fun provideFiles(): Set<File> = fileCollection.files
}