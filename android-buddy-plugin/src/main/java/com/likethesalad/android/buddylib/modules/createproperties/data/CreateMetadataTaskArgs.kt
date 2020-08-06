package com.likethesalad.android.buddylib.modules.createproperties.data

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.modules.createproperties.action.CreateAndroidBuddyLibraryMetadataActionFactory
import javax.inject.Inject

@LibraryScope
data class CreateMetadataTaskArgs @Inject constructor(
    val createAndroidBuddyLibraryMetadataActionFactory: CreateAndroidBuddyLibraryMetadataActionFactory
)