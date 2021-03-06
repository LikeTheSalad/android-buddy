package com.likethesalad.android.common.utils

import com.google.common.truth.Truth
import com.likethesalad.android.testutils.BaseMockable
import com.likethesalad.android.testutils.DummyResourcesFinder
import io.github.classgraph.ClassGraph
import io.mockk.every
import org.junit.Test
import java.io.File

class PluginsFinderTest : BaseMockable() {

    private val resourcesFinder = DummyResourcesFinder()

    @Test
    fun `Give empty list when no annotated plugin class is found`() {
        val classGraphProvider = createClassGraphProviderMock(getClassGraphWithoutPlugins())
        val pluginsFinder = createInstance(classGraphProvider)

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).isEmpty()
    }

    @Test
    fun `Give plugin list when annotated plugin classes are found`() {
        val classGraphProvider = createClassGraphProviderMock(getClassGraphWithPlugins())
        val pluginsFinder = createInstance(classGraphProvider)

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).containsExactly(
            "com.likethesalad.android.buddy.transform.temptransforms.ProperTransform",
            "com.likethesalad.android.buddy.transform.temptransforms.subdir.AnotherProperTransform"
        )
    }

    private fun createClassGraphProviderMock(classGraph: ClassGraph): ClassGraphProvider {
        val provider = mockk<ClassGraphProvider>()
        every { provider.classGraph }.returns(classGraph)
        return provider
    }

    private fun getClassGraphWithPlugins(): ClassGraph {
        val file = getWithPluginClassesDir()
        return getClassGraphWithClasspath(file)
    }

    private fun getClassGraphWithoutPlugins(): ClassGraph {
        val file = getWithoutPluginClassesDir()
        return getClassGraphWithClasspath(file)
    }

    private fun getClassGraphWithClasspath(vararg classpath: Any): ClassGraph {
        return ClassGraph().overrideClasspath(*classpath)
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