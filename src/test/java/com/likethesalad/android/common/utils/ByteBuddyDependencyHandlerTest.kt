package com.likethesalad.android.common.utils

import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.junit.Before
import org.junit.Test

class ByteBuddyDependencyHandlerTest : BaseMockable() {

    @MockK
    lateinit var dependencyHandler: DependencyHandler

    private lateinit var byteBuddyDependencyHandler: ByteBuddyDependencyHandler

    @Before
    fun setUp() {
        every { dependencyHandler.add(any(), any()) }.returns(mockk())
        byteBuddyDependencyHandler = ByteBuddyDependencyHandler()
    }

    @Test
    fun `Add dependency with default version`() {
        byteBuddyDependencyHandler.addDependency(dependencyHandler, mapOf())

        verifyDependencyAdded(Constants.BYTE_BUDDY_DEPENDENCY_VERSION)
    }

    @Test
    fun `Add dependency with properties version`() {
        val propertiesVersion = "someVersion"
        val properties = mapOf<String, Any?>("android.buddy.byteBuddy.version" to propertiesVersion)

        byteBuddyDependencyHandler.addDependency(dependencyHandler, properties)

        verifyDependencyAdded(propertiesVersion)
    }

    private fun verifyDependencyAdded(version: String) {
        verify {
            dependencyHandler.add("compileOnly", "net.bytebuddy:byte-buddy:$version")
        }
    }
}