package com.likethesalad.android.buddy.modules.customconfig

import com.likethesalad.android.buddy.modules.customconfig.data.BucketConfiguration
import com.likethesalad.android.buddy.modules.customconfig.utils.BucketConfigurationsFinder
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.junit.Before
import org.junit.Test

class CustomBucketConfigurationCreatorTest : BaseMockable() {

    @MockK
    lateinit var bucketConfigurationsFinder: BucketConfigurationsFinder

    @MockK
    lateinit var gradleConfigurationsProvider: GradleConfigurationsProvider

    @MockK
    lateinit var configurationContainer: ConfigurationContainer

    private lateinit var onConfigurationFoundCaptor: CapturingSlot<(BucketConfiguration) -> Unit>
    private lateinit var customBucketConfigurationCreator: CustomBucketConfigurationCreator

    @Before
    fun setUp() {
        onConfigurationFoundCaptor = slot()
        every { gradleConfigurationsProvider.getConfigurationContainer() }.returns(configurationContainer)
        every {
            bucketConfigurationsFinder.searchForAllowedConfigurations(capture(onConfigurationFoundCaptor))
        } just Runs
        customBucketConfigurationCreator = CustomBucketConfigurationCreator(
            bucketConfigurationsFinder,
            gradleConfigurationsProvider
        )
    }

    @Test
    fun `Create configurations for all project's allowed configurations found`() {
        customBucketConfigurationCreator.createAndroidBuddyConfigurations()
        val original1Name = "implementation"
        val original2Name = "demoOriginal2Api"
        val custom1Name = "androidBuddyImplementation"
        val custom2Name = "demoOriginal2AndroidBuddyApi"
        val onConfigurationFound = onConfigurationFoundCaptor.captured
        val originalBucket1 = createOriginalBucket(original1Name, "", "implementation")
        val originalBucket2 = createOriginalBucket(original2Name, "demoOriginal2", "Api")
        val customConfig1 = mockk<Configuration>()
        val customConfig2 = mockk<Configuration>()
        every {
            configurationContainer.maybeCreate(custom1Name)
        }.returns(customConfig1)
        every {
            configurationContainer.maybeCreate(custom2Name)
        }.returns(customConfig2)

        onConfigurationFound.invoke(originalBucket1)
        onConfigurationFound.invoke(originalBucket2)

        verify {
            configurationContainer.maybeCreate(custom1Name)
            configurationContainer.maybeCreate(custom2Name)
            originalBucket1.configuration.extendsFrom(customConfig1)
            originalBucket2.configuration.extendsFrom(customConfig2)
            customConfig1.isCanBeConsumed = false
            customConfig1.isCanBeResolved = false
            customConfig2.isCanBeConsumed = false
            customConfig2.isCanBeResolved = false
        }
    }

    private fun createOriginalBucket(name: String, prefix: String, suffix: String)
            : BucketConfiguration {
        val configuration = mockk<Configuration>()
        every { configuration.name }.returns(name)
        every { configuration.extendsFrom(any()) }.returns(configuration)
        return BucketConfiguration(configuration, prefix, suffix)
    }
}