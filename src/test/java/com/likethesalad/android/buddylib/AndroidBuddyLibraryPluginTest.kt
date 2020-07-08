package com.likethesalad.android.buddylib

import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.PluginManager
import org.junit.Before
import org.junit.Test

class AndroidBuddyLibraryPluginTest : BaseMockable() {

    @MockK
    lateinit var project: Project

    @MockK
    lateinit var pluginManager: PluginManager

    private lateinit var androidBuddyLibraryPlugin: AndroidBuddyLibraryPlugin

    @Before
    fun setUp() {
        every { project.pluginManager }.returns(pluginManager)
        androidBuddyLibraryPlugin = AndroidBuddyLibraryPlugin()
        androidBuddyLibraryPlugin.apply(project)
    }

    @Test
    fun `Add java library plugin on apply`() {
        verify {
            pluginManager.apply(JavaLibraryPlugin::class.java)
        }
    }
}