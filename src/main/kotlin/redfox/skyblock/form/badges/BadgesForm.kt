package redfox.skyblock.form.badges

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.BadgesManager

object BadgesForm {

    fun send(player: Player) {
        val playerName = player.name.lowercase()
        val form = SimpleForm("Rozetler")

        BadgesManager.allBadges().forEach { badge ->
            val suffix = if (BadgesManager.playerHasBadge(playerName, badge.name)) " \n§5Buna Sahipsin" else ""
            form.addElement(ElementButton(badge.name + suffix, ButtonImage(ButtonImage.Type.PATH, badge.image)))
        }

        form.send(player)

        form.onSubmit { sender, response ->
            if (sender !is Player || response == null) return@onSubmit

            val index = response.buttonId()
            if (index == BadgesManager.allBadges().size) return@onSubmit

            val badge = BadgesManager.allBadges().getOrNull(index) ?: return@onSubmit
            BadgeInfoForm.send(sender, badge.name)
        }
    }
}
