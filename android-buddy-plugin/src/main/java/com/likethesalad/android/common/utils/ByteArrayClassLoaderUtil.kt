package com.likethesalad.android.common.utils

import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ByteArrayClassLoaderUtil
@Inject constructor(private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator) {

    fun loadClass(name: String, byteArray: ByteArray): Class<out Any> {
        val classLoader = byteBuddyClassesInstantiator.makeByteArrayClassLoader(
            mapOf(name to byteArray)
        )

        return classLoader.loadClass(name)
    }
}