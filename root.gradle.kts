plugins {
    kotlin("jvm") version "1.9.24" apply false
    id("org.polyfrost.multi-version.root")
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

preprocess {
    val fabric12180 = createNode("1.21.8-fabric", 12108, "yarn")
    val forge10809 = createNode("1.8.9-forge", 10809, "srg")

    fabric12180.link(forge10809)
}
