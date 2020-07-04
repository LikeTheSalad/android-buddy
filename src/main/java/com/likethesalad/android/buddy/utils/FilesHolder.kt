package com.likethesalad.android.buddy.utils

import com.google.auto.factory.AutoFactory
import java.io.File

@AutoFactory
class FilesHolder(val allFiles: Set<File>) {

    val files: Set<File> by lazy {
        allFiles.filter { it.isFile }.toSet()
    }

    val dirs: Set<File> by lazy {
        allFiles.filter { it.isDirectory }.toSet()
    }
}