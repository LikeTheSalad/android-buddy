package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.File
import java.net.URI
import java.net.URL
import java.net.URLClassLoader

class ClassLoaderCreatorTest : BaseMockable() {

    @MockK
    lateinit var instantiatorWrapper: InstantiatorWrapper

    private lateinit var classLoaderCreator: ClassLoaderCreator

    @Before
    fun setUp() {
        classLoaderCreator = ClassLoaderCreator(instantiatorWrapper)
    }

    @Test
    fun `Create url class loader from folders`() {
        val folder1 = createFolderAndUrl()
        val folder2 = createFolderAndUrl()
        val parent = mockk<ClassLoader>()
        val expectedClassLoader = mockk<URLClassLoader>()
        every {
            instantiatorWrapper.getUrlClassLoader(any(), parent)
        }.returns(expectedClassLoader)

        val result = classLoaderCreator.create(setOf(folder1.folder, folder2.folder), parent)

        Truth.assertThat(result).isEqualTo(expectedClassLoader)
        verify {
            instantiatorWrapper.getUrlClassLoader(
                arrayOf(folder1.url, folder2.url), parent
            )
        }
    }

    private fun createFolderAndUrl(): FolderAndUrl {
        val folder = mockk<File>()
        val uri = mockk<URI>()
        val url = mockk<URL>()
        every { folder.toURI() }.returns(uri)
        every { uri.toURL() }.returns(url)

        return FolderAndUrl(folder, url)
    }

    class FolderAndUrl(val folder: File, val url: URL)
}