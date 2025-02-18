import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import java.io.File

object ConfigManager {
    private const val CONFIG_FILE = "config.json"
    val CONFIG: JSONObject

    init {
        CONFIG = loadConfig()
    }

    private fun loadConfig(): JSONObject {
        // 从resources目录加载配置文件
        val resourceStream = ConfigManager::class.java.getResourceAsStream("/$CONFIG_FILE")
            ?: ConfigManager::class.java.classLoader.getResourceAsStream(CONFIG_FILE)

        return try {
            if (resourceStream != null) {
                resourceStream.use { stream ->
                    stream.reader().use { reader ->
                        JSON.parseObject(reader.readText())
                    }
                }
            } else {
                JSONObject()
            }
        } catch (e: Exception) {
            println("配置文件读取失败: ${e.message}")
            JSONObject()
        }
    }
}