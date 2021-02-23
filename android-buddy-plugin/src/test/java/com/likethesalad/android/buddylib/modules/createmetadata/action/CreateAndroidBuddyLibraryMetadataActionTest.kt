package com.likethesalad.android.buddylib.modules.createmetadata.action

import com.google.common.truth.Truth
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.models.libinfo.LibraryInfoMapper
import com.likethesalad.android.common.models.libinfo.NamedClassInfo
import com.likethesalad.android.common.models.libinfo.utils.AndroidBuddyLibraryInfoFqnBuilder
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
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
    private val androidBuddyLibraryInfoFqnBuilder = AndroidBuddyLibraryInfoFqnBuilder()
    private val infoMapper = LibraryInfoMapper(
        androidBuddyLibraryInfoFqnBuilder,
        byteArrayClassLoaderUtil
    )
    private lateinit var outputDir: File
    private val outputDirName = "android-buddy"
    private val id = "some-id"
    private val group = "some.group"
    private val name = "some-name"
    private val version = "1.0.1"
    private val expectedClassName = "some.group.some_name.some_id.library_definition"

    @Before
    fun setUp() {
        outputDir = temporaryFolderRule.newFolder(outputDirName)
    }

    @Test
    fun `Store multiple plugin class names into a metadata file`() {
        val pluginNames = setOf("some.class.Name", "some.other.class.Name")

        val action = createStandardInstance(pluginNames)
        action.execute()

        verifyStandardMetadataSaved(setOf("some.class.Name", "some.other.class.Name"))
        verifyCommonSuccessActions(pluginNames)
    }

    @Test
    fun `Store empty plugin class names into a metadata file`() {
        val pluginNames = setOf<String>()

        val action = createStandardInstance(pluginNames)
        action.execute()

        verifyStandardMetadataSaved(emptySet())
        verifyCommonSuccessActions(pluginNames)
    }

    @Test
    fun `Store one plugin class names into a metadata file`() {
        val pluginNames = setOf("the.one.Class")

        val action = createStandardInstance(pluginNames)
        action.execute()

        verifyStandardMetadataSaved(setOf("the.one.Class"))
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
        val action = createStandardInstance(pluginNames)
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

    private fun verifyStandardMetadataSaved(expectedPlugins: Set<String>) {
        val info = AndroidBuddyLibraryInfo(id, group, name, version, expectedPlugins.toSet())
        verifyMetadataSaved(info)
    }

    private fun verifyMetadataSaved(expectedInfo: AndroidBuddyLibraryInfo) {
        val file = getGeneratedMetadataFile()
        val loaded = infoMapper.convertToAndroidBuddyLibraryInfo(
            NamedClassInfo(
                expectedClassName,
                file.readBytes()
            )
        )

        Truth.assertThat(loaded).isEqualTo(expectedInfo)
    }

    private fun getGeneratedMetadataFile(): File {
        val dir = File(outputDir, "META-INF/android-buddy-plugins")
        return File(dir, "$expectedClassName.class")
    }

    private fun createStandardInstance(pluginNames: Set<String>): CreateAndroidBuddyLibraryMetadataAction {
        val info = AndroidBuddyLibraryInfo(id, group, name, version, pluginNames)
        return createInstance(info)
    }

    private fun createInstance(info: AndroidBuddyLibraryInfo)
            : CreateAndroidBuddyLibraryMetadataAction {
        return CreateAndroidBuddyLibraryMetadataAction(
            DirectoryCleaner(), logger, infoMapper, info, outputDir
        )
    }
}