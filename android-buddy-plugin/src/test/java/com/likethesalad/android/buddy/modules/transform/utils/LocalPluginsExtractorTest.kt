package com.likethesalad.android.buddy.modules.transform.utils

import com.google.common.truth.Truth
import com.likethesalad.android.common.providers.impl.DefaultClassGraphFilesProvider
import com.likethesalad.android.common.utils.ClassGraphProvider
import com.likethesalad.android.common.utils.PluginsFinder
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.io.File

class LocalPluginsExtractorTest : BaseMockable() {

    @MockK
    lateinit var pluginsFinderFactory: PluginsFinder.Factory

    @MockK
    lateinit var classGraphProviderFactory: ClassGraphProvider.Factory

    private lateinit var localPluginsExtractor: LocalPluginsExtractor

    @Before
    fun setUp() {
        localPluginsExtractor = LocalPluginsExtractor(pluginsFinderFactory, classGraphProviderFactory)
    }

    @Test
    fun `Get one plugin names`() {
        val pluginName = "one.plugin.Name"
        val localDirs = getLocalDirsWithPlugins(pluginName)

        val names = localPluginsExtractor.getLocalPluginNames(localDirs)

        Truth.assertThat(names).containsExactly(pluginName)
    }

    @Test
    fun `Get empty plugin names if file list is empty`() {
        val names = localPluginsExtractor.getLocalPluginNames(emptySet())

        Truth.assertThat(names).isEmpty()
    }

    private fun getLocalDirsWithPlugins(vararg pluginNames: String): Set<File> {
        val localDirs = setOf<File>(mockk())
        val classGraphProvider = mockk<ClassGraphProvider>()
        val pluginsFinder = mockk<PluginsFinder>()
        every {
            classGraphProviderFactory.create(DefaultClassGraphFilesProvider(localDirs))
        }.returns(classGraphProvider)
        every {
            pluginsFinderFactory.create(classGraphProvider)
        }.returns(pluginsFinder)
        every {
            pluginsFinder.findBuiltPluginClassNames()
        }.returns(pluginNames.toList().toSet())

        return localDirs
    }
}