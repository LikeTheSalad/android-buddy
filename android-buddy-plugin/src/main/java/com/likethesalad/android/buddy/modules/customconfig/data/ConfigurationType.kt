package com.likethesalad.android.buddy.modules.customconfig.data

enum class ConfigurationType(
    val configurationName: String,
    val capitalizedName: String = configurationName.capitalize()
) {
    IMPLEMENTATION("implementation"),
    API("api"),
    RUNTIME_ELEMENTS("runtimeElements"),
    RUNTIME_CLASSPATH("runtimeClasspath"),
    API_ELEMENTS("apiElements"),
    API_CLASSPATH("apiClasspath")
}