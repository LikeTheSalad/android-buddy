package com.likethesalad.android.buddylib.actions

import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddylib.actions.base.BaseAction
import com.likethesalad.android.common.utils.PluginsFinder

@AutoFactory
class VerifyPluginClassesProvidedAction(
    private val pluginNames: Set<String>,
    private val pluginsFinder: PluginsFinder
) : BaseAction {

    override fun execute() {
        val builtPluginNames = pluginsFinder.findBuiltPluginClassNames()
        val notFoundProvidedPlugins = mutableSetOf<String>()
        for (providedName in pluginNames) {
            if (providedName !in builtPluginNames) {
                notFoundProvidedPlugins.add(providedName)
            }
        }
        if (notFoundProvidedPlugins.isNotEmpty()) {
            throw IllegalArgumentException("Plugin(s) not found: $notFoundProvidedPlugins")
        }
    }
}