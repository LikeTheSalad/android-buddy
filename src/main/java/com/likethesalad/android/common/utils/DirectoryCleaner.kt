package com.likethesalad.android.common.utils

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DirectoryCleaner @Inject constructor() {

    fun cleanDirectory(directory: File) {
        directory.listFiles()?.forEach {
            if (it.isDirectory) {
                cleanDirectory(it)
            }
            it.delete()
        }
    }
}