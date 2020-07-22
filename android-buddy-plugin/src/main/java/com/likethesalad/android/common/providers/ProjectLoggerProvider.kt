package com.likethesalad.android.common.providers

import org.gradle.api.logging.Logger

interface ProjectLoggerProvider {

    fun getLogger(): Logger
}