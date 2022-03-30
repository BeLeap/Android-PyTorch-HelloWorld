buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(GradlePlugins.Android)
        classpath(GradlePlugins.Kotlin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}