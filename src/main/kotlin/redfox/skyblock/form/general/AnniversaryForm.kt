package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.item.Item
import cn.nukkit.item.ItemID
import cn.nukkit.item.enchantment.Enchantment
import redfox.skyblock.data.Database
import redfox.skyblock.data.VIP
import redfox.skyblock.model.HistoryRecord
import redfox.skyblock.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

object AnniversaryForm {

    private val daysStr = 1

    fun send(player: Player) {
        val name = player.name
        val firstJoinUnix = Database.getFirstJoinUnix(name) ?: return
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("tr", "TR"))
        val formattedDate = dateFormat.format(Date(firstJoinUnix * 1000))

        val form = SimpleForm("Yıl Dönümü Kutlaması")
        form.addElement(
            ElementLabel(
                """
        §l§bHoş Geldin §e${player.name}§b!
        
        §r
        §7Bugün §b$formattedDate §7tarihinde sunucumuza ilk kez giriş yaptığın günün
        §e§l1. Yıl Dönümü§r§7'nü kutluyoruz.

        §r§aBu özel gün için sana bazı ödüller hazırladık:

        §6- §f1x §d§lNetherite§r
        §6- §f100 Kredi§r
        §6- §a500.000 TL §foyun parası
        §6- §f1x §dÖzel Yıl Dönümü Rozeti
        §6- §f1 Günlük §6MVIP §3üyeliği
        §6- §f5x Haykır hakkı
        §6- §3Nadir 'Yıl Dönümü' kiti

        §r
        §bİyi ki bizimlesin, nice yıllara!
        """.trimIndent()
            )
        )
        form.addElement(ElementButton("Ödülü Al", ButtonImage(ButtonImage.Type.PATH, "textures/ui/gift_square.png")))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) send(player)
            Database.setLastAnniversaryRewardUnix(name, System.currentTimeMillis() / 1000)
            player.sendMessage("§7<------------------------------>")
            player.sendMessage("\n§3İyi ki bizim oyuncumuzsun :)\n\n")
            player.sendMessage("§7<------------------------------>")
            player.inventory.addItem(Item.get(ItemID.NETHERITE_INGOT, 0, 1))
            Database.addCredi(player.name, 100)
            Database.addCrediRecord(
                player.name,
                HistoryRecord(
                    name = player.name,
                    action = "§aKredi Alındı§7 <- Yıl Dönümü Hediyesi",
                    amount = 100
                )
            )

            Database.addMoney(player.name, 500000)
            VIP.addVIP(player.name, "MVIP", daysStr)
            Database.addShoutRight(player, 5)
            Database.addBadge(player.name, "Yıl Dönümü Rozeti")

            val sword = Item.get(ItemID.DIAMOND_SWORD, 0, 1).setCustomName("§r§dYıl Dönümü Kılıcı")
            sword.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY).setLevel(3))
            sword.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL).setLevel(3))
            val pickaxe = Item.get(ItemID.DIAMOND_PICKAXE, 0, 1).setCustomName("§r§dYıl Dönümü Kazması")
            sword.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY).setLevel(3))
            sword.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL).setLevel(3))
            val axe = Item.get(ItemID.DIAMOND_AXE, 0, 1).setCustomName("§r§dYıl Dönümü Baltası")
            sword.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY).setLevel(3))
            sword.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL).setLevel(3))
            player.inventory.addItem(sword, pickaxe, axe)
            Utils.sound(player, "note.harp")
        }
    }
}
