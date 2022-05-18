package com.likethesalad.android.buddylib.modules.createmetadata.data

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.modules.createmetadata.action.CreateAndroidBuddyLibraryMetadataAction
import javax.inject.Inject

@LibraryScope
data class CreateMetadataTaskArgs @Inject constructor(
    val createAndroidBuddyLibraryMetadataActionFactory: CreateAndroidBuddyLibraryMetadataAction.Factory,
    val androidBuddyLibraryInfoMaker: AndroidBuddyLibraryInfoMaker,
    val libraryGnvProviders: LibraryGnvProviders
)