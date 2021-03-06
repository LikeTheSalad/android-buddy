package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.utils.Logger
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import javax.inject.Inject

/**
 * Taken from
 * https://github.com/raphw/byte-buddy/blob/master/byte-buddy-gradle-plugin/src/main/java/net/bytebuddy/build/gradle/TransformationAction.java
 */
@AppScope
class TransformationLogger @Inject constructor(private val logger: Logger) : Plugin.Engine.Listener.Adapter() {

    companion object {
        private const val PREFIX = "[ByteBuddy]"
    }

    override fun onTransformation(typeDescription: TypeDescription, plugins: List<Plugin>) {
        logger.debug("{} Transformed {} using {}", PREFIX, typeDescription, plugins)
    }

    override fun onError(
        typeDescription: TypeDescription,
        plugin: Plugin,
        throwable: Throwable
    ) {
        logger.warning("{} Failed to transform {} using {}", PREFIX, typeDescription, plugin, throwable)
    }

    override fun onError(throwables: Map<TypeDescription, List<Throwable>>) {
        logger.warning("{} Failed to transform {} types", PREFIX, throwables.size)
    }

    override fun onError(plugin: Plugin, throwable: Throwable) {
        logger.error("{} Failed to close {}", PREFIX, plugin, throwable)
    }

    override fun onLiveInitializer(typeDescription: TypeDescription, definingType: TypeDescription) {
        logger.debug(
            "{} Discovered live initializer for {} as a result of transforming {}",
            PREFIX,
            definingType,
            typeDescription
        )
    }
}