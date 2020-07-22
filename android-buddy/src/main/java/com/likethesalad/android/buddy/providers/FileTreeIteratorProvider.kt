package com.likethesalad.android.buddy.providers

import java.io.File

interface FileTreeIteratorProvider {
    fun createFileTreeIterator(folder: File): Iterator<File>
}