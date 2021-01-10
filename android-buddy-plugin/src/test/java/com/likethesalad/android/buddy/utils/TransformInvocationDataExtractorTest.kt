package com.likethesalad.android.buddy.utils

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.transform.TransformInvocationDataExtractor
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.io.File

class TransformInvocationDataExtractorTest : BaseMockable() {

    @MockK
    lateinit var transformInvocation: TransformInvocation

    private lateinit var transformInvocationDataExtractor: TransformInvocationDataExtractor

    @Before
    fun setUp() {
        transformInvocationDataExtractor =
            TransformInvocationDataExtractor(
                transformInvocation
            )
    }

    @Test
    fun `Get output folder`() {
        val outputProvider = mockk<TransformOutputProvider>()
        val scopes = mutableSetOf(QualifiedContent.Scope.PROJECT)
        val expectedFolder = mockk<File>()
        every {
            transformInvocation.outputProvider
        }.returns(outputProvider)
        every {
            outputProvider.getContentLocation(any(), any(), any(), any())
        }.returns(expectedFolder)

        val result = transformInvocationDataExtractor.getOutputFolder(scopes)

        Truth.assertThat(result).isEqualTo(expectedFolder)
        verify {
            outputProvider.getContentLocation(
                "androidBuddyTransform",
                setOf(QualifiedContent.DefaultContentType.CLASSES),
                scopes,
                Format.DIRECTORY
            )
        }
    }

    @Test
    fun `Get scope classpath`() {
        val dir1 = mockk<File>()
        val dir2 = mockk<File>()
        val jar1 = mockk<File>()
        val jar2 = mockk<File>()
        val inputs = createInputs(setOf(dir1, dir2), setOf(jar1, jar2))
        every {
            transformInvocation.inputs
        }.returns(inputs)

        val result = transformInvocationDataExtractor.getScopeClasspath()

        Truth.assertThat(result.allFiles).containsExactly(dir1, dir2, jar1, jar2)
        Truth.assertThat(result.dirFiles).containsExactly(dir1, dir2)
        Truth.assertThat(result.jarFiles).containsExactly(jar1, jar2)
    }

    @Test
    fun `Get reference classpath`() {
        val dir1 = mockk<File>()
        val dir2 = mockk<File>()
        val jar1 = mockk<File>()
        val jar2 = mockk<File>()
        val inputs = createInputs(setOf(dir1, dir2), setOf(jar1, jar2))
        every {
            transformInvocation.referencedInputs
        }.returns(inputs)

        val result = transformInvocationDataExtractor.getReferenceClasspath()

        Truth.assertThat(result).containsExactly(dir1, dir2, jar1, jar2)
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