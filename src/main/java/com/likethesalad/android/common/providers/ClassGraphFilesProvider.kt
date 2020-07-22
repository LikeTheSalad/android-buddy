package com.likethesalad.android.common.providers

import java.io.File

interface ClassGraphFilesProvider {
    fun provideFiles(): Set<File>
}