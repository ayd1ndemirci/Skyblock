package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.form.general.ListingForm
import redfox.skyblock.form.general.ProfileForm
import redfox.skyblock.utils.Utils

object FactionBannedMembersForm {
    fun send(player: Player, pageGot: Int = 0, back: (() -> Unit)?) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        val role = member.getInteger("role")
        val permitted = role >= FactionMemberRole.OFFICER.ordinal
        val allMembers = Factions.getBannedMembers(member.getString("factionName"))

        var page = pageGot
        val pageCount = Utils.pageCount(allMembers.size)
        if (page < 0) page = 0
        if (page >= pageCount) page = pageCount - 1
        val members = Utils.page(allMembers, page)

        ListingForm.send(
            player,
            SimpleForm("Yasaklanan Klan Üyeleri"),
            members, page, pageCount,
            "Hiç kimse yasaklanmamış :)",
            { member, _ -> "$member${if (permitted) "\n§cYasağını kaldırmak için tıkla" else ""}" },
            { ProfileForm.send(player, it) { send(player, pageGot, back) } },
            { send(player, it, back) }, back
        )
    }
}