package com.likethesalad.android.buddylib.modules.createmetadata.utils

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

class CreateMetadataTaskNameGeneratorTest {

    private lateinit var createMetadataTaskNameGenerator: CreateMetadataTaskNameGenerator

    @Before
    fun setUp() {
        createMetadataTaskNameGenerator = CreateMetadataTaskNameGenerator()
    }

    @Test
    fun `Generate task name from variant name`() {
        val variantName = "someName"

        Truth.assertThat(createMetadataTaskNameGenerator.generateTaskName(variantName))
            .isEqualTo("createSomeNameAndroidBuddyLibraryMetadata")
    }
}