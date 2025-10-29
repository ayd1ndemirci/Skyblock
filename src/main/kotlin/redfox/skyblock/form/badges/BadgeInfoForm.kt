package redfox.skyblock.form.badges

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.BadgesManager

object BadgeInfoForm {

    fun send(player: Player, badgeName: String) {
        val form = SimpleForm(badgeName)

        val description = BadgesManager.getBadgeDescription(badgeName)
        BadgesManager.getBadgeImage(badgeName)

        form.addElement(ElementLabel(description))
        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))

        form.send(player)

        form.onSubmit { sender, response ->
            if (sender !is Player || response == null) return@onSubmit

            if (response.buttonId() == 0) BadgesForm.send(sender)
        }
    }
}
