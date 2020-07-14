package com.likethesalad.android.buddy.bytebuddy

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.providers.PluginClassNamesProvider
import com.likethesalad.android.buddy.utils.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
import com.likethesalad.android.buddy.utils.FilesHolder
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.testutils.BaseMockable
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
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    @MockK
    lateinit var androidBuddyLibraryPluginsExtractor: AndroidBuddyLibraryPluginsExtractor

    private lateinit var pluginFactoriesProvider: PluginFactoriesProvider

    @Before
    fun setUp() {
        pluginFactoriesProvider = PluginFactoriesProvider(
            pluginClassNamesProvider, classLoaderCreator,
            instantiatorWrapper, byteBuddyClassesInstantiator, androidBuddyLibraryPluginsExtractor
        )
    }

    @Test
    fun `Get factories from local and external classpath`() {
        val allFiles = setOf<File>(mockk(), mockk())
        val jarFiles = setOf<File>(mockk(), mockk())
        val classpath = mockk<FilesHolder>()
        val classLoader = mockk<ClassLoader>()
        val name1 = createNameAndPluginAndFactory(SomePlugin1::class.java, classLoader)
        val name2 = createNameAndPluginAndFactory(SomePlugin2::class.java, classLoader)
        val libName1 = createNameAndPluginAndFactory(SomeLibPlugin1::class.java, classLoader)
        every {
            classLoaderCreator.create(allFiles, any())
        }.returns(classLoader)
        every { classpath.allFiles }.returns(allFiles)
        every { classpath.jarFiles }.returns(jarFiles)
        every { pluginClassNamesProvider.getPluginClassNames() }.returns(
            setOf(name1.name, name2.name)
        )
        every {
            androidBuddyLibraryPluginsExtractor.extractPluginNames(jarFiles)
        }.returns(setOf(libName1.name))

        val factories = pluginFactoriesProvider.getFactories(classpath)

        Truth.assertThat(factories)
            .containsExactly(name1.expectedFactory, name2.expectedFactory, libName1.expectedFactory)
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
            byteBuddyClassesInstantiator.makeFactoryUsingReflection(clazz)
        }.returns(factory)

        return ClassNameAndPluginAndFactory(name, factory)
    }

    class ClassNameAndPluginAndFactory(
        val name: String,
        val expectedFactory: Plugin.Factory
    )

    class SomePlugin1 : BaseMockPlugin()
    class SomePlugin2 : BaseMockPlugin()
    class SomeLibPlugin1 : BaseMockPlugin()

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