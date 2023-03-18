plugins {
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
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
