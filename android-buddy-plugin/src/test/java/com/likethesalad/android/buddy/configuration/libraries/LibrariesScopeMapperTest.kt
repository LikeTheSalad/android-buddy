package com.likethesalad.android.buddy.configuration.libraries

import com.google.common.truth.Truth.assertThat
import com.likethesalad.android.buddy.configuration.libraries.scope.LibrariesScope
import com.likethesalad.android.buddy.configuration.libraries.scope.LibrariesScopeMapper
import com.likethesalad.android.buddy.extension.libraries.scope.LibrariesScopeExtension
import io.mockk.every
import io.mockk.mockk
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.junit.Assert.fail
import org.junit.Test

@Suppress("UnstableApiUsage")
class LibrariesScopeMapperTest {

    private val librariesOptionsMapper = LibrariesScopeMapper()

    @Test
    fun `Map LibrariesOptions to LibrariesScope`() {
        assertThat(verifyMapping("UseAll")).isEqualTo(LibrariesScope.UseAll)
        assertThat(verifyMapping("IgnoreAll")).isEqualTo(LibrariesScope.IgnoreAll)
        assertThat(verifyMapping("UseOnly", listOf("abc", "def")))
            .isEqualTo(LibrariesScope.UseOnly(setOf("abc", "def")))
    }

    @Test
    fun `Expect exception when no valid library scope name is provided`() {
        try {
            verifyMapping("NotValidName")
            fail("Should've crashed above")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo(
                "Invalid library scope type name: 'NotValidName', " +
                        "the available options are: [UseAll, IgnoreAll, UseOnly]"
            )
        }
    }

    @Test
    fun `Expect exception when no args are passed for UseOnly`() {
        try {
            verifyMapping("UseOnly")
            fail("Should've crashed above")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo(
                "No library ids specified for 'UseOnly', if you don't want to use any library you " +
                        "should set the libraries scope to 'IgnoreAll' instead."
            )
        }
    }

    @Test
    fun `Expect exception when args are passed for options that don't expect any args`() {
        verifyNoArgExpectedException("UseAll")
        verifyNoArgExpectedException("IgnoreAll")
    }

    private fun verifyNoArgExpectedException(scopeType: String) {
        val dummyArgs = listOf("arg1", "arg2")
        try {
            verifyMapping(scopeType, dummyArgs)
            fail("Should've crashed above")
        } catch (e: IllegalArgumentException) {
            assertThat(e.message).isEqualTo("No args should be passed for the '$scopeType' scope")
        }
    }

    private fun verifyMapping(scopeType: String, args: List<Any> = emptyList()): LibrariesScope {
        val options = mockk<LibrariesScopeExtension>()
        val scopeNameProperty = createPropertyMock(scopeType)
        val argsProperty = createListPropertyMock(args)
        every { options.type }.returns(scopeNameProperty)
        every { options.args }.returns(argsProperty)

        return librariesOptionsMapper.librariesScopeExtensionToLibrariesScope(options)
    }

    private fun <T> createPropertyMock(value: T): Property<T> {
        val property = mockk<Property<T>>()
        every { property.get() }.returns(value)
        return property
    }

    private fun <T> createListPropertyMock(value: List<T>): ListProperty<T> {
        val property = mockk<ListProperty<T>>()
        every { property.get() }.returns(value)
        return property
    }
}