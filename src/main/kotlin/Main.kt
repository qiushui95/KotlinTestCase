import kotlinx.coroutines.runBlocking
import java.io.File

val dir =
    File("D:\\Code\\Android\\Work\\AiImage\\ui\\impl\\src\\main\\java\\nbe\\someone\\code\\ui\\impl\\common\\theme\\color1")

fun main(args: Array<String>) = runBlocking {
    dealFile(dir)
}

private val skipFileName = listOf("CommonColors.kt", "VipRechargeColors.kt", "PageColors.kt", "PageColorsProvider.kt")
private fun dealFile(file: File) {
    if (file.isDirectory) {
        file.listFiles()?.forEach { dealFile(it) }
    } else if (file.isFile) {
        if (file.name in skipFileName) return

        writeFile(file)
    }
}

private fun writeFile(file: File) {
    if (file.isFile.not()) return

    var className: String = ""

    val lineList = file.readLines().flatMap { line ->
        when {
            line.startsWith("package") -> listOf(line.replace("color", "color1"))
            line.contains("isDarkTheme: Boolean") -> emptyList()
            line.contains("commonColors: CommonColors") -> emptyList()
            line.contains("isDarkTheme = isDarkTheme") -> emptyList()
            line.contains("nightColor = null") -> emptyList()
            line.contains("import nbe.someone.code.ui.impl.common.theme.color.CommonColors") -> emptyList()
            line.contains("import nbe.someone.code.ui.impl.common.theme.color.DayNightColor") -> emptyList()
            line.contains("DayNightColor = DayNightColor") -> listOf(line.replace("DayNightColor", "Color"))
            line.contains("dayColor") -> listOf(line.replace("dayColor = {", "").replace("},", ""))
            line.contains("internal") -> {
                className = line.substring(line.lastIndexOf(" ") + 1, line.indexOf("("))

                listOf(
                    "import androidx.compose.ui.graphics.Color",
                    "import nbe.someone.code.ui.impl.common.theme.color1.CommonColors",
                    "import nbe.someone.code.ui.impl.common.theme.color1.base.PageColors",
                    "import nbe.someone.code.ui.impl.common.theme.color1.base.PageColorsProvider",
                    line
                )
            }

            else -> listOf(line)
        }
    }

    val resultList = mutableListOf<String>()

    var hasDefaultBgColor = false



    for (i in lineList.indices) {
        val line = lineList[i]

        if (line.contains("val pageBgColor: Color = Color(")) {
            hasDefaultBgColor = true
        }
    }
    for (i in lineList.indices) {
        val line = lineList[i]

        when {
            line.contains("0xFF") -> continue
            line.contains("),") -> continue
            line.contains("Color = Color(") && line.contains("pageBgColor") -> {
                resultList.add("override ${line}${lineList[i + 1]}${lineList[i + 2]}".replace("\n", ""))
            }

            line.contains("Color = Color(") -> {
                resultList.add("${line}${lineList[i + 1]}${lineList[i + 2]}".replace("\n", ""))
            }

            line.startsWith(")") -> {
                resultList.add(") : PageColors {")
                resultList.add("    companion object : PageColorsProvider<$className> {")
                resultList.add("        override fun provider(isDarkTheme: Boolean, commonColors: CommonColors): $className {")
                if (hasDefaultBgColor) {
                    resultList.add("            return $className()")
                } else {
                    resultList.add("            return $className(commonColors.pageBgColor)")
                }
                resultList.add("        }")
                resultList.add("    }")
                resultList.add("}")
            }

            else -> resultList.add(line)
        }

    }

    file.writeText(resultList.joinToString("\n"))
}