plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    `maven-publish`
}

val mod_name: String = property("mod.name").toString()
val mod_id: String = property("mod.id").toString()
val mod_version: String = property("mod.version").toString()
val mod_group: String = property("mod.group").toString()
val mod_archives_name: String = property("mod.archives_name").toString()

val minecraft_version: String by project
val loader_version: String by project
val fabric_version: String by project
val yacl_version: String by project
val hypixel_api_version: String by project
val modmenu_version: String by project
val iris_version: String by project

version = mod_version
group = mod_group

base {
    archivesName.set(mod_archives_name)
}
repositories {
    mavenCentral()
    maven("https://api.modrinth.com/maven")
    maven("https://repo.hypixel.net/repository/Hypixel/")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.isxander.dev/releases")
    maven("https://repo.nea.moe/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    implementation("net.fabricmc:fabric-loader:$loader_version")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    implementation("dev.isxander:yet-another-config-lib:$yacl_version")
    implementation("com.terraformersmc:modmenu:$modmenu_version")
    implementation("maven.modrinth:iris:$iris_version")

    implementation("net.hypixel:mod-api:$hypixel_api_version")

    implementation("moe.nea:libautoupdate:1.3.1")
    include("moe.nea:libautoupdate:1.3.1")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("loader_version", loader_version)
    inputs.property("mod_id", mod_id)
    inputs.property("mod_version", mod_version)
    inputs.property("mod_name", mod_name)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "mc_version" to minecraft_version,
                "minecraft_version" to minecraft_version,
                "loader_version" to loader_version,
                "mod_id" to mod_id,
                "mod_version" to mod_version,
                "mod_name" to mod_name,
                "mod_description" to "Secret Route Waypoints for Hypixel Skyblock Dungeons",
                "minor_mc_version" to minecraft_version
            )
        )
    }
}

val targetJavaVersion = 25
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_$mod_archives_name" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = mod_archives_name
            from(components["java"])
        }
    }
    repositories {}
}