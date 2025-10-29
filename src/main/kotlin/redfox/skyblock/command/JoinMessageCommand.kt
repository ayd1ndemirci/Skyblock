package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import redfox.skyblock.form.general.SetJoinMessageForm
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.Utils


class JoinMessageCommand : Command(
    "joinmessage",
    "Giriş mesajını kişiselleştirmek istemez misin?",
    "/joinmessage",
    arrayOf("girismesaji")
) {

    init {
        permission = Permission.SET_JOIN_MESSAGE
        permissionMessage = "§8» §cBu komutu §4§lVIP §r§cve üzerleri kullanabilir. VIP satın almak için /vipbilgi."
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        if (!testPermission(sender) || !sender.isOp) {
            sender.sendMessage(permissionMessage)
            return false
        }
        SetJoinMessageForm.send(sender)
        Utils.sound(sender, "note.harp")
        return true
    }
}