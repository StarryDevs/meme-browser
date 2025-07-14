import com.github.gradle.node.yarn.task.YarnTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.node)

    application
}

group = "starry.memebrowser"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    api(libs.bundles.kotlinx.ecosystem)

    api(libs.fuzzy.search)
    api(libs.auxframework.application)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "starry.memebrowser.MemeBrowserKt"
}

node {
    download = true
    version = "22.4.1"
    yarnVersion = "1.22.19"
}

val yarnBuild = tasks.register<YarnTask>("yarnBuild") {
    dependsOn(tasks.yarn)
    workingDir.set(file("src-vue"))
    args.set(listOf("build-only")) // 执行构建
}

val copyVueDist = tasks.register<Copy>("copyVueDist") {
    dependsOn(yarnBuild) // 依赖于 yarnBuild 任务
    from("src-vue/dist")
    into(layout.buildDirectory.dir("resources/main/public"))
}

tasks.processResources {
    dependsOn(copyVueDist)
}

tasks.clean {
    delete.add("src-vue/dist")
}
