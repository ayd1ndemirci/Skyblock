package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.utils.Utils

object ClearForm {
    fun send(player: Player) {
        val form = CustomForm("Envanter Temizleme Menüsü")
        form.addElement(ElementLabel("Envanterini temizlemek istediğine emin misin?"))
        form.addElement(ElementToggle("Onaylıyorum", false))
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            val accept = response!!.getToggleResponse(1)
            if (accept) {
                val inventory = player.inventory
                inventory?.clearAll()
                player.sendMessage("§aEnvanteriniz başarıyla temizlendi.")
                Utils.sound(player, "note.harp")
            } else {
                player.sendMessage("§cEnvanter temizleme işlemi iptal edildi.")
            }
        }
    }
}