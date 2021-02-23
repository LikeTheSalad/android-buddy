package com.likethesalad.android.buddylib.modules.createmetadata.data

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.modules.createmetadata.action.CreateAndroidBuddyLibraryMetadataActionFactory
import javax.inject.Inject

@LibraryScope
data class CreateMetadataTaskArgs @Inject constructor(
    val createAndroidBuddyLibraryMetadataActionFactory: CreateAndroidBuddyLibraryMetadataActionFactory,
    val androidBuddyLibraryInfoMaker: AndroidBuddyLibraryInfoMaker
)