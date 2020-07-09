package com.likethesalad.android.buddylib.tasks.actions

import io.github.classgraph.ClassGraph

class VerifyPluginClassesProvided(
    private val pluginNames: Set<String>,
    private val builtClassGraph: ClassGraph
) {
    
}