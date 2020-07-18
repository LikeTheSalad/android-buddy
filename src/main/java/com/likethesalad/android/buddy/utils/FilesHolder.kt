package com.likethesalad.android.buddy.utils

import java.io.File

data class FilesHolder(
    val dirFiles: Set<File>,
    val jarFiles: Set<File>,
    val allFiles: Set<File>
)