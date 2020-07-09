package com.likethesalad.android.buddylib.tasks.actions

import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.testutils.BaseMockable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Properties

class CreateJarDescriptionPropertiesActionTest : BaseMockable() {

    @get:Rule
    val temporaryFolderRule = TemporaryFolder()

    private lateinit var outputDir: File
    private val outputDirName = "android-buddy"

    @Before
    fun setUp() {
        outputDir = temporaryFolderRule.newFolder(outputDirName)
    }

    @Test
    fun `Store multiple plugin class names into a properties file`() {
        val pluginNames = setOf("some.class.Name", "some.other.class.Name")

        val action = createInstance(pluginNames)
        action.execute()

        val storedProperties = Properties()
        val generatedFile = getGeneratedPropertiesFile()
        generatedFile.reader().use {
            storedProperties.load(it)
            Truth.assertThat(storedProperties.count()).isEqualTo(1)
            Truth.assertThat(storedProperties.getProperty("plugin-classes"))
                .isEqualTo("some.class.Name,some.other.class.Name")
        }
    }

    @Test
    fun `Store empty plugin class names into a properties file`() {
        val pluginNames = setOf<String>()

        val action = createInstance(pluginNames)
        action.execute()

        val storedProperties = Properties()
        val generatedFile = getGeneratedPropertiesFile()
        generatedFile.reader().use {
            storedProperties.load(it)
            Truth.assertThat(storedProperties.count()).isEqualTo(1)
            Truth.assertThat(storedProperties.getProperty("plugin-classes")).isEmpty()
        }
    }

    @Test
    fun `Store one plugin class names into a properties file`() {
        val pluginNames = setOf("the.one.Class")

        val action = createInstance(pluginNames)
        action.execute()

        val storedProperties = Properties()
        val generatedFile = getGeneratedPropertiesFile()
        generatedFile.reader().use {
            storedProperties.load(it)
            Truth.assertThat(storedProperties.count()).isEqualTo(1)
            Truth.assertThat(storedProperties.getProperty("plugin-classes"))
                .isEqualTo("the.one.Class")
        }
    }

    @Test
    fun `Empty output dir before writing new file`() {
        val randomFile = File(outputDir, "someFile.txt")
        randomFile.createNewFile()
        Truth.assertThat(outputDir.listFiles()).asList().containsExactly(randomFile)

        val pluginNames = setOf("the.one.Class")
        val action = createInstance(pluginNames)
        action.execute()

        Truth.assertThat(outputDir.listFiles()).asList().containsExactly(getGeneratedPropertiesFile())
    }

    private fun getGeneratedPropertiesFile(): File {
        return File(outputDir, "plugins.properties")
    }

    private fun createInstance(pluginNames: Set<String>)
            : CreateJarDescriptionPropertiesAction {
        return CreateJarDescriptionPropertiesAction(
            pluginNames, outputDir,
            DirectoryCleaner()
        )
    }
}