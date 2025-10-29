import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.utils.Location
import redfox.skyblock.utils.Utils

object WarpForm {
    fun send(player: Player) {
        val form = SimpleForm("Mekan")
        form.addElement(
            ElementButton(
                "Lobi\n${Location.playerCount("SkyBlock")} kişi var",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/icon_recipe_nature")
            )
        )
        form.addElement(
            ElementButton(
                "Arena\n${Location.playerCount("SkyBlock")} kişi var",
                ButtonImage(ButtonImage.Type.PATH, "textures/items/iron_sword.png")
            )
        )
        form.addElement(
            ElementButton(
                "Nether §c§l(YAKINDA)§r\n${Location.playerCount("SkyBlock")} kişi var",
                ButtonImage(ButtonImage.Type.PATH, "textures/blocks/netherrack.png")
            )
        )
        form.addElement(
            ElementButton(
                "End §c§l(YAKINDA)§r\n${Location.playerCount("SkyBlock")} kişi var",
                ButtonImage(ButtonImage.Type.PATH, "textures/blocks/end_stone.png")
            )
        )
        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    Location.teleportWorld(player, Location.LOBBY)
                    Utils.sound(player, "note.pling")
                }

                1 -> {
                    Location.teleportWorld(player, Location.ARENA)
                    Utils.sound(player, "note.pling")
                }
//                2 -> Location.teleportWorld(player, Location.NETHER)
//                3 -> Location.teleportWorld(player, Location.END)
            }
        }
    }
}
