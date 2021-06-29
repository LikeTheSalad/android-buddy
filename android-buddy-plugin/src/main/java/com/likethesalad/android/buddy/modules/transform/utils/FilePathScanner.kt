package com.likethesalad.android.buddy.modules.transform.utils

import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilePathScanner @Inject constructor() {

    fun scanFilePath(path: String): FileInfo {
        val file = File(path)
        return FileInfo(file.name, file.parent)
    }

    data class FileInfo(val name: String, val dirPath: String)
}