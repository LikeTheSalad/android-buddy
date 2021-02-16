package com.likethesalad.android.buddylib.modules.createmetadata.action

import com.google.common.truth.Truth
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class CreateAndroidBuddyLibraryMetadataActionTest : BaseMockable() {

    @get:Rule
    val temporaryFolderRule = TemporaryFolder()

    @MockK
    lateinit var logger: Logger

    private val byteArrayClassLoaderUtil = ByteArrayClassLoaderUtil(ByteBuddyClassesInstantiator())
    private lateinit var outputDir: File
    private val outputDirName = "android-buddy"

    @Before
    fun setUp() {
        outputDir = temporaryFolderRule.newFolder(outputDirName)
    }

    @Test
    fun `Store multiple plugin class names into a metadata file`() {
        val pluginNames = setOf("some.class.Name", "some.other.class.Name")

        val action = createInstance(pluginNames)
        action.execute()

        verifyPluginsSaved(listOf("some.class.Name,some.other.class.Name"))
        verifyCommonSuccessActions(pluginNames)
    }

    @Test
    fun `Store empty plugin class names into a metadata file`() {
        val pluginNames = setOf<String>()

        val action = createInstance(pluginNames)
        action.execute()

        verifyPluginsSaved(emptyList())
        verifyCommonSuccessActions(pluginNames)
    }

    @Test
    fun `Store one plugin class names into a metadata file`() {
        val pluginNames = setOf("the.one.Class")

        val action = createInstance(pluginNames)
        action.execute()

        verifyPluginsSaved(listOf("the.one.Class"))
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
        Truth.assertThat(expectedAndroidBuddyDir.listFiles()).asList().containsExactly(getGeneratedMetadataFile())
        verifyCommonSuccessActions(pluginNames)
    }

    private fun verifyCommonSuccessActions(pluginNames: Set<String>) {
        verify {
            logger.debug("Transformations found: {}", pluginNames)
        }
    }

    private fun verifyPluginsSaved(expectedPlugins: List<String>) {
        val file = getGeneratedMetadataFile()
        val loaded = byteArrayClassLoaderUtil.loadClass("plugins_definitions", file.readBytes())

        Truth.assertThat(expectedPlugins.joinToString(",")).isEqualTo(loaded.newInstance().toString())
    }

    private fun getGeneratedMetadataFile(): File {
        val dir = File(outputDir, "META-INF/android-buddy-plugins")
        return File(dir, "plugins_definitions.class")
    }

    private fun createInstance(pluginNames: Set<String>)
            : CreateAndroidBuddyLibraryMetadataAction {
        /*return CreateAndroidBuddyLibraryMetadataAction(
            DirectoryCleaner(), logger, pluginNames, outputDir
        )*/
        throw UnsupportedOperationException()
    }
}