package redfox.skyblock.manager

import redfox.skyblock.config.WLConfig

object WhitelistManager {
    val isActive get() = WLConfig.isActive
    fun setActive(status: Boolean) = WLConfig.setActive(status)
    val players get() = WLConfig.players
    fun addPlayer(name: String) = WLConfig.addPlayer(name)
    fun removePlayer(name: String) = WLConfig.removePlayer(name)
    fun isPlayerWhitelisted(name: String) = WLConfig.isPlayerWhitelisted(name)
    fun clear() = WLConfig.clearPlayers()
}