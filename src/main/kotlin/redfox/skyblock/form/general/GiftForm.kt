package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.element.custom.ElementSlider
import cn.nukkit.form.window.CustomForm
import cn.nukkit.item.Item
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils

object GiftForm {

    fun send(player: Player, item: Item) {
        val players = Server.getInstance().onlinePlayers.values
            .filter { it.name != player.name }
            .map { it.name }
            .sorted()

        if (players.isEmpty()) {
            player.sendMessage("§cŞu anda çevrimiçi başka oyuncu yok!")
            return
        }

        if (item.isNull || item.count <= 0) {
            player.sendMessage("§cElinde geçerli bir eşya bulunmuyor!")
            return
        }

        val form = CustomForm("Hediye Gönder")
        form.addElement(ElementDropdown("§7Gönderilecek oyuncu", players))

        val maxCount = item.count.coerceAtMost(64)
        val hasQuantityChoice = maxCount > 1
        if (hasQuantityChoice) {
            form.addElement(
                ElementSlider("Gönderilecek miktar", 1f, maxCount.toFloat(), 1).defaultValue(1f)
            )
        }

        form.send(player)

        form.onSubmit { _, response ->
            val handItem = player.inventory.itemInHand
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(0).elementId()
            val targetName = players.getOrNull(selectedIndex)

            if (targetName == null) {
                player.sendMessage("§8» §cGeçersiz oyuncu seçimi.")
                return@onSubmit
            }

            val target = Server.getInstance().getPlayerExact(targetName)
            if (target == null || !target.isOnline) {
                player.sendMessage("§8» §cOyuncu çevrimiçi değil.")
                return@onSubmit
            }

            if (
                handItem.id != item.id ||
                handItem.name != item.name ||
                handItem.count != item.count ||
                handItem.customName != item.customName ||
                (handItem.namedTag == null && item.namedTag != null) ||
                (handItem.namedTag != null && item.namedTag == null) ||
                (handItem.namedTag != null && item.namedTag != null && handItem.namedTag != item.namedTag) ||
                handItem.enchantments.size != item.enchantments.size ||
                !handItem.enchantments.all { handEnch ->
                    item.enchantments.any { itemEnch ->
                        itemEnch.id == handEnch.id && itemEnch.level == handEnch.level
                    }
                }
            ) {
                player.sendMessage("§8» §cEşyayı değiştirme len!")
                Utils.sound(player, "note.bass")
                return@onSubmit
            }

            val quantity = if (hasQuantityChoice) response.getSliderResponse(1).toInt() else 1

            if (quantity <= 0 || quantity > item.count || quantity > 64) {
                player.sendMessage("§8» §cGeçersiz miktar seçimi.")
                return@onSubmit
            }

            if (!Database.isSettingEnabled(target.name, "gift")) {
                player.sendMessage("§8» §c${target.name} adlı oyuncu hediye alımını kapatmış.")
                return@onSubmit
            }

            if (target.level.name in Utils.BlockedWorlds.forGiftRequests) {
                player.sendMessage("§8» §c${target.name} adlı oyuncu ${target.level.name} dünyasında olduğu için hediye gönderemezsin.")
                return@onSubmit
            }

            val giftItem = item.clone()
            giftItem.count = quantity

            if (!target.inventory.canAddItem(giftItem)) {
                player.sendMessage("§8» §c${target.name} adlı oyuncunun envanteri dolu.")
                return@onSubmit
            }

            val removed = player.inventory.removeItem(giftItem)
            if (removed.isNotEmpty()) {
                player.sendMessage("§8» §cEşya envanterden çıkarılamadı.")
                return@onSubmit
            }

            target.inventory.addItem(giftItem)
            player.sendMessage("§8» §2§o${target.name} §r§aisimli oyuncuya §o§2${quantity}x ${giftItem.name} §r§aeşyasını gönderdin.")
            target.sendMessage("§8» §o§2${player.name} §r§aisimli oyuncu sana §2§o${quantity}x ${giftItem.name} §r§aeşyasını gönderdi.")
            Utils.sound(player, "note.harp")
            Utils.sound(target, "note.harp")
        }
    }
}