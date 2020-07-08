package com.likethesalad.android.common.utils

import java.net.URL
import java.net.URLClassLoader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstantiatorWrapper @Inject constructor() {

    @Suppress("UNCHECKED_CAST")
    fun <T> getClassForName(name: String, initialize: Boolean, classLoader: ClassLoader): Class<out T> {
        return Class.forName(name, initialize, classLoader) as Class<T>
    }

    fun getUrlClassLoader(urls: Array<URL>, parent: ClassLoader): URLClassLoader {
        return URLClassLoader(urls, parent)
    }
}