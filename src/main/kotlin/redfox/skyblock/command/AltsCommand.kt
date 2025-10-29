package redfox.skyblock.command

import cn.nukkit.Server
import cn.nukkit.command.Command
import cn.nukkit.command.CommandSender
import org.bson.Document
import redfox.skyblock.data.Tracker

class AltsCommand : Command("alts") {

    init {
        this.description = "Aynı cihaz veya IP'den giriş yapan oyuncuları listeler"
        this.usage = "/alts [device|ip]"
    }

    override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
        if (!sender.isOp) return false

        val mode = if (args.isNotEmpty()) args[0].lowercase() else "device"
        val groupBy = when (mode) {
            "ip" -> "\$ip"
            else -> "\$deviceId"
        }

        Server.getInstance().scheduler.scheduleTask(
            redfox.skyblock.Core.instance,
            object : cn.nukkit.scheduler.AsyncTask() {
                override fun onRun() {
                    val collection = Tracker.collection

                    val pipeline = listOf(
                        Document(
                            "\$group", Document()
                                .append("_id", groupBy)
                                .append("names", Document("\$addToSet", "\$name"))
                                .append("count", Document("\$sum", 1))
                        ),
                        Document("\$match", Document("count", Document("\$gt", 1)))
                    )

                    val resultLines = mutableListOf<String>()

                    for (doc in collection.aggregate(pipeline)) {
                        val id = doc.getString("_id") ?: continue
                        val names = doc.getList("names", String::class.java)?.distinct() ?: continue

                        if (names.size > 1) {
                            resultLines.add("§7[$id] §e" + names.joinToString(", "))
                        }
                    }

                    Server.getInstance().scheduler.scheduleTask(redfox.skyblock.Core.instance, Runnable {
                        if (resultLines.isEmpty()) {
                            sender.sendMessage("§aHiç alt hesap tespit edilmedi.")
                        } else {
                            val typeText = if (groupBy == "\$ip") "IP" else "cihaz"
                            sender.sendMessage("§cAynı $typeText üzerinden bağlanan oyuncular:")
                            resultLines.forEach { sender.sendMessage(it) }
                        }
                    })
                }
            })

        return true
    }
}
