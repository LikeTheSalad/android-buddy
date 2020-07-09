package com.likethesalad.android.common.utils

import com.google.common.truth.Truth
import com.likethesalad.android.testutils.ResourcesFinder
import io.github.classgraph.ClassGraph
import org.junit.Test
import java.io.File

class PluginsFinderTest {

    private val resourcesFinder = ResourcesFinder(javaClass)

    @Test
    fun `Give empty list when no plugin class is found`() {
        val pluginsFinder = createInstance(getClassGraphWithoutPlugins())

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).isEmpty()
    }

    @Test
    fun `Give plugin list when plugin class are found`() {
        val pluginsFinder = createInstance(getClassGraphWithPlugins())

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).containsExactly(
            "com.likethesalad.android.common.plugins.WhyUHere",
            "com.likethesalad.android.common.plugins.subplugins.SubAbstractPlugin",
            "com.likethesalad.android.common.plugins.subplugins.SubBasePlugin",
            "com.likethesalad.android.common.plugins.BasePlugin",
            "com.likethesalad.android.common.plugins.AbstractPlugin"
        )
    }

    private fun getClassGraphWithPlugins(): ClassGraph {
        return ClassGraph().overrideClasspath(getWithPluginClassesDir().path)
    }

    private fun getClassGraphWithoutPlugins(): ClassGraph {
        return ClassGraph().overrideClasspath(getWithoutPluginClassesDir().path)
    }

    private fun getWithoutPluginClassesDir(): File {
        return resourcesFinder.getResourceFile("classdirs/withoutplugins")
    }

    private fun getWithPluginClassesDir(): File {
        return resourcesFinder.getResourceFile("classdirs/withplugins")
    }

    private fun createInstance(buildClassGraph: ClassGraph): PluginsFinder {
        return PluginsFinder(buildClassGraph)
    }
}