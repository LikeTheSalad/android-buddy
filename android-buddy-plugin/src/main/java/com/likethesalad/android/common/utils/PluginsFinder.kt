package com.likethesalad.android.common.utils

import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.tools.Transformation

@AutoFactory
class PluginsFinder(private val classGraphProvider: ClassGraphProvider) {

    companion object {
        private const val PLUGIN_INTERFACE_NAME = "net.bytebuddy.build.Plugin"
    }

    fun findBuiltPluginClassNames(): Set<String> {
        val scanResult = classGraphProvider.classGraph.enableClassInfo().enableAnnotationInfo().scan()
        val info = scanResult.getClassesWithAnnotation(Transformation::class.java.name)
            .filter {
                it.isStandardClass && it.implementsInterface(PLUGIN_INTERFACE_NAME) && !it.isAbstract
            }
        return info.names.toSet()
    }
}