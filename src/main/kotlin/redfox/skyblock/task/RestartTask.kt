package redfox.skyblock.task

import cn.nukkit.Server
import cn.nukkit.scheduler.NukkitRunnable
import redfox.skyblock.Core

class RestartTask(
    private var seconds: Int
) {

    private var runnable: NukkitRunnable? = null

    fun start() {
        runnable = object : NukkitRunnable() {
            override fun run() {
                sendRestartWarningsIfNeeded(seconds)
                if (seconds-- <= 1) {
                    Server.getInstance().broadcastMessage("§4§SUNUCU YENIDEN BASLATILIYOR!")
                    Server.getInstance().shutdown()
                    cancel()
                }
            }
        }.apply { runTaskTimer(Core.instance, 20, 20) }
    }

    fun stop() {
        runnable?.cancel()
    }

    fun getRemainingSeconds(): Int = seconds

    private fun sendRestartWarningsIfNeeded(sec: Int) {
        val hours = sec / 3600
        when {
            sec % 3600 == 0 && hours in 1..3 -> broadcast("§eSunucunun yeniden başlatılmasına §l§b${hours} §r§esaat kaldı!")
            sec in listOf(
                30,
                15,
                10,
                5,
                4,
                3,
                2,
                1
            ) -> broadcast("§eSunucunun yeniden başlatılmasına §l§b${sec} §r§esaniye kaldı!")
        }
    }

    private fun broadcast(msg: String) {
        Server.getInstance().broadcastMessage(msg)
    }

    fun setSeconds(newSeconds: Int) {
        this.seconds = newSeconds
    }

}