plugins {
    kotlin("jvm") version "1.8.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    api("net.mamoe", "mirai-core", "2.16.0")
    implementation("net.mamoe:mirai-core-utils:2.16.0")
    implementation("com.alibaba:fastjson:1.2.83")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("org.jsoup:jsoup:1.17.2")
}