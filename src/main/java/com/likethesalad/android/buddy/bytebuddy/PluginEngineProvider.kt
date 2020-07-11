package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.di.AppScope
import net.bytebuddy.build.Plugin
import javax.inject.Inject

@AppScope
class PluginEngineProvider @Inject constructor() {
    fun makeEngine(): Plugin.Engine {
        return Plugin.Engine.Default()
    }
}