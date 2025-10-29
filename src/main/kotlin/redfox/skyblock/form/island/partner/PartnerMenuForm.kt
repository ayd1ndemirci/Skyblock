package redfox.skyblock.form.island.partner

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.IslandDB
import redfox.skyblock.form.island.IslandForm
import redfox.skyblock.permission.Permission

object PartnerMenuForm {
    fun send(player: Player, isPartner: Boolean = false) {
        val actualOwner = if (isPartner) {
            IslandDB.getPlayerPartner(player.name) ?: run {
                player.sendMessage("§cAda sahibi bulunamadı.")
                return
            }
        } else {
            player.name
        }

        val form = SimpleForm("Ortaklık Menüsü")

        form.addElement(ElementButton("Ada Üyeleri"))
        form.addElement(ElementButton("Ortak Ekle"))
        if (!isPartner) {
            form.addElement(ElementButton("Ortak Çıkar"))
        }
        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))
        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            when (response.button().text()) {
                "Geri" -> IslandForm.send(player)

                "Ada Üyeleri" -> {
                    val partners = IslandDB.getIsland(actualOwner)
                        ?.getList("partners", String::class.java)
                        ?.toMutableList()
                        ?: mutableListOf()

                    if (partners.isEmpty()) {
                        player.sendMessage("§l§6Ada §r§cSen ve benden başka hiç bir üye yok..")
                        return@onSubmit
                    }

                    val listed = partners.joinToString(", ") {
                        if (isPartner && it == player.name) "$it §7(Sen)§e" else it
                    }
                    player.sendMessage("§l§6Ada §r§6Toplamda §g${partners.size} §6adet üye listelendi: §e$listed")
                }

                "Ortak Ekle" -> {
                    val currentPartners = IslandDB.getIsland(actualOwner)
                        ?.getList("partners", String::class.java)
                        ?: emptyList()

                    val maxPartners = when {
                        player.hasPermission(Permission.MVIP) -> 8
                        player.hasPermission(Permission.VIP_PLUS) -> 6
                        player.hasPermission(Permission.VIP) -> 5
                        else -> 3
                    }

                    if (currentPartners.size >= maxPartners) {
                        player.sendMessage("§l§6Ada §r§cOrtak ekleme sınırına ulaştınız.")
                        return@onSubmit
                    }

                    PartnerAddForm.send(player)
                }

                "Ortak Çıkar" -> {
                    val currentPartners = IslandDB.getIsland(actualOwner)
                        ?.getList("partners", String::class.java)
                        ?: emptyList()

                    if (currentPartners.isEmpty()) {
                        player.sendMessage("§l§6Ada §r§cHiç ortağın yok!")
                        return@onSubmit
                    }

                    PartnerRemoveForm.send(player)
                }
            }
        }
    }
}
