@file:Suppress("UnstableApiUsage", "PropertyName")

import dev.deftu.gradle.utils.GameSide
import groovy.lang.MissingPropertyException
import org.gradle.kotlin.dsl.include

plugins {
    java
    kotlin("jvm")
    id("dev.deftu.gradle.multiversion") // Applies preprocessing for multiple versions of Minecraft and/or multiple mod loaders.
    id("dev.deftu.gradle.tools") // Applies several configurations to things such as the Java version, project name/version, etc.
    id("dev.deftu.gradle.tools.resources") // Applies resource processing so that we can replace tokens, such as our mod name/version, in our resources.
    id("dev.deftu.gradle.tools.bloom") // Applies the Bloom plugin, which allows us to replace tokens in our source files, such as being able to use `@MOD_VERSION` in our source files.
    id("dev.deftu.gradle.tools.minecraft.loom") // Applies the Loom plugin, which automagically configures Essential's Architectury Loom plugin for you.
    id("dev.deftu.gradle.tools.shadow") // Applies the Shadow plugin, which allows us to shade our dependencies into our mod JAR. This is NOT recommended for Fabric mods, but we have an *additional* configuration for those!
    id("dev.deftu.gradle.tools.minecraft.releases") // Applies the Minecraft auto-releasing plugin, which allows you to automatically release your mod to CurseForge and Modrinth.
}

if (mcData.isForge) {
    loom.forge.mixinConfig("mixins.${modData.id}.json")
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
            exclude("assets/skyblocker-catacombs/**")
        } else if (mcData.isFabric) {
            exclude("mixins.${modData.id}.json")
            exclude("assets/roomdetection/**")
            exclude("assets/secretroutesmod/**")
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
