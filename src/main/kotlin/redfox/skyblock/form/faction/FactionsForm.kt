package redfox.skyblock.form.faction

import cn.nukkit.Player
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole

object FactionsForm {
    fun send(player: Player) {
        val member = Factions.getMember(player)
        if (member == null) {
            NoFactionMainForm.send(player)
            return
        }

        when (member.getInteger("role")) {
            FactionMemberRole.OWNER.ordinal -> FactionOwnerMainForm.send(player)
            FactionMemberRole.OFFICER.ordinal -> FactionOwnerMainForm.send(player)
            FactionMemberRole.MEMBER.ordinal -> FactionMemberMainForm.send(player)
        }
    }

    fun requireOfficer(player: Player): Boolean {
        val member = Factions.getMember(player)
        if (member == null) {
            NoFactionMainForm.send(player)
            return false
        }
        if (member.getInteger("role") < FactionMemberRole.OFFICER.ordinal) {
            FactionMemberMainForm.send(player)
            return false
        }
        return true
    }
}