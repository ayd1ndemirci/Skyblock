package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole

object FactionTransferForm {
    fun send(player: Player, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member === null) return NoFactionMainForm.send(player)
        if (member.getInteger("role") != FactionMemberRole.OWNER.ordinal) {
            if (back != null) back()
            else FactionsForm.send(player)
            return
        }

        val factionName = member.getString("factionName")
        val form = CustomForm("Klanı Devretme Onayı")

        val members = Factions.getMembers(factionName)
            .map { it.getString("name") }
            .filter { it != player.name }
            .toMutableList()
        members.add(0, "§c§lSeçiniz")

        form.addElement(ElementDropdown("§aKlanı devredeceğin klan üyesini seç:", members))
        form.addElement(
            ElementToggle(
                "§e§l${factionName} §r§eadlı klanı transfer etmek istediğinden emin misin?",
                false
            )
        )
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit
            val memberIndex = response.getDropdownResponse(0).elementId()
            if (memberIndex == 0) {
                return@onSubmit
            }
            val member = Factions.getMember(player.name)
            if (member == null) return@onSubmit NoFactionMainForm.send(player)
            val target = members[memberIndex]
            val targetMember = Factions.getMember(target)
            if (targetMember == null) return@onSubmit send(player, back)
            val factionName = member.getString("factionName")
            if (factionName != targetMember.getString("factionName")) {
                return@onSubmit send(player, back)
            }
            if (member.getInteger("role") != FactionMemberRole.OWNER.ordinal) {
                return@onSubmit FactionsForm.send(player)
            }
            if (response.getToggleResponse(1)) {
                Factions.setMember(factionName, targetMember.getString("name"), FactionMemberRole.OWNER)
                Factions.setMember(factionName, player.name, FactionMemberRole.MEMBER)
            } else back?.invoke()
        }
    }
}