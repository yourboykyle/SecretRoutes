plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    // Minecraft 1.21.8 exists and has Yarn mappings (1.21.8+build.1)
    "1.21.8-fabric"(1_21_08, "yarn") {
        "1.8.9-forge"(1_08_09, "srg")
    }
}
