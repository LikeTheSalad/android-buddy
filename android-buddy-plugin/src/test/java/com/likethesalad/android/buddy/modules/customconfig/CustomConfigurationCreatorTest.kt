package com.likethesalad.android.buddy.modules.customconfig

import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.testutils.BaseMockable
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer

class CustomConfigurationCreatorTest : BaseMockable() {

    @MockK
    lateinit var gradleConfigurationsFinder: GradleConfigurationsFinder

    @MockK
    lateinit var gradleConfigurationsProvider: GradleConfigurationsProvider

    @MockK
    lateinit var configurationContainer: ConfigurationContainer

    private lateinit var onConfigurationFoundCaptor: CapturingSlot<(Configuration) -> Unit>
    private lateinit var customConfigurationCreator: CustomConfigurationCreator

//    @Before
//    fun setUp() {
//        onConfigurationFoundCaptor = slot()
//        every { gradleConfigurationsProvider.getConfigurationContainer() }.returns(configurationContainer)
//        every {
//            gradleConfigurationsFinder.searchForAllowedConfigurations(capture(onConfigurationFoundCaptor))
//        } just Runs
//        customConfigurationCreator = CustomConfigurationCreator(
//            gradleConfigurationsFinder,
//            gradleConfigurationsProvider
//        )
//    }
//
//    @Test
//    fun `Create configurations for all project's allowed configurations found`() {
//        customConfigurationCreator.createAndroidBuddyConfigurations()
//        val original1Name = "original1"
//        val original2Name = "original2"
//        val custom1Name = "androidBuddyOriginal1"
//        val custom2Name = "androidBuddyOriginal2"
//        val onConfigurationFound = onConfigurationFoundCaptor.captured
//        val customConfig1ActionCaptor = slot<Action<Configuration>>()
//        val customConfig2ActionCaptor = slot<Action<Configuration>>()
//        val originalConfig1 = createOriginalConfiguration(original1Name)
//        val originalConfig2 = createOriginalConfiguration(original2Name)
//        val customConfig1 = mockk<Configuration>()
//        val customConfig2 = mockk<Configuration>()
//        every {
//            configurationContainer.create(custom1Name, capture(customConfig1ActionCaptor))
//        }.returns(customConfig1)
//        every {
//            configurationContainer.create(custom2Name, capture(customConfig2ActionCaptor))
//        }.returns(customConfig2)
//
//        onConfigurationFound.invoke(originalConfig1)
//        onConfigurationFound.invoke(originalConfig2)
//
//        customConfig1ActionCaptor.captured.execute(customConfig1)
//        customConfig2ActionCaptor.captured.execute(customConfig2)
//        verify {
//            configurationContainer.create(custom1Name, any<Action<Configuration>>())
//            configurationContainer.create(custom2Name, any<Action<Configuration>>())
//            originalConfig1.extendsFrom(customConfig1)
//            originalConfig2.extendsFrom(customConfig2)
//            customConfig1.isCanBeConsumed = true
//            customConfig1.isCanBeResolved = false
//            customConfig2.isCanBeConsumed = true
//            customConfig2.isCanBeResolved = false
//        }
//    }

    private fun createOriginalConfiguration(name: String): Configuration {
        val configuration = mockk<Configuration>()
        every { configuration.name }.returns(name)
        every { configuration.extendsFrom(any()) }.returns(configuration)
        return configuration
    }
}