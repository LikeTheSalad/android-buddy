package com.likethesalad.android.buddylib.utils

import com.likethesalad.android.buddylib.actions.CreateJarDescriptionPropertiesActionFactory
import com.likethesalad.android.buddylib.actions.VerifyPluginClassesProvidedActionFactory
import com.likethesalad.android.common.utils.ClassGraphProviderFactory
import com.likethesalad.android.common.utils.PluginsFinderFactory

data class CreateJarDescriptionPropertiesArgs(
    val createJarDescriptionPropertiesActionFactory: CreateJarDescriptionPropertiesActionFactory,
    val verifyPluginClassesProvidedActionFactory: VerifyPluginClassesProvidedActionFactory,
    val pluginsFinderFactory: PluginsFinderFactory,
    val classGraphProviderFactory: ClassGraphProviderFactory
)