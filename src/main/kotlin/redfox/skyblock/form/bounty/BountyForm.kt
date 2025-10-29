package redfox.skyblock.form.bounty

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Bounty

object BountyForm {
    fun send(player: Player) {
        val form = SimpleForm("Kelle Avcısı")
        form.addElement(ElementButton("Teklifleri Görüntüle"))
        form.addElement(ElementButton("Verdiğim Teklifleri Görüntüle"))
        form.addElement(ElementButton("Kelleme Koyulmuş Teklifleri Görüntüle"))
        form.addElement(ElementButton("Teklif Ver"))
        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            when (response.buttonId()) {
                0 -> BountyOfferViewForm.send(player, Bounty.BountyViewMode.ALL)
                1 -> BountyOfferViewForm.send(player, Bounty.BountyViewMode.WARLORD)
                2 -> BountyOfferViewForm.send(player, Bounty.BountyViewMode.TARGET)
                3 -> BountyOfferGiveForm.send(player)
            }
        }
    }
}