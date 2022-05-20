package com.likethesalad.android.functional

import com.likethesalad.tools.functional.testing.AndroidProjectTest
import com.likethesalad.tools.functional.testing.app.layout.AndroidAppProjectDescriptor
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
    fun `Check app class instrumentation`() {
        val projectName = "basic"

        val appDescriptor = createAppProjectDescriptor(projectName)

        val result = createProjectAndBuild(appDescriptor, listOf("assembleDebug"))

        verifyResultContainsLine(result, "> Task :$projectName:transformClassesWithAndroidBuddyForDebug")

        val classLoader = getAppClassloader(projectName)
        val helloClass = classLoader.loadClass("Hello")
        val getMessage = helloClass.getDeclaredMethod("getMessage")
        val helloInstance = helloClass.newInstance()
        val message = getMessage.invoke(helloInstance) as String

        println("Message: $message")
    }

    private fun getAppClassloader(projectName: String): ClassLoader {
        val jarFile = extractJarFromProject(projectName)
        return URLClassLoader(arrayOf(jarFile.toURI().toURL()), javaClass.classLoader)
    }

    private fun extractJarFromProject(projectName: String): File {
        val projectDir = getProjectDir(projectName)
        val apkDir = File(projectDir, "build/outputs/apk/debug")
        val apkPath = File(apkDir, "$projectName-debug.apk")

        return extractJar(apkPath)
    }

    private fun createAppProjectDescriptor(projectName: String): AndroidAppProjectDescriptor {
        val descriptor = AndroidAppProjectDescriptor(
            projectName,
            getInputTestAsset(projectName),
            ANDROID_PLUGIN_VERSION
        )
        descriptor.pluginsBlock.addPlugin(GradlePluginDeclaration("com.likethesalad.android-buddy"))
        return descriptor
    }

    private fun extractJar(apkFile: File): File {
        val destinationDir = apkFile.parentFile
        val dexFileName = "classes.dex"
        ZipFile(apkFile).extractFile(dexFileName, destinationDir.absolutePath)

        val dexFile = File(destinationDir, dexFileName)

        return convertDextToJar(dexFile)
    }

    private fun convertDextToJar(dexFile: File): File {
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