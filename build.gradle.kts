subprojects {
    group = "io.github.magiccheese1"
    version = "2.1.2"
    apply(plugin = "java")

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
    tasks.withType<JavaCompile> { options.release.set(16) }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://oss.sonatype.org/content/groups/public/")
    }
}
