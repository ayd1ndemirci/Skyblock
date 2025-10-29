package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole

object FactionDisbandForm {
    fun send(player: Player, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null || member.getInteger("role") != FactionMemberRole.OWNER.ordinal) {
            back?.invoke()
            return
        }
        val form = CustomForm("Klan Sil")
        form.addElement(
            ElementToggle(
                "§a${member.getString("factionName")} §eadlı klanını silmek istediğinden emin misin?",
                false
            )
        )
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            if (!response.getToggleResponse(0)) {
                back?.invoke()
                return@onSubmit
            }

            val form = SimpleForm("Eminsin?")
            form.addElement(ElementLabel("Eminsin?"))
            form.addElement(ElementButton("Evet."))
            form.send(player)

            form.onSubmit { _, response ->
                val form = SimpleForm("Eminsin??????")
                form.addElement(ElementLabel("Emin misin????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????"))
                form.addElement(ElementButton("Hayır."))
                form.addElement(ElementButton("Hayır."))
                form.addElement(ElementButton("Hayır."))
                form.addElement(ElementButton("Hayır."))
                form.addElement(ElementButton("Hayır."))
                form.addElement(ElementButton("Hayır."))
                form.addElement(ElementButton("Evet."))
                form.send(player)

                form.onSubmit { _, response ->
                    if (response == null) return@onSubmit
                    if (response.buttonId() != 6) {
                        back?.invoke()
                        return@onSubmit
                    }

                    val member = Factions.getMember(player)
                    if (member == null || member.getInteger("role") != FactionMemberRole.OWNER.ordinal) {
                        back?.invoke()
                        return@onSubmit
                    }

                    Factions.remove(member.getString("factionName"))

                    player.sendMessage("§aKlanın silindi.")
                }
            }
        }
    }
}