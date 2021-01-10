package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader
import org.junit.Before
import org.junit.Test

class ByteArrayClassLoaderUtilTest : BaseMockable() {

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    @MockK
    lateinit var byteArrayClassLoader: ByteArrayClassLoader

    private lateinit var byteArrayClassLoaderUtil: ByteArrayClassLoaderUtil

    @Before
    fun setUp() {
        byteArrayClassLoaderUtil = ByteArrayClassLoaderUtil(byteBuddyClassesInstantiator)
    }

    @Test
    fun `load class from name`() {
        val name = "className"
        val byteArray = ByteArray(1)
        val expectedLoadedClass = javaClass
        every {
            byteBuddyClassesInstantiator.makeByteArrayClassLoader(mapOf(name to byteArray))
        }.returns(byteArrayClassLoader)
        every { byteArrayClassLoader.loadClass(name) }.returns(expectedLoadedClass)

        val loaded = byteArrayClassLoaderUtil.loadClass(name, byteArray)

        Truth.assertThat(loaded).isEqualTo(expectedLoadedClass)
        verify {
            byteArrayClassLoader.loadClass(name)
        }
    }
}