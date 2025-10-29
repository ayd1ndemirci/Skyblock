package redfox.skyblock.task

import cn.nukkit.Server
import cn.nukkit.scheduler.Task
import redfox.skyblock.manager.QuestionBotManager
import redfox.skyblock.utils.Utils

class QuestionBotTask : Task() {
    private var tick = 16

    override fun onRun(currentTick: Int) {
        tick--

        if (tick == 15 && !QuestionBotManager.isGenerated()) {
            if (Server.getInstance().onlinePlayers.isEmpty()) {
                tick++
                return
            }
            QuestionBotManager.generateQuestion()
            Server.getInstance().broadcastMessage(QuestionBotManager.getQuestion() ?: "Soru bulunamadı.")
            Server.getInstance().levels.values.forEach { level ->
                level.players.values.forEach { onlinePlayer ->
                    Utils.sound(onlinePlayer, "item.trident.hit_ground")
                }
            }
        }

        if (tick == 0) {
            QuestionBotManager.reset()
            tick = 16
        }
    }
}
