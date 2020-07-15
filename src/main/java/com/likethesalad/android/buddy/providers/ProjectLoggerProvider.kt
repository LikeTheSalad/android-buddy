package com.likethesalad.android.buddy.providers

import org.gradle.api.logging.Logger

interface ProjectLoggerProvider {

    fun getLogger(): Logger
}