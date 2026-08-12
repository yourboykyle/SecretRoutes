plugins {
    id("dev.kikugie.loom-back-compat")
    `maven-publish`
}

if (sc.current.parsed < "26") {
    apply(plugin = "net.fabricmc.fabric-loom-remap")
} else {
    apply(plugin = "net.fabricmc.fabric-loom")
}

fun prop(key: String): String = property(key) as String

val mod_name = prop("mod.name")
val mod_id = prop("mod.id")
val mod_version = prop("mod.version")
val mod_group = prop("mod.group")
val mod_archives_name = prop("mod.archives_name")

stonecutter {
    properties.tags(current.version, "fabric")

    if (current.parsed < "26") {
        val excludedClasses = listOf(
            "commands/ChangeColorProfile.java",
            "commands/ChangeRoute.java",
            "commands/Debug.java",
            "commands/LoadRoute.java",
            "commands/Recording.java",
            "commands/SRM.java",
            "config/SRMKeybinds.java",
            "dungeons/rendering/RenderingBackend.java",
            "events/OnPlaySound.java",
            "utils/ChatUtils.java"
        )
        excludedClasses.forEach { filters.exclude("java/xyz/yourboykyle/secretroutes/$it") }
    }
}

val minecraft_version = sc.current.version
val loader_version = prop("loader_version")
val fabric_version = prop("fabric_version")
val yacl_version = prop("yacl_version")
val hypixel_api_version = prop("hypixel_api_version")
val modmenu_version = prop("modmenu_version")
val iris_version = prop("iris_version")
val autoupdate_version = prop("libautoupdate_version")

version = "$mod_version+$minecraft_version"
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

    if (sc.current.parsed < "26") {
        val loom = project.extensions.getByName("loom") as net.fabricmc.loom.api.LoomGradleExtensionAPI
        mappings(loom.layered {
            officialMojangMappings()
        })
    }

    modImplementation("net.fabricmc:fabric-loader:$loader_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    modImplementation("dev.isxander:yet-another-config-lib:$yacl_version")
    modImplementation("com.terraformersmc:modmenu:$modmenu_version")
    modImplementation("maven.modrinth:iris:$iris_version")

    implementation("net.hypixel:mod-api:$hypixel_api_version")

    implementation("moe.nea:libautoupdate:$autoupdate_version")
    include("moe.nea:libautoupdate:$autoupdate_version")
}

val targetJavaVersion = when {
    sc.current.parsed >= "26" -> 25
    sc.current.parsed >= "1.21.11" -> 21
    else -> 17
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val expandProps = mapOf(
        "version" to project.version,
        "mc_version" to minecraft_version,
        "minecraft_version" to minecraft_version,
        "loader_version" to loader_version,
        "mod_id" to mod_id,
        "mod_version" to mod_version,
        "mod_name" to mod_name,
        "mod_description" to "Secret Route Waypoints for Hypixel Skyblock Dungeons",
        "minor_mc_version" to minecraft_version,
        "java_version" to targetJavaVersion
    )

    inputs.properties(expandProps)
    filteringCharset = "UTF-8"

    filesMatching(listOf("fabric.mod.json", "mixins.secretroutesmod.fabric.json")) {
        expand(expandProps)
    }
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