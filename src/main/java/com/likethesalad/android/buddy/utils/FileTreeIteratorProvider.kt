package com.likethesalad.android.buddy.utils

import java.io.File

interface FileTreeIteratorProvider {
    fun createFileTreeIterator(folder: File): Iterator<File>
}