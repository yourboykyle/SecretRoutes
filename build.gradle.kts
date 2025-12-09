plugins {
    id("fabric-loom") version "1.10.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "2.0.21"
}

val mod_version: String by project
val mod_id: String by project
val mod_name: String by project
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project

version = mod_version
group = "xyz.yourboykyle"

base {
    archivesName.set("SecretRoutes")
}

repositories {
    mavenCentral()
    maven("https://repo.nea.moe/releases")
    maven("https://maven.teamresourceful.com/repository/maven-public/") // ResourcefulConfig
}

loom {
    runConfigs.configureEach {
        ideConfigGenerated(true)
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.2+kotlin.2.1.20")

    // ResourcefulConfig - GUI Config Library (1.21.7 artifact supports 1.21.8)
    modImplementation("com.teamresourceful.resourcefulconfig:resourcefulconfig-fabric-1.21.7:3.7.6")
    include("com.teamresourceful.resourcefulconfig:resourcefulconfig-fabric-1.21.7:3.7.6")

    // libautoupdate
    implementation("moe.nea:libautoupdate:1.3.1") {
        isTransitive = false
    }
    include("moe.nea:libautoupdate:1.3.1")
}

tasks.processResources {
    inputs.property("version", mod_version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("loader_version", loader_version)

    filesMatching("fabric.mod.json") {
        expand(
            "version" to mod_version,
            "minecraft_version" to minecraft_version,
            "loader_version" to loader_version,
            "id" to mod_id,
            "name" to mod_name
        )
    }
}

tasks.withType<JavaCompile> {
    options.release.set(21)
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}
