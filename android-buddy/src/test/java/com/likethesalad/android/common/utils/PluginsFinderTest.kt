package com.likethesalad.android.common.utils

import com.google.common.truth.Truth
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.DummyResourcesFinder
import io.github.classgraph.ClassGraph
import io.mockk.every
import org.junit.Test
import java.io.File

class PluginsFinderTest : BaseMockable() {

    private val resourcesFinder = DummyResourcesFinder(javaClass)

    @Test
    fun `Give empty list when no annotated plugin class is found`() {
        val pluginsFinder = createInstance(createClassGraphProviderMock(getClassGraphWithoutPlugins()))

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).isEmpty()
    }

    @Test
    fun `Give plugin list when annotated plugin classes are found`() {
        val pluginsFinder = createInstance(createClassGraphProviderMock(getClassGraphWithPlugins()))

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).containsExactly(
            "com.likethesalad.android.buddy.transform.pluginstemp.ProperPlugin",
            "com.likethesalad.android.buddy.transform.pluginstemp.subdir.AnotherProperPlugin"
        )
    }

    private fun createClassGraphProviderMock(classGraph: ClassGraph): ClassGraphProvider {
        val provider = mockk<ClassGraphProvider>()
        every { provider.classGraph }.returns(classGraph)
        return provider
    }

    private fun getClassGraphWithPlugins(): ClassGraph {
        return ClassGraph().overrideClasspath(getWithPluginClassesDir())
    }

    private fun getClassGraphWithoutPlugins(): ClassGraph {
        return ClassGraph().overrideClasspath(getWithoutPluginClassesDir())
    }

    private fun getWithoutPluginClassesDir(): File {
        return resourcesFinder.getResourceFile("classdirs/withoutplugins")
    }

    private fun getWithPluginClassesDir(): File {
        return resourcesFinder.getResourceFile("classdirs/withplugins")
    }

    private fun createInstance(buildClassGraphProvider: ClassGraphProvider): PluginsFinder {
        return PluginsFinder(buildClassGraphProvider)
    }
}