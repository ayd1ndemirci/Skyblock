package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB
import redfox.skyblock.event.custom.IslandCreateEvent
import redfox.skyblock.utils.IslandUtils

object CreateIslandForm {

    fun send(player: Player) {
        val form = SimpleForm("Ada Oluştur")
        form.addElement(ElementLabel("§3Ada oluşturarak eşsiz ve eğlenceli skyblock deneyimine başlayabilirsin."))
        form.addElement(ElementButton("Ada Oluştur"))
        form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    if (Island.hasIsland(player.name)) {
                        player.sendMessage("§cZaten adan var!")
                        return@onSubmit
                    }
                    val event = IslandCreateEvent(player.name)
                    Server.getInstance().pluginManager.callEvent(event)

                    if (event.isCancelled) {
                        player.sendMessage("§cAda oluşumu iptal edildi!")
                        return@onSubmit
                    }

                    IslandDB.createIsland(player.name)
                    val created = IslandUtils.createIslandWorld(player.name)
                    if (created) {
                        player.sendMessage("§aAdan oluşturuldu, iyi oyunlar dileriz!")
                    } else player.sendMessage("§cBeklenmedik hata oluştur")
                }

                1 -> NoIslandForm.send(player)
            }
        }
    }
}
