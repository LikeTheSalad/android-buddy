package com.likethesalad.android.buddy.modules.transform.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.libraries.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.buddy.modules.libraries.models.LibraryPluginsExtracted
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.bytebuddy.build.Plugin
import net.bytebuddy.description.type.TypeDescription
import net.bytebuddy.dynamic.ClassFileLocator
import net.bytebuddy.dynamic.DynamicType
import org.junit.Before
import org.junit.Test
import java.io.File

class PluginFactoriesProviderTest : BaseMockable() {

    @MockK
    lateinit var instantiatorWrapper: InstantiatorWrapper

    @MockK
    lateinit var byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator

    @MockK
    lateinit var logger: Logger

    @MockK
    lateinit var projectLoggerProvider: ProjectLoggerProvider

    @MockK
    lateinit var projectLogger: org.gradle.api.logging.Logger

    @MockK
    lateinit var androidBuddyLibraryPluginsExtractor: AndroidBuddyLibraryPluginsExtractor

    @MockK
    lateinit var loggerArgumentResolver: Plugin.Factory.UsingReflection.ArgumentResolver

    @MockK
    lateinit var localPluginsExtractor: LocalPluginsExtractor

    private lateinit var pluginFactoriesProvider: PluginFactoriesProvider

    @Before
    fun setUp() {
        every { projectLoggerProvider.getLogger() }.returns(projectLogger)
        every {
            byteBuddyClassesInstantiator.makeFactoryArgumentResolverFor(
                org.gradle.api.logging.Logger::class.java,
                projectLogger
            )
        }.returns(loggerArgumentResolver)
        pluginFactoriesProvider = PluginFactoriesProvider(
            instantiatorWrapper,
            byteBuddyClassesInstantiator,
            androidBuddyLibraryPluginsExtractor,
            localPluginsExtractor,
            logger,
            projectLoggerProvider
        )
    }

    @Test
    fun `Get factories from local and external classpaths`() {
        val androidBuddyJar = mockk<File>()
        val normalJar = mockk<File>()
        val librariesJars = setOf(normalJar, androidBuddyJar)
        val localDirs = setOf<File>(mockk())
        val classLoader = mockk<ClassLoader>()
        val librariesJarsProvider = mockk<LibrariesJarsProvider>()

        val name1 = createNameAndPluginAndFactory(SomePlugin1::class.java, classLoader)
        val name2 = createNameAndPluginAndFactory(SomePlugin2::class.java, classLoader)
        val libName1 = createNameAndPluginAndFactory(SomeLibPlugin1::class.java, classLoader)
        val libraryPluginsExtracted = LibraryPluginsExtracted(setOf(libName1.name), setOf(androidBuddyJar))

        every { librariesJarsProvider.getLibrariesJars() }.returns(librariesJars)
        every {
            androidBuddyLibraryPluginsExtractor.extractLibraryPlugins(librariesJars)
        }.returns(libraryPluginsExtracted)
        every {
            localPluginsExtractor.getLocalPluginNames(localDirs)
        }.returns(setOf(name1.name, name2.name))

        val factories = pluginFactoriesProvider.getFactories(localDirs, librariesJarsProvider, classLoader)

        Truth.assertThat(factories)
            .containsExactly(name1.expectedFactory, name2.expectedFactory, libName1.expectedFactory)
        verify {
            logger.debug("Local transformations found: {}", setOf(name1.name, name2.name))
            logger.debug("Dependencies transformations found: {}", setOf(libName1.name))
            name1.expectedFactory.with(loggerArgumentResolver)
            name2.expectedFactory.with(loggerArgumentResolver)
            libName1.expectedFactory.with(loggerArgumentResolver)
        }
    }

    private fun createNameAndPluginAndFactory(
        clazz: Class<out Plugin>,
        classLoader: ClassLoader
    ): ClassNameAndPluginAndFactory {
        val name = clazz.name
        val factory = mockk<Plugin.Factory.UsingReflection>()

        every {
            instantiatorWrapper.getClassForName<Plugin>(name, false, classLoader)
        }.returns(clazz)
        every {
            byteBuddyClassesInstantiator.makeFactoryUsingReflection(clazz)
        }.returns(factory)
        every {
            factory.with(any<Plugin.Factory.UsingReflection.ArgumentResolver>())
        }.returns(factory)

        return ClassNameAndPluginAndFactory(name, factory)
    }

    class ClassNameAndPluginAndFactory(
        val name: String,
        val expectedFactory: Plugin.Factory.UsingReflection
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