package com.likethesalad.android.common.providers.impl

import com.likethesalad.android.common.providers.FileSetProvider
import java.io.File

class DefaultFileSetProvider(private val files: Set<File>) : FileSetProvider {

    override fun provideFiles(): Set<File> = files
}