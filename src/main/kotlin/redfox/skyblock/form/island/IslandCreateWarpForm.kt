package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB

object IslandCreateWarpForm {

    fun send(player: Player) {
        val existingWarpsDoc = IslandDB.getIsland(player.name)?.get("warps", org.bson.Document::class.java)
        val existingWarps = existingWarpsDoc?.entries?.associate { it.key to it.value.toString() } ?: emptyMap()
        val maxWarpLimit = Island.getMaxWarpLimit(player)
        val currentWarpCount = existingWarps.size

        if (currentWarpCount >= maxWarpLimit) {
            player.sendMessage("§cAda warp oluşturma sınırına ulaştınız! ($currentWarpCount/$maxWarpLimit)")
            return
        }

        val form = CustomForm("Ada Warp Oluşturma")

        form.addElement(ElementLabel("§8Aşağıdaki kutucuğa oluşturulacak ada noktası adı girin."))
        form.addElement(ElementInput("\n§7İsim Gir", "Örn: Sandık odası"))

        form.onSubmit { _, response ->
            val warpName = response.getInputResponse(1)?.trim() ?: ""

            if (warpName.isEmpty()) {
                player.sendMessage("§cBir ad girmelisin.")
                return@onSubmit
            }
            if (warpName.contains("§")) {
                player.sendMessage("§cLütfen ad kısmında renk kodu kullanmayınız!")
                return@onSubmit
            }
            if (warpName.length > 16) {
                player.sendMessage("§cAda warp adı en fazla 16 karakter olabilir! (Şu an: ${warpName.length})")
                return@onSubmit
            }
            if (existingWarps.containsKey(warpName)) {
                player.sendMessage("§c'$warpName' adında bir warp zaten mevcut!")
                return@onSubmit
            }
            if (player.level.name != player.name) {
                player.sendMessage("§cAda warpı oluşturmak için adanızda olmanız gerekmektedir!")
                return@onSubmit
            }

            val pos = player.position
            val posString = "${pos.floorX}:${pos.floorY}:${pos.floorZ}"

            val newWarps = existingWarps.toMutableMap()
            newWarps[warpName] = posString

            val newWarpsDoc = org.bson.Document()
            newWarps.forEach { (k, v) -> newWarpsDoc.append(k, v) }

            IslandDB.collection.updateOne(
                com.mongodb.client.model.Filters.eq("player", player.name),
                com.mongodb.client.model.Updates.set("warps", newWarpsDoc)
            )

            player.sendMessage("§g'$warpName' adlı warp noktası oluşturuldu.")
        }

        form.send(player)
    }
}
