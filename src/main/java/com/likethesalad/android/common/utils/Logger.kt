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

    fun d(text: String, vararg extra: Any) {
        projectLogger.debug("$PREFIX $text", *extra)
    }

    fun i(text: String, vararg extra: Any) {
        projectLogger.info("$PREFIX $text", *extra)
    }

    fun w(text: String, vararg extra: Any) {
        projectLogger.warn("$PREFIX $text", *extra)
    }
}