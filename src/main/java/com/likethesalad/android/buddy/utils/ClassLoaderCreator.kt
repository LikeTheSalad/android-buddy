package com.likethesalad.android.buddy.utils

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassLoaderCreator @Inject constructor() {

    fun create(folders: Set<File>, parent: ClassLoader): ClassLoader {
        val urls = mutableListOf<URL>()
        for (file in folders) {
            urls.add(file.toURI().toURL())
        }
        return URLClassLoader(urls.toTypedArray(), parent)
    }
}