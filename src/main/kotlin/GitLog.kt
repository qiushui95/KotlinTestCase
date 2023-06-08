import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import org.eclipse.jgit.api.Git
import java.io.File

fun main() {
    val git = Git.open(File("D:\\Code\\SSR\\.git"))

    val commits = git.log().all().call()

    val commitArray = JSONArray()

    for (commit in commits.sortedBy { it.commitTime*-1 }) {
        val commitObject = JSONObject()
        commitObject["id"] = commit.name
        commitObject["msg"] = commit.shortMessage

        commitArray.add(commitObject)
    }

    val jsonFile = File("D://1024.json")

    jsonFile.writeText(commitArray.toJSONString())
}