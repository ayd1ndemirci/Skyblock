package redfox.skyblock.utils

import cn.nukkit.Server
import cn.nukkit.network.protocol.PlaySoundPacket

object ShoutUtil {
    private var globalCooldownEnd: Long = 0L
    private const val COOLDOWN_TIME = 60 * 1000L // 1 dakika ms

    fun isOnCooldown(): Boolean {
        val now = System.currentTimeMillis()
        return now < globalCooldownEnd
    }

    fun getRemainingCooldown(): Long {
        val now = System.currentTimeMillis()
        return if (now < globalCooldownEnd) (globalCooldownEnd - now) / 1000 else 0
    }

    fun startCooldown() {
        globalCooldownEnd = System.currentTimeMillis() + COOLDOWN_TIME
    }

    fun playSoundToAll(soundName: String, pitch: Float = 1f, volume: Float = 1f) {
        val players = Server.getInstance().onlinePlayers.values
        for (player in players) {
            val psp = PlaySoundPacket().apply {
                name = soundName
                this.pitch = pitch
                this.volume = volume
                x = player.location.x.toInt()
                y = player.location.y.toInt()
                z = player.location.z.toInt()
            }
            player.session.sendPacket(psp)
        }
    }
}
