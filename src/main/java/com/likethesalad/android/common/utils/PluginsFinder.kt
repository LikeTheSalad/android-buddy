package com.likethesalad.android.common.utils

import io.github.classgraph.ClassGraph
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginsFinder @Inject constructor(private val buildClassGraph: ClassGraph) {

    companion object {
        private const val PLUGIN_INTERFACE_NAME = "net.bytebuddy.build.Plugin"
    }

    fun findBuiltPluginClassNames(): Set<String> {
        val scanResult = buildClassGraph.enableClassInfo().scan()
        val info = scanResult.getClassesImplementing(PLUGIN_INTERFACE_NAME)
        return info.names.toSet()
    }
}