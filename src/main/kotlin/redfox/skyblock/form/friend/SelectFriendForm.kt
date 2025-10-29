package redfox.skyblock.form.friend

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.level.Sound
import redfox.skyblock.utils.Utils

object SelectFriendForm {

    fun send(player: Player, friendName: String) {
        val friend = Server.getInstance().getPlayerExact(friendName)
        val options = mutableListOf<ElementButton>()

        if (friend != null) {
            options.add(
                ElementButton(
                    "Arkadaşına Işınlan",
                    ButtonImage(ButtonImage.Type.PATH, "textures/items/ender_pearl")
                )
            )
            options.add(
                ElementButton(
                    "Arkadaşlıktan Çıkar",
                    ButtonImage(ButtonImage.Type.PATH, "textures/ui/wither_effect")
                )
            )
            options.add(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left")))
        } else {
            options.add(
                ElementButton(
                    "Arkadaşlıktan Çıkar",
                    ButtonImage(ButtonImage.Type.PATH, "textures/ui/wither_effect")
                )
            )
            options.add(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left")))
        }

        val form = SimpleForm("Arkadaşlık - $friendName")
        options.forEach { form.addElement(it) }

        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit
            val buttonText = response.button().text()
            when (buttonText) {
                "Arkadaşına Işınlan" -> {
                    if (friend != null) {
                        if (friend.isOnline) {
                            if (friend.level.folderName != "arena") {
                                player.teleport(friend.location)
                                player.sendMessage("§2§o$friendName §r§aadlı arkadaşına ışınlandın!")
                                player.level.addSound(player.location, Sound.MOB_ENDERMEN_PORTAL)
                            } else player.sendMessage("§8» §cArkadaşın arena dünyasında olduğu için ışınlanamazsın.")
                        } else player.sendMessage("§8» §cArkadaşın oyundan ayrılmış.")

                        if (friend.isOnline) {
                            friend.sendMessage("§2§o${player.name} §r§aadlı arkadaşın sana ışınlandı!")
                            player.level.addSound(player.location, Sound.MOB_ENDERMEN_PORTAL)
                        }
                    }
                }

                "Arkadaşlıktan Çıkar" -> {
                    FriendDeleteForm.send(player, friendName)
                    Utils.sound(player, "note.harp")
                }

                "Geri" -> {
                    FriendForm.send(player)
                    Utils.sound(player, "note.harp")
                }
            }
        }
    }
}
