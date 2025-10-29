package redfox.skyblock.utils

import cn.nukkit.item.Item
import org.json.JSONObject
import redfox.skyblock.Core
import java.io.File
import java.util.*

object InventoryUtils {

    private val logFile: File = Core.dupeLogFile

    fun processItem(playerName: String, item: Item) {
        if (item.id == Item.AIR.id || item.count <= 0) return

        val tag = item.namedTag ?: cn.nukkit.nbt.tag.CompoundTag().also { item.namedTag = it }
        var uuid = tag.getString("uuid")

        if (uuid == null || uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString()
            tag.putString("uuid", uuid)
            item.namedTag = tag
        }

        val jsonText = if (logFile.exists()) logFile.readText() else "{}"
        val json = JSONObject(jsonText)

        if (json.has(uuid) && json.getString(uuid) != playerName) {
            Core.instance.logger.warning("Dupe detected! UUID: $uuid Oyuncular: ${json.getString(uuid)} & $playerName")
        }

        json.put(uuid, playerName)
        logFile.writeText(json.toString(4))
    }
}
