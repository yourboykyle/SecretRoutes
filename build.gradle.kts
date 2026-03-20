@file:Suppress("UnstableApiUsage", "PropertyName")

import dev.deftu.gradle.utils.GameSide
import groovy.lang.MissingPropertyException

plugins {
    java
    kotlin("jvm")
    id("dev.deftu.gradle.multiversion")
    id("dev.deftu.gradle.tools")
    id("dev.deftu.gradle.tools.resources")
    id("dev.deftu.gradle.tools.bloom")
    id("dev.deftu.gradle.tools.shadow")
    id("dev.deftu.gradle.tools.publishing.maven")
    id("dev.deftu.gradle.tools.minecraft.loom")
    id("dev.deftu.gradle.tools.minecraft.api")
    id("dev.deftu.gradle.tools.minecraft.releases-v2")
}

loom.enableModProvidedJavadoc.set(false)

if (mcData.isForge) {
    toolkitLoomHelper.useForgeMixin(modData.id)
}

val mod_archives_name: String = extra["mod.archives_name"]?.toString()
    ?: throw MissingPropertyException("mod.archives_name has not been set.")

base {
    archivesName.set(mod_archives_name)
}

toolkitLoomHelper {
    if (mcData.isForge) {
        useOneConfig {
            version = "1.0.0-alpha.171"
            loaderVersion = "1.1.0-alpha.53"

            usePolyMixin = true
            polyMixinVersion = "0.8.4+build.7"

            applyLoaderTweaker = true

            for (module in arrayOf("commands", "config", "config-impl", "events", "internal", "ui", "utils")) {
                +module
            }
        }
    }
    useDevAuth("1.2.+")
    useMixinExtras("0.5.0")

    useProperty("mixin.debug.export", "true", GameSide.BOTH)

    // Turns off the server-side run configs, as we're building a client-sided mod.
    disableRunConfigs(GameSide.SERVER)

    // Defines the name of the Mixin refmap, which is used to map the Mixin classes to the obfuscated Minecraft classes.
    if (!mcData.isNeoForge) {
        useMixinRefMap(modData.id)
    }

    if (mcData.isForge) {
        // Configures the Mixin tweaker if we are building for Forge.
        useForgeMixin(modData.id)
    }
}

repositories {
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
    maven("https://maven.bawnorton.com/releases") {
        content { includeGroup("com.github.bawnorton.mixinsquared") }
    }
    maven("https://repo.nea.moe/releases")

    // YACL
    maven("https://maven.isxander.dev/releases")

    // Hypixel mod api
    maven("https://repo.hypixel.net/repository/Hypixel/")

    // ModMenu
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    // Fabric API - only for Fabric 1.21.10 build
    // Other Fabric versions don't load Fabric API to avoid compatibility issues
    if (mcData.isFabric && project.name == "1.21.10-fabric") {
        modImplementation("net.fabricmc.fabric-api:fabric-api:0.138.4+1.21.10")

        modImplementation("dev.isxander:yet-another-config-lib:3.8.1+1.21.10-fabric")

        modImplementation("net.hypixel:mod-api:1.0.1")

        modImplementation("com.terraformersmc:modmenu:16.0.0-rc.2")
    } else if (mcData.isFabric && project.name == "1.21.11-fabric") {
        modImplementation("net.fabricmc.fabric-api:fabric-api:0.141.3+1.21.11")

        modImplementation("dev.isxander:yet-another-config-lib:3.8.1+1.21.11-fabric")

        modImplementation("net.hypixel:mod-api:1.0.1")

        modImplementation("com.terraformersmc:modmenu:17.0.0-beta.2")

        modImplementation("maven.modrinth:iris:1.9.1+1.21.7-fabric")
    }

    modCompileOnly("moe.nea:libautoupdate:1.3.1") {
        isTransitive = false
    }
    shade("moe.nea:libautoupdate:1.3.1") {
        isTransitive = false
    }
}

tasks {
    processResources {
        if (mcData.isForge) {
            exclude("mixins.${modData.id}.fabric.json")
            exclude("rooms.json")
            exclude("assets/secretroutesmod/secretlocations.json")
            exclude("LICENSE-ODIN.txt")
        } else if (mcData.isFabric) {
            exclude("mixins.${modData.id}.json")
            exclude("assets/roomdetection/**")
        }
    }
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    remapJar {
        val configs = mutableListOf<String>()

        if (mcData.isForge) {
            configs.add("mixins.${modData.id}.forge.json")
        }

        if (configs.isNotEmpty()) {
            manifest {
                attributes(mapOf(
                    "MixinConfigs" to configs.joinToString(",")
                ))
            }
        }
    }
}
