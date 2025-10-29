package redfox.skyblock.utils

import cn.nukkit.Server
import com.google.gson.Gson
import redfox.skyblock.model.HitSettings
import java.io.File

object HitUtils {
    private var data: HitSettings = HitSettings()
    private val gson = Gson()

    fun load() {
        val file = File(Server.getInstance().dataPath, "serverConfig/hit.json")
        if (!file.exists()) {
            saveDefault(file)
        } else {
            val json = file.readText()
            data = try {
                gson.fromJson(json, HitSettings::class.java)
            } catch (ex: Exception) {
                saveDefault(file)
                HitSettings()
            }
        }
    }

    private fun saveDefault(file: File) {
        data = HitSettings(knockback = 0.28, attackCooldown = 5)
        save(file)
    }

    fun save(file: File? = null) {
        val targetFile = file ?: File(Server.getInstance().dataPath, "serverConfig/hit.json")
        targetFile.writeText(gson.toJson(data))
    }

    fun getKnockback(): Double = data.knockback
    fun setKnockback(value: Double) {
        data.knockback = value
        save()
    }

    fun getAttackCooldown(): Int = data.attackCooldown
    fun setAttackCooldown(value: Int) {
        data.attackCooldown = value
        save()
    }
}