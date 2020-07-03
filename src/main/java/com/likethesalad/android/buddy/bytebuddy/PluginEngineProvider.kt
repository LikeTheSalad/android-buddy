package com.likethesalad.android.buddy.bytebuddy

import net.bytebuddy.build.Plugin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PluginEngineProvider @Inject constructor() {
    fun makeEngine(): Plugin.Engine {
        return Plugin.Engine.Default()
    }
}