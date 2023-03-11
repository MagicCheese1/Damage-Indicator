
group = "io.github.magiccheese1"
version = "1.3.6-SNAPSHOT"

subprojects {
    apply(plugin = "java")

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
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
