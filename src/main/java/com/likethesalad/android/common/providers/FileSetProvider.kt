package com.likethesalad.android.common.providers

import java.io.File

interface FileSetProvider {
    fun provideFiles(): Set<File>
}