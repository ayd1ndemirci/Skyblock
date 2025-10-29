package redfox.skyblock.form.island.partner

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.ModalForm
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.event.custom.IslandPartnerAddEvent
import redfox.skyblock.utils.IslandUtils

object PartnershipTransactionsForm {

    fun send(player: Player) {
        val invitations = IslandUtils.partnershipInvitations[player.name] ?: emptyList()

        if (invitations.isEmpty()) {
            player.sendMessage("§cOrtaklık isteği yok.")
            return
        }

        val form = SimpleForm("Ortaklık İşlemleri")
        invitations.forEach { name ->
            form.addElement(ElementButton(name))
        }

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            val selectedName = invitations.getOrNull(response.buttonId()) ?: return@onSubmit

            val modal = ModalForm("Ortaklık Kabul Formu")
                .content("§a$selectedName §7adlı oyuncunun adasına ortak olmayı kabul ediyor musun?")
                .text("Evet", "Hayır")
                .onYes { p ->
                    val partner = p.server.getPlayerExact(selectedName)
                    if (partner != null) {
                        val event = IslandPartnerAddEvent(p, partner)
                        event.call()
                        IslandUtils.removePartnershipInvitation(p.name, selectedName)
                    } else {
                        p.sendMessage("§cOrtaklık isteği gönderen oyuncu oyundan çıkmış! İstek silindi.")
                        IslandUtils.removePartnershipInvitation(p.name, selectedName)
                    }
                }
                .onNo { p ->
                    p.sendMessage("§4§o$selectedName §r§cadlı oyuncunun ada ortaklık isteğini reddettiniz.")
                    IslandUtils.removePartnershipInvitation(p.name, selectedName)
                }

            modal.send(player)
        }
    }
}
