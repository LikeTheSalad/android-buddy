package com.likethesalad.android.buddy.modules.customconfig.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationGroup
import com.likethesalad.android.buddy.modules.customconfig.data.ConfigurationType
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class CustomConfigurationNamesGeneratorTest : BaseMockable() {

    @MockK
    lateinit var configurationGroup: ConfigurationGroup

    @MockK
    lateinit var bucketType: ConfigurationType

    @MockK
    lateinit var resolvableType: ConfigurationType

    private val bucketCapitalizedName = "SomeBucketName"
    private val resolvableCapitalizedName = "SomeResolvableName"
    private lateinit var customConfigurationNamesGenerator: CustomConfigurationNamesGenerator

    @Before
    fun setUp() {
        every { bucketType.capitalizedName }.returns(bucketCapitalizedName)
        every { resolvableType.capitalizedName }.returns(resolvableCapitalizedName)
        every { configurationGroup.bucketType }.returns(bucketType)
        every { configurationGroup.resolvableType }.returns(resolvableType)

        customConfigurationNamesGenerator = CustomConfigurationNamesGenerator(configurationGroup)
    }

    @Test
    fun `Get resolvable name`() {
        val variantName = "someVariantName"

        val result = customConfigurationNamesGenerator.getResolvableConfigurationName(variantName)

        Truth.assertThat(result).isEqualTo("${variantName}AndroidBuddy${resolvableCapitalizedName}")
    }

    @Test
    fun `Get variant path bucket names`() {
        val variantPath = listOf("one", "two", "three")

        val result = customConfigurationNamesGenerator.getSortedBucketConfigNames(variantPath)

        Truth.assertThat(result).containsExactly(
            "androidBuddy${bucketCapitalizedName}",
            "oneAndroidBuddy${bucketCapitalizedName}",
            "twoAndroidBuddy${bucketCapitalizedName}",
            "threeAndroidBuddy${bucketCapitalizedName}"
        )
    }
}