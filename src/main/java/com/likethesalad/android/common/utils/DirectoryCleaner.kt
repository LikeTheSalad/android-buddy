package com.likethesalad.android.common.utils

import java.io.File

class DirectoryCleaner {

    fun cleanDirectory(directory: File) {
        directory.listFiles()?.forEach {
            if (it.isDirectory) {
                cleanDirectory(it)
            }
            it.delete()
        }
    }
}