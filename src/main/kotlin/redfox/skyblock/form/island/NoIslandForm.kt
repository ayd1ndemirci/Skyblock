package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.form.island.partner.PartnershipTransactionsForm
import redfox.skyblock.utils.IslandUtils

object NoIslandForm {

    fun send(player: Player) {
        val partnershipCount = IslandUtils.partnershipInvitations[player.name]?.size ?: 0

        val form = SimpleForm("SkyBlock - Ada Menüsü")
        form.addElement(ElementButton("Bir Ada Oluştur"))
        form.addElement(
            ElementButton(
                "Ortaklık İsteği Kabul Et\n" +
                        if (partnershipCount > 0)
                            "§g$partnershipCount §6adet ortaklık isteği var!"
                        else
                            "§cHiç ortaklık isteği yok."
            )
        )

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> CreateIslandForm.send(player)
                1 -> {
                    if (partnershipCount == 0) {
                        player.sendMessage("§cHenüz bir ortaklık isteği bulunmamakta!")
                    } else {
                        if (IslandUtils.partnershipInvitations.containsKey(player.name)) {
                            PartnershipTransactionsForm.send(player)
                        } else player.sendMessage("§cBir sorun oluştu!")
                    }
                }
            }
        }
        form.send(player)
    }
}
