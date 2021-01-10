package com.likethesalad.android.buddy.utils

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.di.AppScope
import javax.inject.Inject

@AppScope
class ByteArrayClassLoaderUtil
@Inject constructor(private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator) {

    fun loadClass(name: String, byteArray: ByteArray): Class<out Any> {
        val classLoader = byteBuddyClassesInstantiator.makeByteArrayClassLoader(
            mapOf(name to byteArray)
        )

        return classLoader.loadClass(name)
    }
}