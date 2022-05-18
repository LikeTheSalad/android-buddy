package com.likethesalad.android.buddy.modules.transform.utils

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.providers.impl.DefaultClassGraphFilesProvider
import com.likethesalad.android.common.utils.ClassGraphProvider
import com.likethesalad.android.common.utils.PluginsFinder
import java.io.File
import javax.inject.Inject

@AppScope
class LocalPluginsExtractor @Inject constructor(
    private val pluginsFinderFactory: PluginsFinder.Factory,
    private val classGraphProviderFactory: ClassGraphProvider.Factory
) {

    fun getLocalPluginNames(localDirs: Set<File>): Set<String> {
        if (localDirs.isEmpty()) {
            return emptySet()
        }

        val classGraphProvider = classGraphProviderFactory.create(DefaultClassGraphFilesProvider(localDirs))
        val pluginsFinder = pluginsFinderFactory.create(classGraphProvider)
        return pluginsFinder.findBuiltPluginClassNames()
    }
}