package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.form.faction.FactionMemberMainForm.addFactionInfo

object FactionOwnerMainForm {
    fun send(player: Player) {
        val member = Factions.getMember(player)
        val role = member?.getInteger("role")
        if (member == null) return NoFactionMainForm.send(player)
        if (role != FactionMemberRole.OWNER.ordinal) return FactionsForm.send(player)
        val form = SimpleForm("Klan Menüsü")
        addFactionInfo(member.getString("factionName"), form)
        form.addElement(ElementButton("Klan Ara"))
        form.addElement(ElementButton("En Güçlü Klanlar"))
        form.addElement(ElementButton("Üyeler"))
        form.addElement(ElementButton("Yasaklanan Üyeler"))
        form.addElement(ElementButton("Müttefikler"))
        form.addElement(ElementButton("Klanı Devret"))
        form.addElement(ElementButton("§c§lKlanı Sil"))
        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            when (response.buttonId()) {
                0 -> FactionSearchForm.send(player) { send(player) }
                1 -> TopFactionsForm.send(player) { send(player) }
                2 -> FactionMembersForm.send(player) { send(player) }
                3 -> FactionBannedMembersForm.send(player) { send(player) }
                4 -> FactionAlliesForm.send(player) { send(player) }
                5 -> FactionTransferForm.send(player) { send(player) }
                6 -> FactionDisbandForm.send(player) { send(player) }
            }
        }
    }
}