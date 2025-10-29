package redfox.skyblock.form.credi

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.data.Database
import redfox.skyblock.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

object CrediHistoryForm {

    fun send(player: Player) {
        val form = SimpleForm("Kredi Geçmişin")
        val records = Database.getLastCrediRecords(player.name)
        if (records.isEmpty()) {
            form.addElement(ElementLabel("§7Hiç kredi işlemi yapılmamış."))
        } else {
            val builder = StringBuilder()
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("Europe/Istanbul")

            for (record in records) {
                val date = sdf.format(Date(record.timestamp * 1000))
                builder.append("§e${record.action} §f${record.amount} kredi (§7$date§f)\n")
            }
            form.addElement(ElementLabel(builder.toString()))
            form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))
        }

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> {
                    CrediForm.send(player)
                    Utils.sound(player, "note.harp")
                }
            }
        }
    }
}