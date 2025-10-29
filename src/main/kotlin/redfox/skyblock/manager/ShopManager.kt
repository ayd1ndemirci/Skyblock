package redfox.skyblock.manager

import cn.nukkit.Server
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object ShopManager {
    private val data: MutableMap<String, CategoryData> = mutableMapOf()

    fun load() {
        val file = File("plugins/Core/shop.json")
        if (!file.exists()) return
        val json = file.readText()
        val type = object : TypeToken<Map<String, CategoryData>>() {}.type
        val parsed = Gson().fromJson<Map<String, CategoryData>>(json, type)
        data.putAll(parsed)
    }

    fun getCategories(): List<String> = data.keys.toList()

    fun getCategoryImage(category: String): String = data[category]?.image ?: ""

    fun getItems(category: String): List<ShopItemData> = data[category]?.items ?: emptyList()
}
