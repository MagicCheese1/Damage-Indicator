plugins {
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}

publishing {
    publications.create<MavenPublication>("damageindicator-api") {
        artifactId = "damageindicator-api"
        from(components["java"])
    }
}
