subprojects {
    group = "io.github.magiccheese1"
    version = "2.0.2-Snapshot"
    apply(plugin = "java")

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(16))
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://oss.sonatype.org/content/groups/public/")
    }
}
