package com.likethesalad.android.common.models.libinfo.utils

import com.google.common.truth.Truth
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import org.junit.Test

class AndroidBuddyLibraryInfoFqnBuilderTest {

    private val builder = AndroidBuddyLibraryInfoFqnBuilder()

    @Test
    fun `Build Fqn`() {
        val id = "some-id"
        val group = "some.group"
        val name = "someProjectName"
        val version = "1.0.1"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "some.group.someProjectName.some_id.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, version, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }

    @Test
    fun `Build Fqn with invalid chars`() {
        val id = "some-id"
        val group = "some.group"
        val name = "some-project*name"
        val version = "1.0.2"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "some.group.some_project_name.some_id.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, version, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }

    @Test
    fun `Build Fqn with numbers after dots`() {
        val id = "some-id"
        val group = "some.group"
        val name = "5someProjectName"
        val version = "1.0.2"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "some.group.n5someProjectName.some_id.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, version, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }

    @Test
    fun `Build Fqn with numbers at the beginning`() {
        val id = "some-id"
        val group = "8some.group"
        val name = "some8ProjectName"
        val version = "1.0.2"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "n8some.group.some8ProjectName.some_id.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, version, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }

    @Test
    fun `Build Fqn with numbers at the beginning and after dots`() {
        val id = "some-id"
        val group = "8some.7group.same6"
        val name = "some8ProjectName"
        val version = "1.0.2"
        val plugins = setOf("one.Class", "another.Class")
        val expectedClassName = "n8some.n7group.same6.some8ProjectName.some_id.library_definition"
        val info = AndroidBuddyLibraryInfo(id, group, name, version, plugins)

        Truth.assertThat(builder.buildFqn(info)).isEqualTo(expectedClassName)
    }
}