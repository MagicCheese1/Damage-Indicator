plugins {
    id("java")
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly(project(":API"))
}
