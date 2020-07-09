package com.likethesalad.android.common.utils

import com.google.common.truth.Truth
import com.likethesalad.android.testutils.BaseMockable
import io.github.classgraph.ClassGraph
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.gradle.api.file.FileCollection
import org.junit.Before
import org.junit.Test
import java.io.File

class ClassGraphProviderTest : BaseMockable() {

    @MockK
    lateinit var instantiatorWrapper: InstantiatorWrapper

    @MockK
    lateinit var classGraph: ClassGraph

    @MockK
    lateinit var dirs: FileCollection

    private lateinit var classGraphProvider: ClassGraphProvider

    @Before
    fun setUp() {
        every {
            instantiatorWrapper.getClassGraph()
        }.returns(classGraph)
        classGraphProvider = ClassGraphProvider(instantiatorWrapper, dirs)
    }

    @Test
    fun `Override class graph path when initializing`() {
        val classPaths = mutableSetOf<File>()
        every {
            classGraph.overrideClasspath(any<Set<File>>())
        }.returns(classGraph)
        every {
            dirs.files
        }.returns(classPaths)

        val result = classGraphProvider.classGraph

        Truth.assertThat(result).isEqualTo(classGraph)
        verify {
            classGraph.overrideClasspath(classPaths)
        }
    }
}