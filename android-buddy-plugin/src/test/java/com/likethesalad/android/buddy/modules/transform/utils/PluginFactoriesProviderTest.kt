package com.likethesalad.android.buddy.modules.transform.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.libraries.AndroidBuddyLibraryPluginsExtractor
import com.likethesalad.android.buddy.modules.libraries.models.LibraryPluginsExtracted
import com.likethesalad.android.buddy.providers.LibrariesJarsProvider
import com.likethesalad.android.buddy.utils.ClassLoaderCreator
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

    @MockK
    lateinit var classLoaderCreator: ClassLoaderCreator

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
            classLoaderCreator,
            logger,
            projectLoggerProvider
        )
    }

    @Test
    fun `Get factories from local and external classpaths`() {
        val androidBuddyJar = mockk<File>()
        val normalJar = mockk<File>()
        val librariesWithPlugins = setOf(androidBuddyJar)
        val librariesWithoutPlugins = setOf(normalJar)
        val baseClassLoader = mockk<ClassLoader>()
        val factoryClassLoader = createFactoryClassLoaderMock(
            baseClassLoader, librariesWithPlugins,
            librariesWithoutPlugins
        )

        val localFactoryWrappers =
            createLibraryFactoryWrappers(factoryClassLoader, LocalPlugin1::class.java, LocalPlugin2::class.java)
        val libFactoryWrappers = createLibraryFactoryWrappers(factoryClassLoader, LibraryPlugin1::class.java)
        val localTransformationNames = localFactoryWrappers.map { it.name }.toSet()
        val libTransformationNames = libFactoryWrappers.map { it.name }.toSet()

        verifyFactoriesProvided(
            librariesWithPlugins, librariesWithoutPlugins, baseClassLoader,
            localFactoryWrappers + libFactoryWrappers
        )

        verify {
            logger.debug("Local transformations found: {}", localTransformationNames)
            logger.debug("Dependencies transformations found: {}", libTransformationNames)
        }
    }

    @Test
    fun `Get factories from local and external classpaths with no library plugins`() {
        val normalJar = mockk<File>()
        val librariesWithPlugins = emptySet<File>()
        val librariesWithoutPlugins = setOf(normalJar)
        val baseClassLoader = mockk<ClassLoader>()
        val factoryClassLoader = createFactoryClassLoaderMock(
            baseClassLoader, librariesWithPlugins,
            librariesWithoutPlugins
        )

        val localFactoryWrappers =
            createLibraryFactoryWrappers(factoryClassLoader, LocalPlugin1::class.java, LocalPlugin2::class.java)
        val localTransformationNames = localFactoryWrappers.map { it.name }.toSet()

        verifyFactoriesProvided(
            librariesWithPlugins, librariesWithoutPlugins, baseClassLoader,
            localFactoryWrappers
        )

        verify {
            logger.debug("Local transformations found: {}", localTransformationNames)
        }
        verify(exactly = 0) {
            logger.debug("Dependencies transformations found: {}", any())
        }
    }

    private fun createFactoryClassLoaderMock(
        baseClassLoader: ClassLoader,
        librariesWithPlugins: Set<File>,
        librariesWithoutPlugins: Set<File>
    ): ClassLoader {
        val factoryClassLoader = mockk<ClassLoader>()
        if (librariesWithPlugins.isNotEmpty()) {
            val pluginsClassloader = mockk<ClassLoader>()
            every {
                classLoaderCreator.create(librariesWithPlugins, baseClassLoader)
            }.returns(pluginsClassloader)
            every {
                classLoaderCreator.create(librariesWithoutPlugins, pluginsClassloader)
            }.returns(factoryClassLoader)
        } else {
            every {
                classLoaderCreator.create(librariesWithoutPlugins, baseClassLoader)
            }.returns(factoryClassLoader)
        }

        return factoryClassLoader
    }

    private fun verifyFactoriesProvided(
        androidBuddyLibraries: Set<File>,
        normalLibraries: Set<File>,
        baseClassLoader: ClassLoader,
        expectedFactoryWrappers: List<FactoryWrapper>
    ) {
        val librariesJars = normalLibraries + androidBuddyLibraries
        val libraryFactoryWrappers =
            expectedFactoryWrappers.filter { LibraryPlugin::class.java.isAssignableFrom(it.pluginClass) }
        val localFactoryWrappers =
            expectedFactoryWrappers.filter { LocalPlugin::class.java.isAssignableFrom(it.pluginClass) }
        val allFactories = expectedFactoryWrappers.map { it.expectedFactory }
        val librariesJarsProvider = mockk<LibrariesJarsProvider>()
        val localDirs = setOf<File>(mockk())
        val localPluginNames = localFactoryWrappers.map { it.name }.toSet()
        val libraryPluginNames = libraryFactoryWrappers.map { it.name }.toSet()

        val libraryPluginsExtracted = LibraryPluginsExtracted(
            libraryPluginNames,
            androidBuddyLibraries
        )

        every { librariesJarsProvider.getLibrariesJars() }.returns(librariesJars)
        every {
            androidBuddyLibraryPluginsExtractor.extractLibraryPlugins(librariesJars)
        }.returns(libraryPluginsExtracted)
        every {
            localPluginsExtractor.getLocalPluginNames(localDirs)
        }.returns(localPluginNames)

        val factories = pluginFactoriesProvider.getFactories(localDirs, librariesJarsProvider, baseClassLoader)

        Truth.assertThat(factories).containsExactlyElementsIn(allFactories)
        verify {
            for (factory in allFactories) {
                factory.with(loggerArgumentResolver)
            }
        }
    }

    private fun createLibraryFactoryWrappers(
        classLoader: ClassLoader,
        vararg classes: Class<out BaseMockPlugin>
    ): List<FactoryWrapper> {
        val wrappers = mutableListOf<FactoryWrapper>()
        for (onePluginClass in classes) {
            wrappers.add(createFactoryWrapper(onePluginClass, classLoader))
        }
        return wrappers
    }

    private fun createFactoryWrapper(
        clazz: Class<out BaseMockPlugin>,
        classLoader: ClassLoader
    ): FactoryWrapper {
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

        return FactoryWrapper(name, clazz, factory)
    }

    class FactoryWrapper(
        val name: String,
        val pluginClass: Class<out BaseMockPlugin>,
        val expectedFactory: Plugin.Factory.UsingReflection
    )

    abstract class LocalPlugin : BaseMockPlugin()
    abstract class LibraryPlugin : BaseMockPlugin()

    class LocalPlugin1 : LocalPlugin()
    class LocalPlugin2 : LocalPlugin()
    class LibraryPlugin1 : LibraryPlugin()
    class LibraryPlugin2 : LibraryPlugin()

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