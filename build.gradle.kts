plugins {
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.kohsuke:github-api:1.115")
}

application {
    mainClassName = "com.github.pierre_ernst.snyk.GhManifestList"
}
