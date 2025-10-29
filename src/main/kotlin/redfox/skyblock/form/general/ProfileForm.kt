package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Bounty
import redfox.skyblock.data.Bounty.BountyViewMode
import redfox.skyblock.data.Database
import redfox.skyblock.data.Factions
import redfox.skyblock.data.Factions.FactionMemberRole
import redfox.skyblock.form.bounty.BountyOfferGiveForm
import redfox.skyblock.form.bounty.BountyOfferViewForm
import redfox.skyblock.form.faction.FactionBanForm
import redfox.skyblock.form.faction.FactionKickForm
import redfox.skyblock.form.faction.FactionMemberAnswerForm
import redfox.skyblock.form.faction.FactionUnbanForm
import redfox.skyblock.form.money.GiveMoneyForm

object ProfileForm {
    fun send(player: Player, target: String, back: (() -> Unit)?) {
        val s = target == player.name
        val form = SimpleForm(if (s) "Profilin" else " - $target")
        val money = Database.getMoney(target)
        val rank = Database.getMoney(target) + 1
        val member = Factions.getMember(target)
        val factionName = member?.getString("factionName")
        val bounty = Bounty.getReward(target)
        val memberSelf = Factions.getMember(player)
        val factionNameSelf = memberSelf?.getString("factionName")
        val roleSelf = memberSelf?.getInteger("role") ?: -1
        val officerSelf = roleSelf >= FactionMemberRole.OFFICER.ordinal
        val inSameFaction = factionNameSelf != null && factionNameSelf == factionName
        form.addElement(ElementLabel("§e${if (s) "Paran" else "Parası"}: $money (Sıralamada #$rank)"))
        form.addElement(ElementLabel("§eKlanı${if (member == null) " Yok" else ": $factionName"}"))
        form.addElement(ElementLabel("§eKellesine Koyulmuş Para: $bounty"))
        form.addElement(ElementButton("Kellesine Para Koyanları Görüntüle"))
        form.addElement(ElementButton("Para Gönder"))
        form.addElement(ElementButton("Kellesine Para Koy"))
        val opts = mutableListOf<() -> Unit>()
        if (officerSelf) {
            if (inSameFaction) {
                form.addElement(ElementButton("Klandan At"))
                opts.add { FactionKickForm.send(player, target) { send(player, target, back) } }
                if (roleSelf == FactionMemberRole.OWNER.ordinal) {
                    if (member?.getInteger("role") == FactionMemberRole.MEMBER.ordinal) {
                        form.addElement(ElementButton("Klan Yardımcısı Yap"))
                        opts.add {
                        }
                    } else {
                        form.addElement(ElementButton("Klan Üyesi Yap"))
                        opts.add {
                        }
                    }
                }
            }
            val bans = Factions.getBannedMembers(factionNameSelf!!)
            if (bans.contains(target)) {
                form.addElement(ElementButton("Klan Yasağını Kaldır"))
                opts.add { FactionUnbanForm.send(player, target) { send(player, target, back) } }
            } else {
                form.addElement(ElementButton("Klandan Yasakla"))
                opts.add { FactionBanForm.send(player, target) { send(player, target, back) } }
            }
        }
        if (roleSelf == FactionMemberRole.OWNER.ordinal
            && Factions.hasRequest(target, factionNameSelf!!, Factions.FactionRequestType.MEMBER)
        ) {
            form.addElement(ElementButton("Klanına Girme İsteğini Cevapla"))
            opts.add { FactionMemberAnswerForm.send(player, target) { send(player, target, back) } }
        }
        if (back != null) form.addElement(
            ElementButton(
                "Geri",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")
            )
        )

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            val button = response.buttonId()
            when (button) {
                0 -> BountyOfferViewForm.send(player, BountyViewMode.TARGET, 0, target)
                1 -> GiveMoneyForm.send(player, target)
                2 -> BountyOfferGiveForm.send(player, target)
                else -> if (opts.size > button - 3) opts[button - 3]() else back?.invoke()
            }
        }
    }
}