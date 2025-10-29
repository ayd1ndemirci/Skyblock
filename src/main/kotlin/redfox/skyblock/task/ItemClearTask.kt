package redfox.skyblock.task

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.entity.item.EntityItem
import cn.nukkit.entity.projectile.EntityArrow
import cn.nukkit.scheduler.Task
import redfox.skyblock.data.Database

class ItemClearTask : Task() {

    private var countdown = 300
    private val importantTimes = setOf(60, 45, 30, 15, 5, 4, 3, 2, 1)

    override fun onRun(currentTick: Int) {
        val server = Server.getInstance()

        if (countdown == 0) {
            server.levels.values.forEach { level ->
                level.entities.forEach { entity ->
                    if (entity is EntityItem || entity is EntityArrow) {
                        entity.close()
                    }
                }
            }

            server.onlinePlayers.values.forEach { player ->
                if (isNoticesEnabled(player)) {
                    player.sendActionBar("§eYerdeki tüm eşyalar temizlendi!")
                }
            }

            countdown = 600
            return
        }

        countdown--

        if (countdown in importantTimes) {
            val message = "§eYerdeki eşyalar §b$countdown §esaniye içinde silinecek!"
            server.onlinePlayers.values.forEach { player ->
                if (isNoticesEnabled(player)) {
                    player.sendActionBar(message)
                }
            }
        }
    }

    private fun isNoticesEnabled(player: Player): Boolean {
        return Database.isSettingEnabled(player.name, "notices")
    }
}
