package com.likethesalad.android.buddy.utils

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.utils.InstantiatorWrapper
import java.io.File
import java.net.URL
import javax.inject.Inject

@AppScope
class ClassLoaderCreator @Inject constructor(private val instantiatorWrapper: InstantiatorWrapper) {

    fun create(folders: Set<File>, parent: ClassLoader): ClassLoader {
        val urls = mutableListOf<URL>()
        for (folder in folders) {
            urls.add(folder.toURI().toURL())
        }
        return instantiatorWrapper.getUrlClassLoader(urls.toTypedArray(), parent)
    }
}