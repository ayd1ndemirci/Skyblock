package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.utils.AutoSell

object AutoSellForm {

    private val ores = listOf("lapis", "taş", "kum")

    fun send(player: Player) {
        val current = AutoSell.getAll(player.name)

        val form = CustomForm("Oto Satış Ayarları")
            .onSubmit { p, response ->
                for ((index, ore) in ores.withIndex()) {
                    val value = response.getToggleResponse(index)
                    AutoSell.set(p.name, ore, value)
                }
                p.sendMessage("§aOto satış ayarların kaydedildi.")
            }

        for (ore in ores) {
            val enabled = current[ore] ?: false
            val oreName = ore.replaceFirstChar { it.uppercaseChar() }
            val statusText = if (enabled) "§aaçık" else "§ckapalı"
            form.addElement(ElementToggle("$oreName $statusText", enabled))
        }

        form.send(player)
    }
}