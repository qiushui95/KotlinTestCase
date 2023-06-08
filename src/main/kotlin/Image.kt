import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

val imgDir = File("D://img")

fun main() = runBlocking {

    val json = File("D://111.json").readText()

    val jsonArray = JSON.parseArray(json)

    val list = mutableListOf<String>()

    repeat(jsonArray.size) {
        list.add(jsonArray.getJSONObject(it).getString("image"))
    }

    val client = OkHttpClient.Builder().build()

    list.map {
        launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://2d-img.gamekee.com/${it}?x-oss-process=image/watermark,image_d2F0ZXIucG5n,fill_1,image/resize,w_200,/format,webp")
                .build()

            val response = client.newCall(request).execute()

            val file = File(imgDir, it)

            file.parentFile.mkdirs()
            file.createNewFile()

            response.body?.byteStream()?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }.forEach { it.join() }

    copyFile(imgDir)
}

private fun copyFile(file: File) {
    if (file.isFile) {
        if (file.parentFile == imgDir) return

        file.copyTo(File(imgDir, file.name))

        file.delete()
    } else if (file.isDirectory) {
        file.listFiles()?.forEach {
            copyFile(it)
        }
    }
}