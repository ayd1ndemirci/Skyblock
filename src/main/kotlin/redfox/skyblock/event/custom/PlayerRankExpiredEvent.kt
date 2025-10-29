package redfox.skyblock.event.custom

import cn.nukkit.Player
import cn.nukkit.event.plugin.PluginEvent
import redfox.skyblock.Core

class PlayerRankExpiredEvent(val player: Player) : PluginEvent(Core.instance)
