package com.likethesalad.android.buddylib.providers

import java.io.File

interface IncrementalDirProvider {

    fun createIncrementalDir(dirName: String): File
}