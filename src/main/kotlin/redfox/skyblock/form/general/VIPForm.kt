package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.VIP
import redfox.skyblock.manager.VIPManager
import java.text.SimpleDateFormat
import java.util.*

object VIPForm {

    fun send(player: Player) {
        val playerDataList = VIP.getPlayerVIPs(player.name)
        val form = SimpleForm("VIP Süre Menüsü")

        if (playerDataList.isEmpty()) {
            form.addElement(ElementLabel("Hiç VIP kaydınız bulunamadı."))
        } else {
            for (playerData in playerDataList) {
                val vipType = playerData["vipType"] as? String ?: "Bilinmiyor"
                val timeLeft = (playerData["time"] as? Number)?.toLong() ?: 0L
                val formattedTimeLeft = VIPManager.formatTime(timeLeft)

                val endDate = Date(timeLeft * 1000)
                val formatter = SimpleDateFormat("dd MMMM EEEE HH:mm", Locale("tr", "TR"))
                val formattedDate = formatter.format(endDate)

                form.addElement(
                    ElementLabel(
                        "§bVIP Türü: §f$vipType\n" +
                                "§bKalan Süre: §f$formattedTimeLeft\n" +
                                "§bBitiş Tarihi: §f$formattedDate"
                    )
                )
            }
        }

        form.addElement(ElementButton("§cKapat"))
        form.send(player)
    }
}
