package com.likethesalad.android.buddy.modules.transform.utils

import com.google.common.truth.Truth
import org.junit.Test

class FilePathScannerTest {

    private val filePathScanner = FilePathScanner()

    @Test
    fun `Get file info from absolute path`() {
        val path = "/home/user/file.txt"

        val info = filePathScanner.scanFilePath(path)

        Truth.assertThat(info.name).isEqualTo("file.txt")
        Truth.assertThat(info.dirPath).isEqualTo("/home/user")
    }

    @Test
    fun `Get file info from relative path`() {
        val path = "androidx/activity/ktx/R\$drawable.class"

        val info = filePathScanner.scanFilePath(path)

        Truth.assertThat(info.name).isEqualTo("R\$drawable.class")
        Truth.assertThat(info.dirPath).isEqualTo("androidx/activity/ktx")
    }
}