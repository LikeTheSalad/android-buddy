package com.likethesalad.android.buddy.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstantiatorWrapper @Inject constructor() {

    @Suppress("UNCHECKED_CAST")
    fun <T> getClassForName(name: String, initialize: Boolean, classLoader: ClassLoader): Class<out T> {
        return Class.forName(name, initialize, classLoader) as Class<T>
    }
}