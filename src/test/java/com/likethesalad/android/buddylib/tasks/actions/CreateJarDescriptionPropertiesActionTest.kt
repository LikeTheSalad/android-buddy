package com.likethesalad.android.buddylib.tasks.actions

import com.google.common.truth.Truth
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
    fun `Store plugin class names into a properties file`() {
        val pluginNames = setOf("some.class.Name", "some.other.class.Name")

        val action = CreateJarDescriptionPropertiesAction(pluginNames, outputDir)
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

    private fun getGeneratedPropertiesFile(): File {
        return File(outputDir, "plugins.properties")
    }
}