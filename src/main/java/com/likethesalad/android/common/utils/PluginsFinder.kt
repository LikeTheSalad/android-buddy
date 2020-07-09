package com.likethesalad.android.common.utils

import com.google.auto.factory.AutoFactory

@AutoFactory
class PluginsFinder(private val classGraphProvider: ClassGraphProvider) {

    companion object {
        private const val PLUGIN_INTERFACE_NAME = "net.bytebuddy.build.Plugin"
    }

    fun findBuiltPluginClassNames(): Set<String> {
        val scanResult = classGraphProvider.classGraph.enableClassInfo().scan()
        val info = scanResult.getClassesImplementing(PLUGIN_INTERFACE_NAME)
        return info.names.toSet()
    }
}