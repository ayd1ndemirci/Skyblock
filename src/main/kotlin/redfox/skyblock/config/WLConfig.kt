package redfox.skyblock.config

import cn.nukkit.Server
import com.google.gson.Gson
import redfox.skyblock.model.WLSettings
import java.io.File

object WLConfig {

    private val gson = Gson()
    private val configDir = File(Server.getInstance().dataPath, "serverConfig")
    private val file = File(configDir, "whitelist.json")

    private var settings = WLSettings()

    fun load() {
        if (!configDir.exists()) configDir.mkdirs()
        if (!file.exists()) saveDefault()
        settings = try {
            gson.fromJson(file.readText(), WLSettings::class.java)
        } catch (e: Exception) {
            saveDefault()
            WLSettings()
        }
    }

    fun save() = file.writeText(gson.toJson(settings))

    private fun saveDefault() {
        settings = WLSettings(active = false, players = mutableListOf())
        save()
    }

    val isActive get() = settings.active
    fun setActive(status: Boolean) {
        settings.active = status; save()
    }

    val players get() = settings.players
    fun addPlayer(name: String) = name.lowercase().let {
        if (it !in settings.players) settings.players.add(it).also { save() } else false
    }

    fun removePlayer(name: String) = settings.players.remove(name.lowercase()).also {
        if (it) save()
    }

    fun isPlayerWhitelisted(name: String) = name.lowercase() in settings.players
    fun clearPlayers() {
        settings.players.clear(); save()
    }
}
