package com.likethesalad.android.common.providers

import org.slf4j.Logger

interface ProjectLoggerProvider {

    fun getLogger(): Logger
}