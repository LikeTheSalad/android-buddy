package com.likethesalad.android.buddy.utils

import com.google.auto.factory.AutoFactory
import java.io.File

@AutoFactory
class FilesHolder(val allFiles: Set<File>) {

    val dirs: Set<File> by lazy {
        allFiles.filter { it.isDirectory }.toSet()
    }

    val jarFiles: Set<File> by lazy {
        allFiles.filter { it.isFile }.toSet()
    }
}