package com.likethesalad.android.buddylib.actions

import com.google.common.truth.Truth
import com.likethesalad.android.buddylib.modules.createproperties.CreateAndroidBuddyLibraryPropertiesAction
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Properties

class CreateAndroidBuddyLibraryPropertiesActionTest : BaseMockable() {

    @get:Rule
    val temporaryFolderRule = TemporaryFolder()

    @MockK
    lateinit var logger: Logger

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
        verifyCommonSuccessActions(pluginNames)
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
        verifyCommonSuccessActions(pluginNames)
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
        verifyCommonSuccessActions(pluginNames)
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
        verifyCommonSuccessActions(pluginNames)
    }

    private fun verifyCommonSuccessActions(pluginNames: Set<String>) {
        verify {
            logger.debug("Plugins found: {}", pluginNames)
        }
    }

    private fun getGeneratedPropertiesFile(): File {
        return File(outputDir, "plugins.properties")
    }

    private fun createInstance(pluginNames: Set<String>)
            : CreateAndroidBuddyLibraryPropertiesAction {
        return CreateAndroidBuddyLibraryPropertiesAction(
            DirectoryCleaner(), logger, pluginNames, outputDir
        )
    }
}