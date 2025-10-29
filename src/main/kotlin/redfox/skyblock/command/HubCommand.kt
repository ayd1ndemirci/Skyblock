package redfox.skyblock.command

import cn.nukkit.*
import cn.nukkit.command.*
import redfox.skyblock.transfer.Transfer

class HubCommand : Command(
    "hub",
    "Hub sunucusuna aktarma komutu"
) {
    override fun execute(sender: CommandSender?, label: String?, args: Array<out String?>?): Boolean {
        val p = sender as? Player ?: return false
        if (!p.isOp || args.isNullOrEmpty()) {
            p.sendMessage("§8» §aHuba yönlendiriliyorsunuz..")
            Transfer.goToHub(p, Transfer.HUB_IP, 19132)
        } else Server.getInstance().getPlayer(args[0])?.let {
            p.sendMessage("§8» §2${it.name} §aadlı oyuncu huba yönlendiriliyor..")
            Transfer.goToHub(it, Transfer.HUB_IP, 19132)
        } ?: p.sendMessage("§8» §4${args[0]} §r§cadlı oyuncu bulunamadı.")
        return true
    }
}