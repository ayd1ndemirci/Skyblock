package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.group.GroupManager
import redfox.skyblock.utils.Utils

object TagForm {

    fun send(player: Player) {
        val groups = GroupManager.getPlayerGroups(player.name)
            .map { it.name }
            .sorted()

        if (groups.isEmpty()) {
            player.sendMessage("§cHerhangi bir tagın yok!")
            return
        }

        val form = CustomForm("Tag Değiştir")
        form.addElement(ElementDropdown("Tag seç", groups))
        form.addElement(ElementLabel("\n§cNot: §eSeçtiğin tagın yetkilerini otomatik olarak alırsın."))

        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit

            val selectedIndex = response.getDropdownResponse(0).elementId()
            if (selectedIndex !in groups.indices) {
                player.sendMessage("§cGeçersiz tag seçimi.")
                Utils.sound(player, "item.trident.hit_ground")
                return@onSubmit
            }

            val selectedTag = groups[selectedIndex]
            val selectedGroup = GroupManager.getGroup(selectedTag)

            if (selectedGroup == null) {
                player.sendMessage("§cGeçersiz tag seçimi.")
                Utils.sound(player, "item.trident.hit_ground")
                return@onSubmit
            }

            GroupManager.setPlayerGroup(player.name, selectedGroup)
            player.sendMessage("§8» §aTagın §2§o$selectedTag §r§aolarak değiştirildi!")
            Utils.sound(player, "note.harp")
        }
    }
}