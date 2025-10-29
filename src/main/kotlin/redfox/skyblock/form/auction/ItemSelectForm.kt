package redfox.skyblock.form.auction

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.CustomForm
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.utils.Utils

object ItemSelectForm {

    private val playerSlotMap = mutableMapOf<String, List<Int>>()

    fun send(player: Player, dropdownMessage: String = "") {
        val inventory = player.inventory

        val itemSlots = mutableListOf<Int>()
        val displayList = mutableListOf<String>()

        for (slot in 0 until inventory.size) {
            val item = inventory.getItem(slot)
            if (item.name != cn.nukkit.block.Block.AIR && item.count > 0) {
                itemSlots.add(slot)
                val display = "${slot}. slot - ${item.customName.takeIf { it.isNotBlank() } ?: item.name}"
                displayList.add(display)
            }
        }

        if (displayList.isEmpty()) {
            SimpleForm("Hata")
                .addElement(ElementLabel("§cEnvanterinde gösterilecek bir eşya yok!"))
                .addElement(ElementLabel(" "))
                .addElement(ElementLabel(" "))
                .addElement(ElementLabel(" "))
                .addElement(ElementButton("Geri"))
                .send(player)
                .onSubmit { _, response ->
                    if (response == null) return@onSubmit
                    if (response.buttonId() == 0) AuctionForm.send(player)
                }
            return
        }

        playerSlotMap[player.name] = itemSlots

        val form = CustomForm("Eşya Seç")
        form.addElement(ElementDropdown(dropdownMessage, displayList))

        form.send(player)

        form.onSubmit { _, response ->
            val selectedIndex = response.getDropdownResponse(0).elementId()
            val slots = playerSlotMap[player.name] ?: return@onSubmit
            val selectedSlot = slots.getOrNull(selectedIndex)

            if (selectedSlot != null) {
                AuctionCreateForm.send(player, player.inventory.getItem(selectedSlot), player.inventory.heldItemIndex)
            } else send(player, "§cGeçersiz eşya, tekrar seç.")

            playerSlotMap.remove(player.name)
        }
    }
}
