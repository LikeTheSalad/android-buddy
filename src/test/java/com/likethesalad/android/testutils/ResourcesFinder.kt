package com.likethesalad.android.testutils

import java.io.File
import java.nio.file.Paths

class ResourcesFinder(clazz: Class<*>) {

    private val packageResourcesPath: File by lazy {
        val resourceDirectory = Paths.get("src", "test", "resources").toFile().absolutePath
        File("$resourceDirectory/${clazz.`package`.name.replace(".", "/")}")
    }

    fun getResourceFile(relativePath: String): File {
        return File(packageResourcesPath, relativePath)
    }
}