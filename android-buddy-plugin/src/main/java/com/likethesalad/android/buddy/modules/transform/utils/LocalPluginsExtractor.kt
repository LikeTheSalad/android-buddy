package com.likethesalad.android.buddy.modules.transform.utils

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.providers.impl.DefaultClassGraphFilesProvider
import com.likethesalad.android.common.utils.ClassGraphProviderFactory
import com.likethesalad.android.common.utils.PluginsFinderFactory
import java.io.File
import javax.inject.Inject

@AppScope
class LocalPluginsExtractor @Inject constructor(
    private val pluginsFinderFactory: PluginsFinderFactory,
    private val classGraphProviderFactory: ClassGraphProviderFactory
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