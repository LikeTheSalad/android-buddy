package com.likethesalad.android.buddy.configuration

import com.android.build.api.transform.QualifiedContent
import com.google.common.truth.Truth
import com.likethesalad.android.buddy.configuration.libraries.scope.LibrariesScope
import com.likethesalad.android.buddy.configuration.libraries.scope.LibrariesScopeMapper
import com.likethesalad.android.buddy.extension.AndroidBuddyExtension
import com.likethesalad.android.buddy.extension.libraries.LibrariesPolicyExtension
import com.likethesalad.android.buddy.extension.libraries.TransformationScopeExtension
import com.likethesalad.android.buddy.extension.libraries.scope.LibrariesScopeExtension
import com.likethesalad.android.buddy.providers.AndroidBuddyExtensionProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.junit.Before
import org.junit.Test

@Suppress("UnstableApiUsage")
class AndroidBuddyPluginConfigurationTest : BaseMockable() {

    @MockK
    lateinit var androidBuddyExtensionProvider: AndroidBuddyExtensionProvider

    @MockK
    lateinit var librariesScopeMapper: LibrariesScopeMapper

    @MockK
    lateinit var extension: AndroidBuddyExtension

    @MockK
    lateinit var librariesPolicyExtension: LibrariesPolicyExtension

    @MockK
    lateinit var librariesScopeExtension: LibrariesScopeExtension

    @MockK
    lateinit var transformationScopeExtension: TransformationScopeExtension

    private lateinit var androidBuddyPluginConfiguration: AndroidBuddyPluginConfiguration

    @Before
    fun setUp() {
        every {
            androidBuddyExtensionProvider.getAndroidBuddyExtension()
        }.returns(extension)
        every {
            extension.librariesPolicy
        }.returns(librariesPolicyExtension)
        every {
            librariesPolicyExtension.scope
        }.returns(librariesScopeExtension)
        every {
            extension.transformationScope
        }.returns(transformationScopeExtension)

        androidBuddyPluginConfiguration =
            AndroidBuddyPluginConfiguration(androidBuddyExtensionProvider, librariesScopeMapper)
    }

    @Test
    fun `Get libraries scope`() {
        val expected = LibrariesScope.UseAll
        every {
            librariesScopeMapper.librariesScopeExtensionToLibrariesScope(librariesScopeExtension)
        }.returns(expected)

        val result = androidBuddyPluginConfiguration.getLibrariesScope()

        Truth.assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `Get PROJECT transformation scope`() {
        val scopeExtension = "PROJECT"
        val scopeProperty = createPropertyOf(scopeExtension)
        every {
            transformationScopeExtension.scope
        }.returns(scopeProperty)

        val result = androidBuddyPluginConfiguration.getTransformationScope()

        Truth.assertThat(result).containsExactly(QualifiedContent.Scope.PROJECT)
    }

    @Test
    fun `Get ALL transformation scope`() {
        val scopeExtension = "ALL"
        val scopeProperty = createPropertyOf(scopeExtension)
        every {
            transformationScopeExtension.scope
        }.returns(scopeProperty)

        val result = androidBuddyPluginConfiguration.getTransformationScope()

        Truth.assertThat(result).containsExactly(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES,
            QualifiedContent.Scope.SUB_PROJECTS
        )
    }

    @Test
    fun `Get excluded prefixes`() {
        val property = createSetPropertyOf("one/prefix", "second/prefix/another")
        every { transformationScopeExtension.excludePrefixes }.returns(property)

        val result = androidBuddyPluginConfiguration.getExcludePrefixes()

        Truth.assertThat(result).containsExactly("one/prefix", "second/prefix/another")
    }

    private inline fun <reified T : Any> createPropertyOf(value: T): Property<T> {
        val mock = mockk<Property<T>>()
        every { mock.get() }.returns(value)
        every { mock.getOrElse(any()) }.returns(value)

        return mock
    }

    private inline fun <reified T : Any> createSetPropertyOf(vararg values: T): SetProperty<T> {
        val mock = mockk<SetProperty<T>>()
        val setValues = values.toSet()
        every { mock.get() }.returns(setValues)
        every { mock.getOrElse(any()) }.returns(setValues)

        return mock
    }
}