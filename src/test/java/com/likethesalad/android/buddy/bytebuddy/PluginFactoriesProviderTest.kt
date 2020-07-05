package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesMaker
import com.likethesalad.android.buddy.testutils.BaseMockable
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.buddy.utils.InstantiatorWrapper
import com.likethesalad.android.buddy.utils.PluginClassNamesProvider
import io.mockk.every
import io.mockk.impl.annotations.MockK
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import org.junit.Before
import org.junit.Test
import java.io.File

class PluginFactoriesProviderTest : BaseMockable() {

    @MockK
    lateinit var pluginClassNamesProvider: PluginClassNamesProvider

    @MockK
    lateinit var classLoaderCreator: ClassLoaderCreator

    @MockK
    lateinit var instantiatorWrapper: InstantiatorWrapper

    @MockK
    lateinit var byteBuddyClassesMaker: ByteBuddyClassesMaker

    private lateinit var pluginFactoriesProvider: PluginFactoriesProvider

    @Before
    fun setUp() {
        pluginFactoriesProvider = PluginFactoriesProvider(
            pluginClassNamesProvider, classLoaderCreator,
            instantiatorWrapper, byteBuddyClassesMaker
        )
    }

    @Test
    fun `Get factories from folders`() {
        val folders = setOf<File>(mockk(), mockk())
        val classLoader = mockk<ClassLoader>()
        val name1 = createNameAndPluginAndFactory(SomePlugin1::class.java, classLoader)
        val name2 = createNameAndPluginAndFactory(SomePlugin2::class.java, classLoader)
        every {
            classLoaderCreator.create(folders, any())
        }.returns(classLoader)
        every { pluginClassNamesProvider.getPluginClassNames() }.returns(
            setOf(name1.name, name2.name)
        )

        val factories = pluginFactoriesProvider.getFactories(folders)

        Truth.assertThat(factories)
            .containsExactly(name1.expectedFactory, name2.expectedFactory)
    }

    private fun createNameAndPluginAndFactory(
        clazz: Class<out Plugin>,
        classLoader: ClassLoader
    ): ClassNameAndPluginAndFactory {
        val name = clazz.name
        val factory = mockk<Plugin.Factory>()

        every {
            instantiatorWrapper.getClassForName<Plugin>(name, false, classLoader)
        }.returns(clazz)
        every {
            byteBuddyClassesMaker.makeFactoryUsingReflection(clazz)
        }.returns(factory)

        return ClassNameAndPluginAndFactory(name, factory)
    }

    class ClassNameAndPluginAndFactory(
        val name: String,
        val expectedFactory: Plugin.Factory
    )

    class SomePlugin1 : BaseMockPlugin()
    class SomePlugin2 : BaseMockPlugin()

    open class BaseMockPlugin : Plugin {
        override fun apply(
            builder: DynamicType.Builder<*>?,
            typeDescription: TypeDescription?,
            classFileLocator: ClassFileLocator?
        ): DynamicType.Builder<*> {
            // Nothing to do
            throw UnsupportedOperationException()
        }

        override fun close() {
            // Nothing to do
        }

        override fun matches(target: TypeDescription?): Boolean {
            // Nothing to do
            throw UnsupportedOperationException()
        }
    }
}