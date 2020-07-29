package com.likethesalad.android.common.utils

import com.likethesalad.android.common.providers.ProjectLoggerProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Logger @Inject constructor(projectLoggerProvider: ProjectLoggerProvider) {

    private val projectLogger by lazy { projectLoggerProvider.getLogger() }

    companion object {
        private const val PREFIX = "[AndroidBuddy]"
    }

    fun debug(text: String, vararg extra: Any) {
        projectLogger.debug("$PREFIX $text", *extra)
    }

    fun info(text: String, vararg extra: Any) {
        projectLogger.info("$PREFIX $text", *extra)
    }

    fun warning(text: String, vararg extra: Any) {
        projectLogger.warn("$PREFIX $text", *extra)
    }

    fun error(text: String, vararg extra: Any) {
        projectLogger.error("$PREFIX $text", *extra)
    }

    fun lifecycle(text: String, vararg extra: Any) {
        projectLogger.lifecycle(text, *extra)
    }
}