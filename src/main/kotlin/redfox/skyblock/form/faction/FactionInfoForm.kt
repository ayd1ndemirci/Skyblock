package redfox.skyblock.form.faction

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.data.Factions.FactionRequestType
import redfox.skyblock.form.faction.FactionMemberMainForm.addFactionInfo
import redfox.skyblock.utils.Utils

object FactionInfoForm {
    fun send(player: Player, factionName: String, back: (() -> Unit)?) {
        if (!Factions.exists(factionName)) {
            if (back != null) back()
            else player.sendMessage("§cKlan bulunamadı.")
            return
        }
        val member = Factions.getMember(player)
        val form = SimpleForm("Klan Bilgi - $factionName")
        addFactionInfo(factionName, form)
        val hasMemberRequest = Factions.hasRequest(player.name, factionName, FactionRequestType.MEMBER)
        if (hasMemberRequest) form.addElement(ElementLabel("§dKlana katılma isteğin beklemede..."))
        val isOwner = member != null && member.getInteger("role") == FactionMemberRole.OWNER.ordinal
        val hasSentAllyRequest = member != null && isOwner
                && Factions.hasRequest(member.getString("factionName"), factionName, FactionRequestType.ALLIANCE)
        if (hasSentAllyRequest) form.addElement(ElementLabel("§dKlana atılan müttefiklik isteği beklemede... (Bunu sadece sen ve karşı klan sahibi görebilir)"))
        val areAllies = member != null && Factions.areAllies(member.getString("factionName"), factionName)
        if (areAllies) form.addElement(ElementLabel("§aBu klan ile müttefiksiniz!"))
        val actions = mutableListOf<() -> Unit>()
        if (member == null && !hasMemberRequest && !Factions.getBannedMembers(factionName).contains(player.name)) {
            form.addElement(ElementButton("Katılma isteği gönder"))
            actions.add {
                val faction = Factions.get(factionName)
                if (faction == null) {
                    if (back != null) back()
                    else player.sendMessage("§cKlan bulunamadı.")
                    return@add
                }
                if (Factions.getMember(player) != null) {
                    send(player, factionName, back)
                    return@add
                }
                if (Factions.getBannedMembers(factionName).contains(player.name)) {
                    send(player, factionName, back)
                    return@add
                }
                if (!Utils.checkCooldown("${player.name}\n$factionName\njoin", 10000 * 60)) {
                    player.sendMessage("§cBu klana yakın bir zamanda katılma isteği attın, lütfen daha sonra tekrar dene.")
                    return@add
                }
                if (Factions.addRequest(player.name, factionName, FactionRequestType.MEMBER)) {
                    player.sendMessage("§aKlan katılma isteğin başarıyla gönderildi.")
                    player.server.getPlayerExact(faction.getString("owner"))
                        .sendMessage("§a${player.name} adlı oyuncu klanınıza katılma isteği gönderdi.")
                } else {
                    send(player, factionName, back)
                }
            }
        }
        if (member != null && isOwner && member.getString("factionName") != factionName) {
            if (!areAllies && !hasSentAllyRequest) {
                form.addElement(ElementButton("Müttefiklik İsteği Gönder"))
                actions.add { FactionAllianceSendForm.send(player, factionName) { send(player, factionName, back) } }
            }
            if (Factions.hasRequest(factionName, member.getString("factionName"), FactionRequestType.ALLIANCE)) {
                form.addElement(ElementButton("Müttefiklik İsteğini Cevapla"))
                actions.add { FactionAllianceAnswerForm.send(player, factionName) { send(player, factionName, back) } }
            }
            if (areAllies) {
                form.addElement(ElementButton("Müttefiklikten Çıkart"))
                actions.add { FactionAllyRemoveForm.send(player, factionName) { send(player, factionName, back) } }
            }
            if (!areAllies && !hasSentAllyRequest) {
                form.addElement(ElementButton("Klana Savaş Aç"))
                actions.add { FactionStartWarForm.send(player, factionName) { send(player, factionName, back) } }
            }
            if (!areAllies && !hasSentAllyRequest && Factions.warRequests.contains(
                    Pair(
                        factionName,
                        member.getString("factionName")
                    )
                )
            ) {
                form.addElement(ElementButton("Klan Savaşı İsteğini Cevapla"))
                actions.add { FactionWarAnswerForm.send(player, factionName) { send(player, factionName, back) } }
            }
        }
        if (back != null) {
            form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))
            actions.add {
                back()
            }
        }
        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response != null) actions[response.buttonId()]()
        }
    }
}