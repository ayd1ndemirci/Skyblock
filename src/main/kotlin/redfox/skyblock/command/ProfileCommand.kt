package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.ProfileForm
import redfox.skyblock.utils.Utils

class ProfileCommand : Command("profil", "Oyuncunun profilini açar", "/profil [oyuncu]") {
    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("§cBu komutu sadece oyuncular kullanabilir.")
            return false
        }

        val target: String = if (args.isEmpty()) sender.name else args[0]

        if (!sender.server.hasOfflinePlayerData(target)) {
            sender.sendMessage("§8» §cBöyle bir oyuncu bulunamadı.")
            return false
        }

        ProfileForm.send(sender, target, null)
        Utils.sound(sender, "note.harp")
        return true
    }
}
