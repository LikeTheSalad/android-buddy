package com.likethesalad.android.buddy.utils

import com.android.build.api.transform.*
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.File

class TransformInvocationDataExtractorTest : BaseMockable() {

    @MockK
    lateinit var androidBootClasspathProvider: AndroidBootClasspathProvider

    @MockK
    lateinit var transformInvocation: TransformInvocation

    private lateinit var transformInvocationDataExtractor: TransformInvocationDataExtractor

    @Before
    fun setUp() {
        transformInvocationDataExtractor = TransformInvocationDataExtractor(
            androidBootClasspathProvider,
            transformInvocation
        )
    }

    @Test
    fun `Get output folder`() {
        val outputProvider = mockk<TransformOutputProvider>()
        val expectedFolder = mockk<File>()
        every {
            transformInvocation.outputProvider
        }.returns(outputProvider)
        every {
            outputProvider.getContentLocation(any(), any(), any(), any())
        }.returns(expectedFolder)

        val result = transformInvocationDataExtractor.getOutputFolder()

        Truth.assertThat(result).isEqualTo(expectedFolder)
        verify {
            outputProvider.getContentLocation(
                "androidBuddyTransform",
                setOf(QualifiedContent.DefaultContentType.CLASSES),
                mutableSetOf(QualifiedContent.Scope.PROJECT),
                Format.DIRECTORY
            )
        }
    }

    @Test
    fun `Get classpath`() {
        val dir1 = mockk<File>()
        val dir2 = mockk<File>()
        val jar1 = mockk<File>()
        val jar2 = mockk<File>()
        val inputs = createInputs(setOf(dir1, dir2), setOf(jar1, jar2))
        val androidPath1 = mockk<File>()
        val androidPath2 = mockk<File>()
        val androidBootClasspath = setOf<File>(androidPath1, androidPath2)
        every {
            androidBootClasspathProvider.getBootClasspath()
        }.returns(androidBootClasspath)
        every {
            transformInvocation.inputs
        }.returns(inputs)

        val result = transformInvocationDataExtractor.getClasspath()

        Truth.assertThat(result).containsExactly(dir1, dir2, jar1, jar2, androidPath1, androidPath2)
    }

    private fun createInputs(dirs: Set<File>, jars: Set<File>): MutableCollection<TransformInput> {
        val jarInputs = mutableListOf<JarInput>()
        val dirInputs = mutableListOf<DirectoryInput>()

        jars.forEach {
            val jar = mockk<JarInput>()
            every { jar.file }.returns(it)
            jarInputs.add(jar)
        }

        dirs.forEach {
            val dir = mockk<DirectoryInput>()
            every { dir.file }.returns(it)
            dirInputs.add(dir)
        }

        val transformInput = mockk<TransformInput>()
        every {
            transformInput.jarInputs
        }.returns(jarInputs)
        every {
            transformInput.directoryInputs
        }.returns(dirInputs)

        return mutableListOf(transformInput)
    }
}