package com.likethesalad.android.common.models.libinfo.utils

import com.google.common.truth.Truth
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import org.junit.Test

class AndroidBuddyLibraryInfoFqnBuilderTest {

    private val builder = AndroidBuddyLibraryInfoFqnBuilder()

    @Test
    fun `Build Fqn`() {
        val id = "SomeId"
        val group = "some.group"
        val name = "someProjectName"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "some.group.someProjectName.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }

    @Test
    fun `Build Fqn with invalid chars`() {
        val id = "SomeId"
        val group = "some.group"
        val name = "some-project*name"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "some.group.some_project_name.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }

    @Test
    fun `Build Fqn with numbers after dots`() {
        val id = "SomeId"
        val group = "some.group"
        val name = "5someProjectName"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "some.group.n5someProjectName.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }

    @Test
    fun `Build Fqn with numbers at the beginning`() {
        val id = "SomeId"
        val group = "8some.group"
        val name = "some8ProjectName"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "n8some.group.some8ProjectName.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }

    @Test
    fun `Build Fqn with numbers at the beginning and after dots`() {
        val id = "SomeId"
        val group = "8some.7group.same6"
        val name = "some8ProjectName"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "n8some.n7group.same6.some8ProjectName.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }
}