package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.Island
import redfox.skyblock.form.island.IslandForm
import redfox.skyblock.form.island.NoIslandForm
import redfox.skyblock.utils.Utils

class IslandCommand : Command(
    "ada",
    "Ada menüsü",
    "/ada"
) {

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§8» Bu komutu sadece oyuncular kullanabilir.")
            return true
        }

        val playerName = sender.name
        val isPartner = Island.isPartner(playerName)
        val hasIsland = Island.hasIsland(playerName)

        when {
            isPartner -> {
                IslandForm.send(sender, true)
                Utils.sound(sender, "note.harp")
            }

            hasIsland -> {
                IslandForm.send(sender, false)
                Utils.sound(sender, "note.harp")
            }

            else -> {
                NoIslandForm.send(sender)
                Utils.sound(sender, "note.harp")
            }
        }
        return true
    }
}
