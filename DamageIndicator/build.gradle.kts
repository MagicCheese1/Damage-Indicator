plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3" // Generates plugin.yml
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    implementation("com.tchristofferson:ConfigUpdater:2.0-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")
    implementation(project(":API"))
    implementation(project(":1_16_R3"))
    implementation(project(":1_17_R1"))
    implementation(project(":1_18_R1"))
    implementation(project(":1_19_R1"))
    implementation(project(":1_19_R2"))
    implementation(project(":1_19_R3"))
    implementation(project(":1_20_R1"))
    implementation(project(":1_20_R2"))
    implementation(project(":1_20_R3"))


    testImplementation("junit:junit:4.13.1")
    testImplementation("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")
}
tasks {
    test {
        useJUnit()
    }
    shadowJar {
        relocate("com.tchristofferson.ConfigUpdater", "io.github.magiccheese1.damageindicator.ConfigUpdater")
    }
    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    main = "io.github.magiccheese1.damageindicator.DamageIndicatorImpl"
    apiVersion = "1.16"
    commands {
        register("damageindicator") {
            description = "Reload"
            permission = "Damageindicator.admin"
        }
    }
}
