package com.likethesalad.android.common.utils

import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.junit.Before
import org.junit.Test

class DependencyHandlerUtilTest : BaseMockable() {

    @MockK
    lateinit var dependencyHandler: DependencyHandler

    @MockK
    lateinit var gradleApiDependency: Dependency

    private lateinit var dependencyHandlerUtil: DependencyHandlerUtil

    @Before
    fun setUp() {
        every { dependencyHandler.add(any(), any()) }.returns(mockk())
        every { dependencyHandler.gradleApi() }.returns(gradleApiDependency)
        dependencyHandlerUtil = DependencyHandlerUtil()
    }

    @Test
    fun `Add byte buddy dependency with default version`() {
        dependencyHandlerUtil.addDependencies(dependencyHandler, mapOf())

        verifyByteBuddyDependencyAdded(Constants.BYTE_BUDDY_DEPENDENCY_VERSION)
    }

    @Test
    fun `Add byte buddy dependency with properties version`() {
        val propertiesVersion = "someVersion"
        val properties = mapOf<String, Any?>("android.buddy.byteBuddy.version" to propertiesVersion)

        dependencyHandlerUtil.addDependencies(dependencyHandler, properties)

        verifyByteBuddyDependencyAdded(propertiesVersion)
    }

    @Test
    fun `Add gradleApi dependency`() {
        dependencyHandlerUtil.addDependencies(dependencyHandler, emptyMap())

        verifyDependencyAdded(gradleApiDependency)
    }

    private fun verifyDependencyAdded(dependency: Any) {
        verify {
            dependencyHandler.add("compileOnly", dependency)
        }
    }

    private fun verifyByteBuddyDependencyAdded(version: String) {
        verifyDependencyAdded("net.bytebuddy:byte-buddy:$version")
    }
}