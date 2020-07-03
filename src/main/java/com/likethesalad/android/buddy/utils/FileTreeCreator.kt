package com.likethesalad.android.buddy.utils

import org.gradle.api.file.FileTree
import java.io.File

interface FileTreeCreator {
    fun createFileTree(folder: File): FileTree
}