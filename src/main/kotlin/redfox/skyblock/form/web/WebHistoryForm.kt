import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.WebManager
import java.text.SimpleDateFormat
import java.util.*

object WebHistoryForm {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    fun send(player: Player) {
        val purchases = WebManager.getPurchases(player.name)
        val form = SimpleForm("Satın Alma Geçmişin")

        if (purchases.isEmpty()) {
            form.addElement(ElementLabel("§cHiçbir şey satın almamışsın :("))
        } else {
            val contentBuilder = StringBuilder()
            for (purchase in purchases) {
                val item = purchase.getString("item") ?: "Bilinmeyen"
                val amount = purchase.getInteger("amount") ?: 1
                val timestamp = purchase.getLong("timestamp") ?: System.currentTimeMillis()
                val dateStr = dateFormat.format(Date(timestamp))
                contentBuilder.append("§e${amount}x §f$item §7($dateStr)\n")
            }
            form.addElement(ElementLabel(contentBuilder.toString()))
        }

        form.send(player)
    }
}
