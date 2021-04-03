package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.ByteBuddy
import net.bytebuddy.dynamic.ClassFileLocator
import org.junit.Before
import org.junit.Test
import java.io.File

class ClassFileLocatorMakerTest : BaseMockable() {

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    private lateinit var classFileLocatorMaker: ClassFileLocatorMaker

    @Before
    fun setUp() {
        classFileLocatorMaker = ClassFileLocatorMaker(byteBuddyClassesInstantiator)
    }

    @Test
    fun `Make class file locator based on files and dirs`() {
        val dir1 = createDirectoryAndLocatorMock()
        val dir2 = createDirectoryAndLocatorMock()
        val dir3 = createDirectoryAndLocatorMock()
        val file1 = createFileAndLocatorMock()
        val file2 = createFileAndLocatorMock()
        val classLoaderLocator = mockk<ClassFileLocator>()
        val dirs = listOf(dir1.fileOrDir, dir2.fileOrDir, dir3.fileOrDir)
        val files = listOf(file1.fileOrDir, file2.fileOrDir)
        val all = (dirs + files).toSet()
        val expectedResult = mockk<ClassFileLocator>()
        every {
            byteBuddyClassesInstantiator.makeClassLoaderClassFileLocator(ByteBuddy::class.java.classLoader)
        }.returns(classLoaderLocator)
        every { byteBuddyClassesInstantiator.makeCompoundClassFileLocator(any()) }.returns(expectedResult)

        val result = classFileLocatorMaker.make(all)

        Truth.assertThat(result).isEqualTo(expectedResult)
        verify {
            byteBuddyClassesInstantiator.makeCompoundClassFileLocator(
                listOf(
                    dir1.expectedLocator, dir2.expectedLocator, dir3.expectedLocator,
                    file1.expectedLocator, file2.expectedLocator, classLoaderLocator
                )
            )
        }
    }

    private fun createFileAndLocatorMock(): FileAndExpectedLocator {
        val file = mockk<File>()
        val locator = mockk<ClassFileLocator>()
        every { file.isFile }.returns(true)
        every { byteBuddyClassesInstantiator.makeJarClassFileLocator(file) }.returns(locator)

        return FileAndExpectedLocator(file, locator)
    }

    private fun createDirectoryAndLocatorMock(): FileAndExpectedLocator {
        val dir = mockk<File>()
        val locator = mockk<ClassFileLocator>()
        every { dir.isFile }.returns(false)
        every { byteBuddyClassesInstantiator.makeFolderClassFileLocator(dir) }.returns(locator)

        return FileAndExpectedLocator(dir, locator)
    }

    class FileAndExpectedLocator(val fileOrDir: File, val expectedLocator: ClassFileLocator)
}