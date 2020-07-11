package com.likethesalad.android.buddylib.models

import com.likethesalad.android.buddylib.actions.CreateJarDescriptionPropertiesActionFactory
import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.common.actions.VerifyPluginClassesProvidedActionFactory
import com.likethesalad.android.common.utils.ClassGraphProviderFactory
import com.likethesalad.android.common.utils.PluginsFinderFactory
import javax.inject.Inject

@LibraryScope
data class CreateJarDescriptionPropertiesArgs @Inject constructor(
    val createJarDescriptionPropertiesActionFactory: CreateJarDescriptionPropertiesActionFactory,
    val verifyPluginClassesProvidedActionFactory: VerifyPluginClassesProvidedActionFactory,
    val pluginsFinderFactory: PluginsFinderFactory,
    val classGraphProviderFactory: ClassGraphProviderFactory
)