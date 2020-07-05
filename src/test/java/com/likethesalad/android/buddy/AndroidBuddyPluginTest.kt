package com.likethesalad.android.buddy

import com.android.build.gradle.AppExtension
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.models.AndroidBuddyExtension
import com.likethesalad.android.buddy.testutils.BaseMockable
import com.likethesalad.android.buddy.utils.DaggerInjector
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.verify
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.junit.Before
import org.junit.Test
import java.io.File

class AndroidBuddyPluginTest : BaseMockable() {

    @MockK
    lateinit var androidExtension: AppExtension

    @MockK
    lateinit var project: Project

    @MockK
    lateinit var extensionContainer: ExtensionContainer

    @MockK
    lateinit var androidBuddyExtension: AndroidBuddyExtension

    private lateinit var androidBuddyPlugin: AndroidBuddyPlugin

    @Before
    fun setUp() {
        mockkObject(DaggerInjector)
        every { project.extensions }.returns(extensionContainer)
        every { extensionContainer.getByType(AppExtension::class.java) }.returns(androidExtension)
        every {
            extensionContainer.create("androidBuddy", AndroidBuddyExtension::class.java)
        }.returns(androidBuddyExtension)

        androidBuddyPlugin = AndroidBuddyPlugin()
        androidBuddyPlugin.apply(project)
    }

    @Test
    fun `Check injector is initiated`() {
        verify {
            DaggerInjector.init(androidBuddyPlugin)
        }
    }

    @Test
    fun `Verify extension is created`() {
        verify {
            extensionContainer.create("androidBuddy", AndroidBuddyExtension::class.java)
        }
    }

    @Test
    fun `Get android booth classpath`() {
        val bootClasspath = mutableListOf<File>(mockk())
        every {
            androidExtension.bootClasspath
        }.returns(bootClasspath)

        Truth.assertThat(androidBuddyPlugin.getBootClasspath()).isEqualTo(bootClasspath.toSet())
    }
}