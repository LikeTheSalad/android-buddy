package com.likethesalad.android

import com.google.common.truth.Truth
import com.likethesalad.android.tools.AndroidBuddyAppConfig
import com.likethesalad.android.tools.AndroidBuddyLibraryConfig
import com.likethesalad.tools.functional.testing.AndroidProjectTest
import com.likethesalad.tools.functional.testing.app.layout.AndroidAppProjectDescriptor
import com.likethesalad.tools.functional.testing.layout.AndroidLibProjectDescriptor
import com.likethesalad.tools.functional.testing.layout.items.impl.plugins.GradlePluginDeclaration
import com.likethesalad.tools.functional.testing.utils.TestAssetsProvider
import net.lingala.zip4j.ZipFile
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.TimeUnit

class AndroidBuddyFunctionalTest : AndroidProjectTest() {

    private val inputAssetsProvider = TestAssetsProvider("test", "consumer")

    companion object {
        private const val ANDROID_PLUGIN_VERSION = "4.2.0"
        private const val GRADLE_VERSION = "6.7.1"
        private const val CONSUMER_PLUGIN_ID = "com.likethesalad.android-buddy"

        @get:ClassRule
        @JvmStatic
        val classTempDir = TemporaryFolder()

        lateinit var dexToolsDir: File

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            val toolsDir = TestAssetsProvider("test", "tools")
            val dexToolsZip = toolsDir.getAssetFile("dex-tools.zip")
            val dexToolsExtractDir = classTempDir.newFolder("dex-tools-extracted")

            ZipFile(dexToolsZip).extractAll(dexToolsExtractDir.absolutePath)

            dexToolsDir = dexToolsExtractDir.listFiles()?.first()!!
        }
    }

    @Test
    fun `Check app java class instrumentation`() {
        val projectName = "basic"

        val appDescriptor = createAppProjectDescriptor(projectName)

        createProjectAndBuild(appDescriptor, listOf("assembleDebug"))

        val classLoader = getAppClassloader(projectName)
        val helloClass = classLoader.loadClass("com.thepackage.Hello")
        val getMessage = helloClass.getDeclaredMethod("getMessage")
        val helloInstance = helloClass.newInstance()
        val message = getMessage.invoke(helloInstance) as String

        Truth.assertThat(message).isEqualTo("Instrumented message")
    }

    @Test
    fun `Check app kotlin class instrumentation`() {
        val projectName = "basic_kotlin"

        val appDescriptor = createAppProjectDescriptor(projectName)
        appDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("org.jetbrains.kotlin.android"))

        createProjectAndBuild(appDescriptor, listOf("assembleDebug"))

        val classLoader = getAppClassloader(projectName)
        val helloClass = classLoader.loadClass("com.thepackage.HelloK")
        val getMessage = helloClass.getDeclaredMethod("getMessage")
        val helloInstance = helloClass.newInstance()
        val message = getMessage.invoke(helloInstance) as String

        Truth.assertThat(message).isEqualTo("Instrumented message")
    }

    @Test
    fun `Check app with lib with instrumentation`() {
        val projectName = "basic_with_lib"
        val libProjectName = "basic_lib"

        val appDescriptor = createAppProjectDescriptor(projectName)
        appDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("org.jetbrains.kotlin.android"))
        val libDescriptor = createLibraryProjectDescriptor(
            libProjectName, AndroidBuddyLibraryConfig(
                "some-id",
                listOf("com.transformations.BasicLibTransformation")
            )
        )
        appDescriptor.dependenciesBlock.addDependency("implementation project(':$libProjectName')")
        createProject(libDescriptor)

        createProjectAndBuild(appDescriptor, listOf("assembleDebug"))

        val classLoader = getAppClassloader(projectName)
        val helloClass = classLoader.loadClass("com.thepackage.HelloLib")
        val getMessage = helloClass.getDeclaredMethod("getMessage")
        val helloInstance = helloClass.newInstance()
        val message = getMessage.invoke(helloInstance) as String

        Truth.assertThat(message).isEqualTo("Instrumented message in lib")
    }

    @Test
    fun `Check app with lib with instrumentation is ignored when configured to use no transformations from libs`() {
        val projectName = "app_with_lib_ignored"
        val libProjectName = "ignored_lib"

        val appDescriptor = createAppProjectDescriptor(
            projectName,
            AndroidBuddyAppConfig(AndroidBuddyAppConfig.LibrariesPolicyConfig.IgnoreAll)
        )
        appDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("org.jetbrains.kotlin.android"))
        val libDescriptor = createLibraryProjectDescriptor(
            libProjectName, AndroidBuddyLibraryConfig(
                "some-id",
                listOf("com.transformations.BasicLibTransformation")
            )
        )
        appDescriptor.dependenciesBlock.addDependency("implementation project(':$libProjectName')")
        createProject(libDescriptor)

        createProjectAndBuild(appDescriptor, listOf("assembleDebug"))

        val classLoader = getAppClassloader(projectName)
        val helloClass = classLoader.loadClass("com.thepackage.HelloLib")
        val getMessage = helloClass.getDeclaredMethod("getMessage")
        val helloInstance = helloClass.newInstance()
        val message = getMessage.invoke(helloInstance) as String

        Truth.assertThat(message).isEqualTo("Not modified message")
    }

    @Test
    fun `Check applying transformations from multiple libraries`() {
        val projectName = "app_with_multiple_libs"
        val libProjectName1 = "multiple_lib_1"
        val libProjectName2 = "multiple_lib_2"

        val appDescriptor = createAppProjectDescriptor(projectName)
        appDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("org.jetbrains.kotlin.android"))
        val libDescriptor1 = createLibraryProjectDescriptor(
            libProjectName1, AndroidBuddyLibraryConfig(
                "some-id1",
                listOf("com.transformations.BasicLibTransformation1")
            )
        )
        val libDescriptor2 = createLibraryProjectDescriptor(
            libProjectName2, AndroidBuddyLibraryConfig(
                "some-id2",
                listOf("com.transformations.BasicLibTransformation2")
            )
        )
        appDescriptor.dependenciesBlock.addDependency("implementation project(':$libProjectName1')")
        appDescriptor.dependenciesBlock.addDependency("implementation project(':$libProjectName2')")
        createProject(libDescriptor1)
        createProject(libDescriptor2)

        createProjectAndBuild(appDescriptor, listOf("assembleDebug"))

        val classLoader = getAppClassloader(projectName)
        val helloClass = classLoader.loadClass("com.thepackage.HelloLibs")
        val getMessage1 = helloClass.getDeclaredMethod("getMessage")
        val getMessage2 = helloClass.getDeclaredMethod("getMessage2")
        val helloInstance = helloClass.newInstance()
        val message1 = getMessage1.invoke(helloInstance) as String
        val message2 = getMessage2.invoke(helloInstance) as String

        Truth.assertThat(message1).isEqualTo("Instrumented message in lib1")
        Truth.assertThat(message2).isEqualTo("Instrumented message in lib2")
    }

    @Test
    fun `Check applying transformations from selected libraries only`() {
        val projectName = "app_with_selected_libs"
        val libProjectName1 = "selected_lib_1"
        val libProjectName2 = "selected_lib_2"

        val appDescriptor = createAppProjectDescriptor(
            projectName,
            AndroidBuddyAppConfig(AndroidBuddyAppConfig.LibrariesPolicyConfig.UseOnly(listOf("some-id2")))
        )
        appDescriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("org.jetbrains.kotlin.android"))
        val libDescriptor1 = createLibraryProjectDescriptor(
            libProjectName1, AndroidBuddyLibraryConfig(
                "some-id1",
                listOf("com.transformations.BasicLibTransformation1")
            )
        )
        val libDescriptor2 = createLibraryProjectDescriptor(
            libProjectName2, AndroidBuddyLibraryConfig(
                "some-id2",
                listOf("com.transformations.BasicLibTransformation2")
            )
        )
        appDescriptor.dependenciesBlock.addDependency("implementation project(':$libProjectName1')")
        appDescriptor.dependenciesBlock.addDependency("implementation project(':$libProjectName2')")
        createProject(libDescriptor1)
        createProject(libDescriptor2)

        createProjectAndBuild(appDescriptor, listOf("assembleDebug"))

        val classLoader = getAppClassloader(projectName)
        val helloClass = classLoader.loadClass("com.thepackage.HelloLibs")
        val getMessage1 = helloClass.getDeclaredMethod("getMessage")
        val getMessage2 = helloClass.getDeclaredMethod("getMessage2")
        val helloInstance = helloClass.newInstance()
        val message1 = getMessage1.invoke(helloInstance) as String
        val message2 = getMessage2.invoke(helloInstance) as String

        Truth.assertThat(message1).isEqualTo("Not modified message")
        Truth.assertThat(message2).isEqualTo("Instrumented message in lib2")
    }

    @Test
    fun `Test android lib transformation`() {
        val libName = "some_consumer_lib"
        val libDescriptor = createLibraryConsumerProjectDescriptor(libName)

        createProjectAndBuild(libDescriptor, listOf("assembleDebug"))

        val classLoader = getLibClassloader(libName)
        val helloClass = classLoader.loadClass("com.thepackage.Hello")
        val getMessage = helloClass.getDeclaredMethod("getMessage")
        val helloInstance = helloClass.newInstance()
        val message = getMessage.invoke(helloInstance) as String

        Truth.assertThat(message).isEqualTo("Instrumented message")
    }

    private fun getAppClassloader(projectName: String): ClassLoader {
        val jarFile = extractJarFromProject(projectName)
        return URLClassLoader(arrayOf(jarFile.toURI().toURL()), javaClass.classLoader)
    }

    private fun getLibClassloader(libProjectName: String): ClassLoader {
        val jarFile = extractJarFromLibProject(libProjectName)
        return URLClassLoader(arrayOf(jarFile.toURI().toURL()), javaClass.classLoader)
    }

    private fun extractJarFromLibProject(libProjectName: String): File {
        val projectDir = getProjectDir(libProjectName)
        val aarDir = File(projectDir, "build/outputs/aar")
        val aarFile = File(aarDir, "$libProjectName-debug.aar")
        val jarFileName = "classes.jar"
        val jarFile = File(aarDir, jarFileName)
        ZipFile(aarFile).extractFile(jarFileName, aarDir.absolutePath)

        return jarFile
    }

    private fun extractJarFromProject(projectName: String): File {
        val projectDir = getProjectDir(projectName)
        val apkDir = File(projectDir, "build/outputs/apk/debug")
        val apkPath = File(apkDir, "$projectName-debug.apk")

        return extractJarFromApk(apkPath)
    }

    private fun createAppProjectDescriptor(
        projectName: String,
        config: AndroidBuddyAppConfig? = null
    ): AndroidAppProjectDescriptor {
        val descriptor = AndroidAppProjectDescriptor(
            projectName,
            getInputTestAsset(projectName),
            ANDROID_PLUGIN_VERSION,
            config?.let { listOf(it) } ?: emptyList()
        )
        descriptor.pluginsBlock.addPlugin(GradlePluginDeclaration(CONSUMER_PLUGIN_ID))
        return descriptor
    }

    private fun createLibraryProjectDescriptor(
        projectName: String,
        config: AndroidBuddyLibraryConfig
    ): AndroidLibProjectDescriptor {
        val descriptor = AndroidLibProjectDescriptor(
            projectName,
            getInputTestAsset(projectName),
            ANDROID_PLUGIN_VERSION,
            listOf(config)
        )
        descriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("com.likethesalad.android-buddy-library"))
        return descriptor
    }

    private fun createLibraryConsumerProjectDescriptor(
        projectName: String
    ): AndroidLibProjectDescriptor {
        val descriptor = AndroidLibProjectDescriptor(
            projectName,
            getInputTestAsset(projectName),
            ANDROID_PLUGIN_VERSION
        )
        descriptor.pluginsBlock.addPlugin(GradlePluginDeclaration(CONSUMER_PLUGIN_ID))
        return descriptor
    }

    private fun extractJarFromApk(apkFile: File): File {
        val destinationDir = apkFile.parentFile
        val dexFileName = "classes.dex"
        ZipFile(apkFile).extractFile(dexFileName, destinationDir.absolutePath)

        val dexFile = File(destinationDir, dexFileName)

        return convertDexToJar(dexFile)
    }

    private fun convertDexToJar(dexFile: File): File {
        val jarFile = File(dexFile.parentFile, "classes.jar")
        val dex2jarScript = File(dexToolsDir, "d2j-dex2jar.sh")
        val processBuilder = ProcessBuilder(
            "sh",
            dex2jarScript.absolutePath,
            "-f",
            dexFile.absolutePath,
            "-o",
            jarFile.absolutePath
        )

        val javaHomeDir = System.getenv("JAVA_HOME")

        if (javaHomeDir.isNullOrEmpty()) {
            throw IllegalStateException("JAVA_HOME not set")
        }

        processBuilder.environment()["PATH"] =
            "${System.getenv("PATH")}${File.pathSeparator}$javaHomeDir/bin"

        val process = processBuilder.start()

        process.waitFor(10, TimeUnit.SECONDS)

        return jarFile
    }

    override fun getGradleVersion(): String = GRADLE_VERSION

    private fun getInputTestAsset(inputDirName: String): File {
        return inputAssetsProvider.getAssetFile(inputDirName)
    }
}