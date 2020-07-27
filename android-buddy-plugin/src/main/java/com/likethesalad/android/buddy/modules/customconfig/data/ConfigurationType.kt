package com.likethesalad.android.buddy.modules.customconfig.data

enum class ConfigurationType(
    val configurationName: String,
    val capitalizedName: String = configurationName.capitalize()
) {
    IMPLEMENTATION("implementation"),
    API("api"),
    RUNTIME_CLASSPATH("runtimeClasspath"),
    COMPILE_CLASSPATH("compileClasspath")
}