package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.response.CustomResponse
import cn.nukkit.form.window.CustomForm
import cn.nukkit.utils.TextFormat
import redfox.skyblock.data.IslandDB
import redfox.skyblock.permission.Permission

object IslandAdvertisementForm {

    private const val MAX_MESSAGE_LENGTH = 64
    private const val SECONDS_IN_HOUR = 3600

    fun send(player: Player) {
        val form = createForm()
        form.onSubmit { _, response -> handleSubmit(player, response) }
        form.send(player)
    }

    private fun createForm(): CustomForm {
        return CustomForm("Ada Reklamı").apply {
            addElement(ElementLabel("§7- §6Kullanacağın kelimelere dikkat etmelisin!"))
            addElement(ElementInput("Mesajı Gir:", "§7Örn; Ada satılıktır!", ""))
        }
    }

    private fun handleSubmit(player: Player, response: CustomResponse?) {
        if (response == null) return

        val message = response.getInputResponse(1)?.trim() ?: run {
            player.sendMessage("§cLütfen bir mesaj giriniz!")
            return
        }

        if (!isValidMessage(player, message)) return

        val currentTime = System.currentTimeMillis() / 1000
        val playerSettings = IslandDB.getSettings(player.name)
        val nextAllowedTime = playerSettings.getLong("advertisementTime") ?: 0L

        if (currentTime < nextAllowedTime) {
            player.sendMessage("§cAda reklam süreniz dolmamış!")
            return
        }

        broadcastAdvertisement(player.name, message)
        val waitDuration = calculateWaitDuration(player, currentTime)

        if (!player.server.isOp(player.name)) {
            playerSettings["advertisementTime"] = waitDuration
            IslandDB.updatePlayerIslandSettings(player.name, playerSettings)
        }

        val waitHours = (waitDuration - currentTime) / SECONDS_IN_HOUR
        player.sendMessage("§aAda reklamı verildi! §7(Tekrar $waitHours saat sonra verebilirsin.)")
    }

    private fun isValidMessage(player: Player, message: String): Boolean {
        return when {
            message.isEmpty() -> {
                player.sendMessage("§cLütfen bir mesaj giriniz!")
                false
            }

            message.length > MAX_MESSAGE_LENGTH -> {
                player.sendMessage("§cMaksimum $MAX_MESSAGE_LENGTH §4( ${message.length} ) §ckarakter girebilirsiniz!")
                false
            }

            else -> true
        }
    }

    private fun broadcastAdvertisement(playerName: String, message: String) {
        val formattedMessage = TextFormat.clean(message)
        val broadcastMsg = """
            §7=-=-=-=-=-=-= §eAda Reklamı §7=-=-=-=-=-=-=

            §g$playerName: §6$formattedMessage

            §7=-=-=-=-=-=-= §eAda Reklamı §7=-=-=-=-=-=-=
        """.trimIndent()
        Server.getInstance().broadcastMessage(broadcastMsg)
    }

    private fun calculateWaitDuration(player: Player, currentTime: Long): Long {
        val waitHours = when {
            player.hasPermission(Permission.MVIP) -> 2
            player.hasPermission(Permission.VIP_PLUS) -> 5
            player.hasPermission(Permission.VIP) -> 10
            else -> 24
        }
        return currentTime + (waitHours * SECONDS_IN_HOUR)
    }
}
