package redfox.skyblock.form.bounty

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import org.bson.Document
import redfox.skyblock.data.Bounty
import redfox.skyblock.data.Database

object BountyOfferRemoveForm {
    fun send(player: Player, doc: Document) {
        val form = CustomForm("Kelle Avcısı - Teklif Sil")
        form.addElement(ElementLabel("§eHedef oyuncu: ${doc.getString("target")}"))
        form.addElement(ElementLabel("§eÖdül miktarı: §e${doc.getInteger("reward")}RP"))
        form.addElement(ElementToggle("§cBu teklifi silmek istediğinize emin misiniz?", false))
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            val accept = response.getToggleResponse(2)
            if (accept) {
                Bounty.remove(doc.getString("target"))
                Database.addMoney(player.name, doc.getInteger("reward"))
                player.sendMessage("§aTeklif başarıyla silindi!")
            } else BountyForm.send(player)
        }
    }
}