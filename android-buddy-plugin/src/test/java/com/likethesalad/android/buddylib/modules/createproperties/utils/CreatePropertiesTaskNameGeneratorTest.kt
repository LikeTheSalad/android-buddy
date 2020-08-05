package com.likethesalad.android.buddylib.modules.createproperties.utils

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

class CreatePropertiesTaskNameGeneratorTest {

    private lateinit var createPropertiesTaskNameGenerator: CreatePropertiesTaskNameGenerator

    @Before
    fun setUp() {
        createPropertiesTaskNameGenerator = CreatePropertiesTaskNameGenerator()
    }

    @Test
    fun `Generate task name from variant name`() {
        val variantName = "someName"

        Truth.assertThat(createPropertiesTaskNameGenerator.generateTaskName(variantName))
            .isEqualTo("createSomeNameAndroidBuddyLibraryProperties")
    }
}