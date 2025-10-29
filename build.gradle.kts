plugins {
    kotlin("jvm") version "1.9.24"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "redfox.skyblock"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("org.mongodb:mongodb-driver-sync:4.11.0")
    implementation("org.json:json:20220924")
    implementation("redis.clients:jedis:4.4.3")
    compileOnly("com.github.PowerNukkitX:PowerNukkitX:master-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

application {
    mainClass.set("redfox.skyblock.Core")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveFileName.set("SkyblockCore.jar")
    destinationDirectory.set(layout.projectDirectory.dir(".."))
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Main-Class" to "redfox.skyblock.Core"))
    }
}

tasks.build {
    dependsOn("shadowJar")
}