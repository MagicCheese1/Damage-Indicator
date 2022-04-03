plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1" // Generates plugin.yml
}

group = "io.github.magiccheese1"
version = "1.3.2"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}


repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    implementation("com.tchristofferson:ConfigUpdater:1.2-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")

    testImplementation("junit:junit:4.13")
    testImplementation("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")
}
tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(16)
    }
    processResources {

    }
    test {
        useJUnit()
    }
    shadowJar {
        relocate("com.tchristofferson.ConfigUpdater", "io.github.magiccheese1.damageindicator.ConfigUpdater")
    }
    jar {
        dependsOn(shadowJar)
    }
}

bukkit {
    main = "com.github.magiccheese1.damageindicator.Main"
    apiVersion = "1.16"
    commands {
        register("damageindicator") {
            description = "Reload"
            permission = "Damageindicator.admin"
        }
    }
}
