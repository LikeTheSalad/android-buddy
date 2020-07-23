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
        val classGraphProvider = createClassGraphProviderMock(getClassGraphWithoutPlugins(SourceType.DIR))
        val pluginsFinder = createInstance(classGraphProvider)

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).isEmpty()
    }

    @Test
    fun `Give plugin list when annotated plugin classes are found`() {
        val classGraphProvider = createClassGraphProviderMock(getClassGraphWithPlugins(SourceType.DIR))
        val pluginsFinder = createInstance(classGraphProvider)

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).containsExactly(
            "com.likethesalad.android.buddy.transform.temptransforms.ProperTransform",
            "com.likethesalad.android.buddy.transform.temptransforms.subdir.AnotherProperTransform"
        )
    }

    @Test
    fun `Give annotated plugins from JAR file`() {
        val classGraphProvider = createClassGraphProviderMock(getClassGraphWithPlugins(SourceType.JAR))
        val pluginsFinder = createInstance(classGraphProvider)

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).containsExactly(
            "com.my.test.lib.ProperPlugin",
            "com.my.test.lib.subdir.AnotherProperPlugin"
        )
    }

    @Test
    fun `Give empty list when no annotated plugin class is found in JAR`() {
        val classGraphProvider = createClassGraphProviderMock(getClassGraphWithoutPlugins(SourceType.JAR))
        val pluginsFinder = createInstance(classGraphProvider)

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).isEmpty()
    }

    @Test
    fun `Give annotated plugins from JAR and DIR files`() {
        val classGraphProvider = createClassGraphProviderMock(
            getClassGraphWithClasspath(
                getWithPluginClassesDir(),
                getWithPluginClassesJar()
            )
        )
        val pluginsFinder = createInstance(classGraphProvider)

        val classesFound = pluginsFinder.findBuiltPluginClassNames()

        Truth.assertThat(classesFound).containsExactly(
            "com.my.test.lib.ProperPlugin",
            "com.my.test.lib.subdir.AnotherProperPlugin",
            "com.likethesalad.android.buddy.transform.temptransforms.ProperTransform",
            "com.likethesalad.android.buddy.transform.temptransforms.subdir.AnotherProperTransform"
        )
    }

    private fun createClassGraphProviderMock(classGraph: ClassGraph): ClassGraphProvider {
        val provider = mockk<ClassGraphProvider>()
        every { provider.classGraph }.returns(classGraph)
        return provider
    }

    private fun getClassGraphWithPlugins(sourceType: SourceType): ClassGraph {
        val file = when (sourceType) {
            SourceType.DIR -> getWithPluginClassesDir()
            SourceType.JAR -> getWithPluginClassesJar()
        }
        return getClassGraphWithClasspath(file)
    }

    private fun getClassGraphWithoutPlugins(sourceType: SourceType): ClassGraph {
        val file = when (sourceType) {
            SourceType.DIR -> getWithoutPluginClassesDir()
            SourceType.JAR -> getWithoutPluginClassesJar()
        }
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

    private fun getWithoutPluginClassesJar(): File {
        return resourcesFinder.getResourceFile("classdirs/normal-java-library-1.0-SNAPSHOT.jar")
    }

    private fun getWithPluginClassesJar(): File {
        return resourcesFinder.getResourceFile("classdirs/android-buddy-library-1.0-SNAPSHOT.jar")
    }

    private fun createInstance(buildClassGraphProvider: ClassGraphProvider): PluginsFinder {
        return PluginsFinder(buildClassGraphProvider)
    }

    enum class SourceType {
        DIR,
        JAR
    }
}