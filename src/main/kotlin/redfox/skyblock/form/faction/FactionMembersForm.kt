package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.form.general.ListingForm
import redfox.skyblock.form.general.ProfileForm
import redfox.skyblock.utils.Utils

object FactionMembersForm {
    fun send(player: Player, pageGot: Int = 0, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        val allMembers = Factions.getMembers(member.getString("factionName"))

        var page = pageGot
        val pageCount = Utils.pageCount(allMembers.size)
        if (page < 0) page = 0
        if (page >= pageCount) page = pageCount - 1
        val members = Utils.page(allMembers, page)

        ListingForm.send(
            player,
            SimpleForm("Klan Üyeleri"),
            members, page, pageCount,
            "Klanda hiç üye yok? Nasıl!?",
            { member, _ -> "${member.getString("name")} (${FactionMemberRole.entries[member.getInteger("role")].name})" },
            { ProfileForm.send(player, it.getString("name")) { send(player, pageGot, back) } },
            { send(player, it, back) }, back
        )
    }
}