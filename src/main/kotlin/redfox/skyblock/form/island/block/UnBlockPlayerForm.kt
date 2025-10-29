package redfox.skyblock.form.island.block

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.IslandDB

object UnBlockPlayerForm {

    fun send(player: Player, blockeds: List<String>) {
        val form = CustomForm("Oyuncu Engelini Kaldır")
        form.addElement(ElementDropdown("Engelli Kişiyi Seç:", blockeds))

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(0).elementId()
            if (selectedIndex !in blockeds.indices) return@onSubmit

            val blocked = blockeds[selectedIndex]
            val updatedBlockeds = blockeds.toMutableList().apply { remove(blocked) }

            IslandDB.updateBlockedsPlayer(player.name, updatedBlockeds)

            player.sendMessage("§2$blocked §aadlı oyuncunun ada engeli kaldırıldı.")
        }

        form.send(player)
    }
}
