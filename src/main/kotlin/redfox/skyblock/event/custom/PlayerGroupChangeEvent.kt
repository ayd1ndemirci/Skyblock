package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.event.HandlerList
import cn.nukkit.event.plugin.PluginEvent
import redfox.skyblock.Core
import redfox.skyblock.group.Group

@Suppress("unused")
class PlayerGroupChangeEvent(
    val player: Player,
    val newGroup: Group,
    val changer: CommandSender
) : PluginEvent(Core.instance) {

    companion object {
        val handlers = HandlerList()
    }
}
