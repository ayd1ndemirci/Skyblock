package redfox.skyblock.utils

import cn.nukkit.item.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object SellDataLoader {

    private val itemPriceMap = mutableMapOf<Pair<String, Int>, Int>()

    init {
        loadData()
    }

    private fun loadData() {
        val file = File("plugins/Core/sell.json")
        if (!file.exists()) return

        val json = file.readText()
        val type = object : TypeToken<List<Map<String, Any>>>() {}.type
        val data: List<Map<String, Any>> = Gson().fromJson(json, type)

        for (entry in data) {
            val id = entry["id"] as? String ?: continue
            val meta = (entry["meta"] as? Double)?.toInt() ?: 0
            val price = (entry["price"] as? Double)?.toInt() ?: continue
            itemPriceMap[id to meta] = price
        }
    }

    fun getPrice(item: Item): Int {
        return itemPriceMap[item.id to item.damage] ?: 0
    }
}
