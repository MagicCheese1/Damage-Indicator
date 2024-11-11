plugins {
    id("java")
    id("io.github.patrick.remapper") version "1.4.2"
}
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}
tasks.withType<JavaCompile> { options.release.set(16) }
dependencies {
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly("org.spigotmc:spigot:1.20.2-R0.1-SNAPSHOT:remapped-mojang")

    implementation(project(":API"))
}

tasks {
    remap {
        version.set("1.20.2")

        inputTask.set(jar)

        archiveName.set("${project.name}-${project.version}.jar")
    }
}

tasks.named("assemble") {
    dependsOn("remap")
}
