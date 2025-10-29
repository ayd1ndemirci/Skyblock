package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object FactionMemberMainForm {
    fun send(player: Player) {
        val member = Factions.getMember(player)
        if (member == null) return NoFactionMainForm.send(player)
        if (member.getInteger("role") != FactionMemberRole.MEMBER.ordinal) return FactionOwnerMainForm.send(player)
        val form = SimpleForm("Klan Menüsü")
        addFactionInfo(member.getString("factionName"), form)
        form.addElement(ElementButton("Klan Ara"))
        form.addElement(ElementButton("En Güçlü Klanlar"))
        form.addElement(ElementButton("Klan Üyeleri"))
        form.addElement(ElementButton("Müttefikler"))
        form.addElement(ElementButton("Klan'dan Çık"))
        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            when (response.buttonId()) {
                0 -> FactionSearchForm.send(player) { send(player) }
                1 -> TopFactionsForm.send(player) { send(player) }
                2 -> FactionMembersForm.send(player) { send(player) }
                3 -> FactionAlliesForm.send(player) { send(player) }
                4 -> FactionLeaveForm.send(player) { send(player) }
            }
        }
    }

    fun addFactionInfo(factionName: String, form: SimpleForm) {
        val faction = Factions.get(factionName)!!
        val createdAt = faction.getLong("createdAt")
        val createdAtFormatted = Instant.ofEpochMilli(createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
        form.addElement(ElementLabel("§aKlan Adı: ${faction.getString("name")}"))
        form.addElement(ElementLabel("§aKlan Açıklaması: ${faction.getString("description")}"))
        form.addElement(ElementLabel("§aKlan Gücü: ${faction.getInteger("power")}"))
        form.addElement(ElementLabel("§aKlan Kasası: ${faction.getInteger("money")}RP"))
        form.addElement(ElementLabel("§aKlan Oluşturulma Tarihi: ${createdAtFormatted}RP"))
    }
}