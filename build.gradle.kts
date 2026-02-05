@file:Suppress("PropertyName")

val exposed_version: String by properties

dependencies {
    api("org.jetbrains.exposed:exposed-kotlin-datetime:$exposed_version")
}
