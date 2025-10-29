package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.SetHitForm
import redfox.skyblock.permission.Permission

class SetHitCommand : Command(
    "sethit",
    "Hitleri ayarlar"
) {

    init {
        permission = Permission.SET_HIT_COMMAND
        permissionMessage = "§8» §cBu komutu kullanmak için yetkiniz yok!"
    }

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String?>?): Boolean {
        if (sender !is Player) return false
        if (!testPermission(sender) || !sender.isOp) {
            sender.sendMessage(this.permissionMessage)
            return false
        }
        SetHitForm.send(sender)
        return true
    }
}