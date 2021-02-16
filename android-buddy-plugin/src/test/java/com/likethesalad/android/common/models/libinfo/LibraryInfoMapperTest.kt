package com.likethesalad.android.common.models.libinfo

import com.google.common.truth.Truth
import com.likethesalad.android.common.models.libinfo.utils.AndroidBuddyLibraryInfoFqnBuilder
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader
import org.junit.Before
import org.junit.Test

class LibraryInfoMapperTest : BaseMockable() {

    @MockK
    lateinit var fqnBuilder: AndroidBuddyLibraryInfoFqnBuilder

    private val byteArrayClassLoaderUtil = ByteArrayClassLoaderUtil(ByteBuddyClassesInstantiator())

    private lateinit var mapper: LibraryInfoMapper

    @Before
    fun setUp() {
        mapper = LibraryInfoMapper(fqnBuilder, byteArrayClassLoaderUtil)
    }

    @Test
    fun `Convert info to byte array class`() {
        val id = "SomeId"
        val group = "some.group"
        val name = "some-project-name"
        val plugins = setOf("one.Class", "another.Class")
        val info = AndroidBuddyLibraryInfo(id, group, name, plugins)
        val pluginNamesString = "one.Class,another.Class"
        val expectedClassName = "some.group.some_project_name.library_definition"
        every { fqnBuilder.buildFqn(info) }.returns(expectedClassName)

        val namedClassInfo = mapper.convertToClassByteArray(info)

        val theClass = loadClassFromByteArray(namedClassInfo)
        val theInstance = theClass.newInstance()

        Truth.assertThat(callNoArgsStringMethodByReflection("getId", theInstance))
            .isEqualTo(id)
        Truth.assertThat(callNoArgsStringMethodByReflection("getGroup", theInstance))
            .isEqualTo(group)
        Truth.assertThat(callNoArgsStringMethodByReflection("getName", theInstance))
            .isEqualTo(name)
        Truth.assertThat(callNoArgsStringMethodByReflection("getPluginNames", theInstance))
            .isEqualTo(pluginNamesString)
        Truth.assertThat(namedClassInfo.name).isEqualTo(expectedClassName)
    }

    @Test
    fun `Convert byte array class into info`() {
        val id = "SomeId"
        val group = "some.group"
        val name = "some-project-name"
        val plugins = setOf("one.Class", "another.Class")
        val info = AndroidBuddyLibraryInfo(id, group, name, plugins)
        val expectedClassName = "some.group.some_project_name.library_definition"
        every { fqnBuilder.buildFqn(info) }.returns(expectedClassName)

        val namedClassInfo = mapper.convertToClassByteArray(info)

        Truth.assertThat(mapper.convertToAndroidBuddyLibraryInfo(namedClassInfo))
            .isEqualTo(info)
    }

    private fun loadClassFromByteArray(namedClassInfo: NamedClassInfo): Class<*> {
        val loader = ByteArrayClassLoader(
            javaClass.classLoader,
            mapOf(namedClassInfo.name to namedClassInfo.byteArray)
        )
        return loader.loadClass(namedClassInfo.name) as Class<*>
    }

    private fun callNoArgsStringMethodByReflection(
        name: String,
        fromInstance: Any
    ): String {
        return fromInstance.javaClass.getDeclaredMethod(name).invoke(fromInstance) as String
    }
}