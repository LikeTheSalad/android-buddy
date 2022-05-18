package com.likethesalad.android.common.utils

import com.likethesalad.android.buddy.tools.Transformation
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class PluginsFinder @AssistedInject
constructor(@Assisted private val classGraphProvider: ClassGraphProvider) {

    @AssistedFactory
    interface Factory {
        fun create(classGraphProvider: ClassGraphProvider): PluginsFinder
    }

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