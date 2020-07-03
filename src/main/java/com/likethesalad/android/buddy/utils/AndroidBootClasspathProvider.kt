package com.likethesalad.android.buddy.utils

import java.io.File

interface AndroidBootClasspathProvider {
    fun getBootClasspath(): Set<File>
}