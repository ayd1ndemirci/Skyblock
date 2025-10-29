package redfox.skyblock.task

import cn.nukkit.scheduler.Task
import cn.nukkit.Server
import redfox.skyblock.data.Redis

class PlayerCountSyncTask : Task() {
    override fun onRun(currentTick: Int) {
        val onlineCount = Server.getInstance().onlinePlayers.size
        Redis.setPlayerCount(onlineCount.toLong())
    }
}