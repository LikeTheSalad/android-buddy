package com.likethesalad.android.buddylib.modules.createmetadata.action

import com.google.common.truth.Truth
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

class CreateAndroidBuddyLibraryMetadataActionTest : BaseMockable() {

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
        val expectedMetaInfDir = File(outputDir, "META-INF")
        val expectedAndroidBuddyDir = File(expectedMetaInfDir, "android-buddy-plugins")
        randomFile.createNewFile()
        Truth.assertThat(outputDir.listFiles()).asList().containsExactly(randomFile)

        val pluginNames = setOf("the.one.Class")
        val action = createInstance(pluginNames)
        action.execute()


        Truth.assertThat(outputDir.listFiles()).asList().containsExactly(expectedMetaInfDir)
        Truth.assertThat(expectedMetaInfDir.listFiles()).asList().containsExactly(expectedAndroidBuddyDir)
        Truth.assertThat(expectedAndroidBuddyDir.listFiles()).asList().containsExactly(getGeneratedPropertiesFile())
        verifyCommonSuccessActions(pluginNames)
    }

    private fun verifyCommonSuccessActions(pluginNames: Set<String>) {
        verify {
            logger.debug("Plugins found: {}", pluginNames)
        }
    }

    private fun getGeneratedPropertiesFile(): File {
        val propertiesDir = File(outputDir, "META-INF/android-buddy-plugins")
        return File(propertiesDir, "plugins.properties")
    }

    private fun createInstance(pluginNames: Set<String>)
            : CreateAndroidBuddyLibraryMetadataAction {
        return CreateAndroidBuddyLibraryMetadataAction(
            DirectoryCleaner(), logger, pluginNames, outputDir
        )
    }
}