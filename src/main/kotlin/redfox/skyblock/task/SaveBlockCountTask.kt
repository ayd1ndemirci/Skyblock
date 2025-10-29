package redfox.skyblock.task

import cn.nukkit.scheduler.Task
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB

class SaveBlockCountTask : Task() {
    override fun onRun(currentTick: Int) {
        for ((worldName, blockCounts) in Island.blockCountCache) {
            IslandDB.saveBlockCounts(worldName, blockCounts)
        }
    }
}
