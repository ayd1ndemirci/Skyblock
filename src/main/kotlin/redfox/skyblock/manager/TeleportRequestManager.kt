package redfox.skyblock.manager

import cn.nukkit.Player
import cn.nukkit.scheduler.NukkitRunnable
import redfox.skyblock.Core

object TeleportRequestManager {
    private val reqs = mutableMapOf<String, TeleportRequest>()

    fun send(from: Player, to: Player) = internalSend(from, to, TeleportType.TO_TARGET)

    fun sendHere(from: Player, to: Player) = internalSend(from, to, TeleportType.TO_SENDER)

    private fun internalSend(from: Player, to: Player, type: TeleportType): Boolean {
        val key = from.name.lowercase()

        if (reqs[key]?.to?.name?.equals(to.name, true) == true) {
            from.msg("§cZaten bu oyuncuya istek gönderdin!")
            return false
        }

        if (key in reqs) {
            from.msg("§cZaten bekleyen bir isteğin var!")
            return false
        }

        reqs[key] = TeleportRequest(from, to, type).apply { timeout() }
        return true
    }

    fun accept(to: Player): Boolean {
        val req = resolve(to) ?: return false

        when (req.type) {
            TeleportType.TO_TARGET -> req.from.teleport(req.to.location)
            TeleportType.TO_SENDER -> req.to.teleport(req.from.location)
        }

        req.from.msg("§g${req.to.name} §6isteğini kabul etti.")
        req.to.msg("§g${req.from.name} §6isteğini kabul ettin.")
        return true
    }

    fun deny(to: Player): Boolean {
        val req = resolve(to) ?: return false

        req.from.msg("§4${req.to.name} §creddetti.")
        req.to.msg("§4${req.from.name} §cisteğini reddettin.")
        return true
    }

    private fun resolve(to: Player): TeleportRequest? {
        val req = reqs.values.find { it.to.name.equals(to.name, true) } ?: return null
        reqs.remove(req.from.name.lowercase())
        return req
    }

    private fun Player.msg(msg: String) = sendMessage("§8» $msg")

    data class TeleportRequest(val from: Player, val to: Player, val type: TeleportType) {
        fun timeout() {
            object : NukkitRunnable() {
                override fun run() {
                    val key = from.name.lowercase()
                    if (reqs.remove(key) != null) {
                        from.msg("§cIşınlanma isteğin zaman aşımına uğradı.")
                    }
                }
            }.runTaskLater(Core.instance, 20 * 30)
        }
    }

    enum class TeleportType { TO_SENDER, TO_TARGET }
}