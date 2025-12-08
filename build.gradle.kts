@file:Suppress("UnstableApiUsage", "PropertyName")

import org.polyfrost.gradle.util.noServerRunConfigs
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// Adds support for kotlin, and adds the Polyfrost Gradle Toolkit
// which we use to prepare the environment.
plugins {
    kotlin("jvm")
    id("org.polyfrost.multi-version")
    id("org.polyfrost.defaults.repo")
    id("org.polyfrost.defaults.java")
    id("org.polyfrost.defaults.loom")
    id("com.github.johnrengelman.shadow")
    id("net.kyori.blossom") version "1.3.2"
    id("signing")
    id("io.freefair.lombok") version "6.5.1"
    java
}


// Gets the mod name, version and id from the `gradle.properties` file.
val mod_name: String by project
val mod_version: String by project
val mod_id: String by project
val mod_archives_name: String by project

// Replaces the variables in `ExampleMod.java` to the ones specified in `gradle.properties`.
blossom {
    replaceToken("@VER@", mod_version)
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
}

// Sets the mod version to the one specified in `gradle.properties`. Make sure to change this following semver!
version = mod_version
// Sets the group, make sure to change this to your own. It can be a website you own backwards or your GitHub username.
// e.g. com.github.<your username> or com.<your domain>
group = "xyz.yourboykyle"

// Sets the name of the output jar (the one you put in your mods folder and send to other people)
// It outputs all versions of the mod into the `versions/{mcVersion}/build` directory.
base {
    archivesName.set(mod_archives_name)
}

// Configures Polyfrost Loom, our plugin fork to easily set up the programming environment.
loom {
    noServerRunConfigs()

    if (project.platform.isLegacyForge) {
        runConfigs {
            "client" {
                programArgs("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
                property("mixin.debug.export", "true")
            }
        }
    }

    // Add Fabric-specific configuration
    if (project.platform.isFabric) {
        // Fabric uses client runs by default, no special config needed
    }

    if (project.platform.isForge) {
        forge {
            mixinConfig("mixins.${mod_id}.json")
        }
    }

    mixin.defaultRefmapName.set("mixins.${mod_id}.refmap.json")
}

// Creates the shade/shadow configuration, so we can include libraries inside our mod, rather than having to add them separately.
val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}
val modShade: Configuration by configurations.creating {
    configurations.modImplementation.get().extendsFrom(this)
}

// Configures the output directory for when building from the `src/resources` directory.
sourceSets {
    main {
        output.setResourcesDir(java.classesDirectory)
    }
}

// Adds the Polyfrost maven repository so that we can get the libraries necessary to develop the mod.
repositories {
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.nea.moe/releases")
}

// Configures the libraries/dependencies for your mod.
dependencies {
    // Adds the OneConfig library, so we can develop with it.
    //modCompileOnly("cc.polyfrost:oneconfig-$platform:1.0.0-alpha+")
    implementation("cc.polyfrost:oneconfig-$platform:1.0.0-alpha+")

    // Adds DevAuth, which we can use to log in to Minecraft in development.
    modRuntimeOnly("me.djtheredstoner:DevAuth-${if (platform.isFabric) "fabric" else if (platform.isLegacyForge) "forge-legacy" else "forge-latest"}:1.2.0")

    // Add Fabric-specific dependencies
    if (platform.isFabric) {
        modImplementation("net.fabricmc:fabric-loader:0.18.1")
        modImplementation("net.fabricmc.fabric-api:fabric-api:0.136.1+1.21.8")
        //modImplementation("cc.polyfrost:1.21.8-fabric:1.0.0-alpha+")
    }

    shade("moe.nea:libautoupdate:1.3.1"){
        isTransitive = false
    }

    // If we are building for legacy forge...
    if (platform.isLegacyForge) {
        compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")
        shade("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta17")
    }
}


tasks.shadowJar{
    relocate("moe.nea.libautoupdate", "xyz.yourboykyle.secretroutes.deps.libautoupdate")
}

tasks {
    // Processes the `src/resources/mcmod.info`, `fabric.mod.json`, or `mixins.${mod_id}.json` and replaces
    // the mod id, name and version with the ones in `gradle.properties`
    processResources {
        inputs.property("id", mod_id)
        inputs.property("name", mod_name)
        val java = if (project.platform.mcMinor >= 18) {
            17 // If we are playing on version 1.18, set the java version to 17
        } else {
            // Else if we are playing on version 1.17, use java 16.
            if (project.platform.mcMinor == 17)
                16
            else
                8 // For all previous versions, we **need** java 8 (for Forge support).
        }
        val compatLevel = "JAVA_${java}"
        inputs.property("java", java)
        inputs.property("java_level", compatLevel)
        inputs.property("version", mod_version)
        inputs.property("mcVersionStr", project.platform.mcVersionStr)
        filesMatching(listOf("mcmod.info", "mixins.${mod_id}.json", "mods.toml")) {
            expand(
                mapOf(
                    "id" to mod_id,
                    "name" to mod_name,
                    "java" to java,
                    "java_level" to compatLevel,
                    "version" to mod_version,
                    "mcVersionStr" to project.platform.mcVersionStr
                )
            )
        }
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "id" to mod_id,
                    "name" to mod_name,
                    "java" to java,
                    "java_level" to compatLevel,
                    "version" to mod_version,
                    "mcVersionStr" to project.platform.mcVersionStr.substringBeforeLast(".") + ".x"
                )
            )
        }
    }

    // Configures the resources to include if we are building for forge or fabric.
    withType(Jar::class.java) {
        if (project.platform.isFabric) {
            exclude("mcmod.info", "mods.toml")
        } else {
            exclude("fabric.mod.json")
            if (project.platform.isLegacyForge) {
                exclude("mods.toml")
            } else {
                exclude("mcmod.info")
            }
        }
    }

    // Configures our shadow/shade configuration, so we can
    // include some dependencies within our mod jar file.
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("dev")
        configurations = listOf(shade, modShade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
        if (project.hasProperty("toModsFolder")) {
            destinationDirectory.set(file("${System.getenv("APPDATA")}\\.minecraft\\mods"))
        }
    }

    jar {
        // Sets the jar manifest attributes.
        if (platform.isLegacyForge) {
            manifest.attributes += mapOf(
                "ModSide" to "CLIENT", // We aren't developing a server-side mod
                "ForceLoadAsMod" to true, // We want to load this jar as a mod, so we force Forge to do so.
                "TweakOrder" to "0", // Makes sure that the OneConfig launch wrapper is loaded as soon as possible.
                "MixinConfigs" to "mixins.${mod_id}.json", // We want to use our mixin configuration, so we specify it here.
                "TweakClass" to "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker" // Loads the OneConfig launch wrapper.
            )
        }
        dependsOn(shadowJar)
        archiveClassifier.set("")
        enabled = false
    }
}

if (project.hasProperty("toModsFolder")) {
    tasks.register("finalize") {
        doLast {
            project.exec {
                commandLine("cmd", "/c", "start", "finish.bat")
            }
        }
    }
    tasks.assemble.get().dependsOn(tasks.getByName("finalize"))
}