package com.likethesalad.android.buddy.providers

import java.io.File

interface AndroidBootClasspathProvider {
    fun getBootClasspath(): Set<File>
}