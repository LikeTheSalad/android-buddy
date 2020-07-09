package com.likethesalad.android.buddylib.tasks.actions

import io.github.classgraph.ClassGraph
import io.mockk.impl.annotations.MockK

class VerifyPluginClassesProvidedTest {

    @MockK
    lateinit var buildClassGraph: ClassGraph

    private fun createInstance(pluginNames: Set<String>): VerifyPluginClassesProvided {
        return VerifyPluginClassesProvided(pluginNames, buildClassGraph)
    }
}