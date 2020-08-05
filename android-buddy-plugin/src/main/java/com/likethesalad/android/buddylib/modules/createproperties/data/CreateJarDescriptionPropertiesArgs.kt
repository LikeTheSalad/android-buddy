package com.likethesalad.android.buddylib.modules.createproperties.data

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.modules.createproperties.CreateAndroidBuddyLibraryPropertiesActionFactory
import com.likethesalad.android.common.utils.ClassGraphProviderFactory
import com.likethesalad.android.common.utils.PluginsFinderFactory
import javax.inject.Inject

@LibraryScope
data class CreateJarDescriptionPropertiesArgs @Inject constructor(
    val createAndroidBuddyLibraryPropertiesActionFactory: CreateAndroidBuddyLibraryPropertiesActionFactory,
    val pluginsFinderFactory: PluginsFinderFactory,
    val classGraphProviderFactory: ClassGraphProviderFactory
)