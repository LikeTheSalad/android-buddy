package com.likethesalad.android.buddy.modules.transform.utils

import com.likethesalad.android.buddy.di.AppScope
import java.io.File
import javax.inject.Inject

@AppScope
class FilePathScanner @Inject constructor() {

    fun scanFilePath(path: String): FileInfo {
        val file = File(path)
        return FileInfo(file.name, file.parent)
    }

    data class FileInfo(val name: String, val dirPath: String)
}