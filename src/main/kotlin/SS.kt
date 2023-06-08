import java.io.File

private fun ignoreFile(file: File): Boolean {
    return when {
        file.name == ".git" -> true
        file.name == ".github" -> true
        file.name == ".gradle" -> true
        file.name == ".idea" -> true
        file.name == "build" -> true
        file.name == "buildSrc" -> true
        file.name == "gradle" -> true
        file.extension == "jar" -> true
        file.extension == "jks" -> true
        else -> false
    }
}

private val projectDir = File("D:\\Code\\SSR")

fun main() {
    moveDir(projectDir)
    println("moveDir end")
    renameDir(projectDir)
    println("renameDir end")
    replacePkg(projectDir)
    println("replacePkg end")
    replaceGradle(projectDir)
    println("replaceGradle end")
    replaceImport(projectDir)
    println("replaceImport end")
    replaceCode(projectDir)
    println("replaceCode end")
    replaceXml(projectDir)
    println("replaceXml end")

    checkDir(projectDir)
}

private fun List<String>.same(other: List<String>): Boolean {
    if (size != other.size) return false

    for (i in indices) {
        if (get(i) != other[i]) return false
    }

    return true
}

private fun mapLine(file: File, map: (String) -> String) {
    val lines = file.readLines()
    val newLines = lines.map(map)

    if (lines.same(newLines).not()) {
        file.writeText(newLines.joinToString("\n"))
    }
}

private fun moveDir(file: File) {
    if (ignoreFile(file)) return

    if (file.isDirectory.not()) return

    if (file.name == "hero" && file.parentFile.name == "game") {
        val targetDir = File(file.parentFile.parentFile, "com/someone")
        file.copyRecursively(targetDir, overwrite = true)
        file.parentFile.deleteRecursively()
    } else {
        file.listFiles()?.forEach(::moveDir)
    }
}

private fun renameDir(file: File) {
    if (ignoreFile(file)) return

    if (file.isDirectory.not()) return

    if (file.name == "game.hero.data.database.HeroDb") {
        val targetDir = File(file.parentFile, "com.someone.data.database.HeroDb")
        file.copyRecursively(targetDir)
        file.deleteRecursively()
    } else if (file.name == "game.hero.data.database.Game6BDB") {
        val targetDir = File(file.parentFile, "com.someone.data.database.Game6BDB")
        file.copyRecursively(targetDir)
        file.deleteRecursively()
    } else {
        file.listFiles()?.forEach(::renameDir)
    }
}

private fun replacePkg(file: File) {
    if (ignoreFile(file)) return
    if (file.isDirectory) {
        file.listFiles()?.forEach(::replacePkg)
    } else if (file.isFile) {
        mapLine(file) { line ->
            line.replace("package game.hero", "package com.someone")
                .replace("package  game.hero", "package com.someone")
        }
    }
}

private fun replaceGradle(file: File) {
    if (ignoreFile(file)) return

    if (file.isDirectory) {
        file.listFiles()?.forEach(::replaceGradle)
        return
    } else if (file.isFile.not()) {
        return
    } else if (file.name != "build.gradle.kts") {
        return
    }

    mapLine(file) { line ->
        line.replace("namespace = \"game.hero", "namespace = \"com.someone")
            .replace("src/main/java/game/hero/ui/element", "src/main/java/com/someone/ui/element")
    }

}

private fun replaceImport(file: File) {
    if (ignoreFile(file)) return
    if (file.isDirectory) {
        file.listFiles()?.forEach(::replaceImport)
    } else if (file.isFile) {
        mapLine(file) { line ->
            line.replace("import game.hero", "import com.someone")
        }
    }
}

private fun replaceCode(file: File) {
    if (ignoreFile(file)) return
    if (file.isDirectory) {
        file.listFiles()?.forEach(::replaceCode)
    } else if (file.isFile && file.extension == "kt") {
        mapLine(file) { line ->
            line.replace("game.hero.lib.installer.R", "com.someone.lib.installer.R")
        }
    }
}

private fun replaceXml(file: File) {
    if (ignoreFile(file)) return
    if (file.isDirectory) {
        file.listFiles()?.forEach(::replaceXml)
    } else if (file.isFile && file.extension == "xml") {
        mapLine(file) { line ->
            line.replace("game.hero.ui.element.traditional", "com.someone.ui.element.traditional")
                .replace("game.hero.ui.element.compose", "com.someone.ui.element.compose")
                .replace("android:name=\"game.hero.", "android:name=\"com.someone.")
        }
    }
}


private fun checkDir(file: File) {
    if (ignoreFile(file)) return

    if (file.isDirectory.not()) return

    if (file.name == "game") {
        println(file.absolutePath)
    }

    file.listFiles()?.forEach(::checkDir)
}
