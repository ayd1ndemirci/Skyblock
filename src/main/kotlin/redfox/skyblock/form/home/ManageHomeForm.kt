package redfox.skyblock.form.home

import cn.nukkit.Player
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import cn.nukkit.level.Position
import redfox.skyblock.data.HomeDB
import redfox.skyblock.model.Home

object ManageHomeForm {
    fun send(player: Player, homeName: String) {
        val homes = HomeDB.getHomes(player)
        val home = homes[homeName] ?: return player.sendMessage("§cEv bulunamadı.")

        val form = SimpleForm(homeName)
        form.addElement(ElementButton("Işınlan", ButtonImage(ButtonImage.Type.PATH, "textures/items/ender_pearl.png")))
        form.addElement(
            ElementButton(
                "Pozisyonu Güncelle",
                ButtonImage(ButtonImage.Type.PATH, "textures/ui/settings_glyph_color_2x.png")
            )
        )
        form.addElement(ElementButton("Sil", ButtonImage(ButtonImage.Type.PATH, "textures/ui/icon_trash.png")))

        form.onSubmit { _, response ->
            when (response.buttonId()) {
                0 -> teleport(player, home)
                1 -> updatePosition(player, homeName)
                2 -> {
                    HomeDB.deleteHome(player, homeName)
                    player.sendMessage("§cEv silindi.")
                }
            }
        }

        form.send(player)
    }

    private fun teleport(player: Player, home: Home) {
        val level = player.server.getLevelByName(home.world)
        if (level == null) {
            player.sendMessage("§cEv dünyası bulunamadı.")
            return
        }

        player.teleport(Position(home.x, home.y, home.z, level))
        player.sendMessage("§a${home.name} evine ışınlandın.")
    }

    private fun updatePosition(player: Player, homeName: String) {
        val pos = player.location
        val newHome = Home(homeName, pos.level.name, pos.x, pos.y, pos.z)
        HomeDB.setHome(player, homeName, newHome)
        player.sendMessage("§a${homeName} evinin pozisyonu güncellendi!")
    }
}
