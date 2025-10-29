package redfox.skyblock.form.money

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementDropdown
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.Core
import redfox.skyblock.data.Database

object GiveMoneyForm {
    fun send(player: Player, targetGot: String? = null) {
        val form = CustomForm("Para Gönder")
        val players = Core.Companion.instance.server.onlinePlayers
            .filter { it.value.name != player.name }
            .map { it.value.name }
            .toMutableList()
        players.add(0, "§c§lSeçiniz")

        form.addElement(ElementLabel("§eParan: ${Database.getMoney(player)}"))
        if (targetGot == null) {
            form.addElement(ElementDropdown("§eGöndermek istediğin oyuncuyu seç:", players))
        }
        form.addElement(ElementInput("§eGöndermek istediğin miktar:", "Örnek: 1000"))
        form.send(player)

        form.onSubmit { _: Player?, response: CustomResponse? ->
            if (response == null) return@onSubmit

            var target: String? = targetGot

            val amount = response.getInputResponse(if (target == null) 2 else 1)?.toIntOrNull() ?: 0
            if (target == null) {
                val playerIndex = response.getDropdownResponse(0).elementId()
                if (playerIndex == 0) {
                    MoneyForm.send(player)
                    return@onSubmit
                }

                target = players[playerIndex]
            }

            if (amount <= 0) {
                player.sendMessage("§cGeçersiz miktar!")
                return@onSubmit
            }

            if (!Database.hasMoney(player, amount)) {
                player.sendMessage("§cYeterli paran yok!")
                return@onSubmit
            }
            Database.removeMoney(player, amount)
            Database.addMoney(target, amount)
            player.sendMessage("§a$amount para $target oyuncusuna gönderildi.")
            Core.Companion.instance.server.getPlayerExact(target)
                ?.sendMessage("§a$amount para ${player.name} oyuncusundan alındı.")
        }
    }
}