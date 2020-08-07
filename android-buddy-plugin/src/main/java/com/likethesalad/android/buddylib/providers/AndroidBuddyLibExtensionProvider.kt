package com.likethesalad.android.buddylib.providers

import com.likethesalad.android.buddylib.extension.AndroidBuddyLibExtension

interface AndroidBuddyLibExtensionProvider {

    fun getExtension(): AndroidBuddyLibExtension
}