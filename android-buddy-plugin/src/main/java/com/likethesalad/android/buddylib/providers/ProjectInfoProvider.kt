package com.likethesalad.android.buddylib.providers

interface ProjectInfoProvider {

    fun getName(): String

    fun getGroup(): String

    fun getVersion(): String
}