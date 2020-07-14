package com.likethesalad.android.buddy.bytebuddy

import com.google.auto.factory.AutoFactory
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import org.gradle.api.logging.Logger

/**
 * Taken from
 * https://github.com/raphw/byte-buddy/blob/master/byte-buddy-gradle-plugin/src/main/java/net/bytebuddy/build/gradle/TransformationAction.java
 */
@AutoFactory
class TransformationLogger(private val logger: Logger) : Plugin.Engine.Listener.Adapter() {

    override fun onTransformation(typeDescription: TypeDescription, plugins: List<Plugin>) {
        logger.debug("Transformed {} using {}", typeDescription, plugins)
    }

    override fun onError(
        typeDescription: TypeDescription,
        plugin: Plugin,
        throwable: Throwable
    ) {
        logger.warn("Failed to transform {} using {}", typeDescription, plugin, throwable)
    }

    override fun onError(throwables: Map<TypeDescription, List<Throwable>>) {
        logger.warn("Failed to transform {} types", throwables.size)
    }

    override fun onError(plugin: Plugin, throwable: Throwable) {
        logger.error("Failed to close {}", plugin, throwable)
    }

    override fun onLiveInitializer(typeDescription: TypeDescription, definingType: TypeDescription) {
        logger.debug("Discovered live initializer for {} as a result of transforming {}", definingType, typeDescription)
    }
}