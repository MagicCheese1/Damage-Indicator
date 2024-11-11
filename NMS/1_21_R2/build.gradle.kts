plugins {
    id("java")
    id("io.github.patrick.remapper") version "1.4.2"
}

dependencies {
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly("org.spigotmc:spigot:1.21.3-R0.1-SNAPSHOT:remapped-mojang")

    implementation(project(":API"))
}

tasks {
    remap {
        version.set("1.21.3")

        inputTask.set(jar)

        archiveName.set("${project.name}-${project.version}.jar")
    }
}

tasks.named("assemble") {
    dependsOn("remap")
}
