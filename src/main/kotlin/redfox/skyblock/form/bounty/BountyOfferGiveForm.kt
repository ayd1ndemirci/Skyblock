package redfox.skyblock.form.bounty

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.element.custom.ElementToggle
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.Bounty
import redfox.skyblock.data.Database

object BountyOfferGiveForm {

    fun send(player: Player, targetGot: String? = null) {
        val form = CustomForm("Kelle Avcısı - Teklif Ver")
        form.addElement(ElementLabel("§eParan: ${Database.getCredi(player.name)}"))

        val hasTargetInput = (targetGot == null)
        if (hasTargetInput) {
            form.addElement(ElementInput("§eHedef oyuncunun adını girin:", "Örnek: NotPantherr")) // index 1
        }
        form.addElement(ElementInput("§aÖdül miktarını girin:", "Örnek: 1000")) // index 1 or 2
        form.addElement(ElementToggle("§eBu paranın şuan benden alınacağını onaylıyorum.", false)) // index 2 or 3

        // Önce onSubmit tanımla
        form.onSubmit { _, response ->
            if (response == null) {
                player.sendMessage("§cForm yanıtı alınamadı.")
                return@onSubmit
            }

            try {
                val target = if (hasTargetInput) {
                    response.getInputResponse(1)?.trim() ?: ""
                } else {
                    targetGot ?: ""
                }

                val rewardInput = response.getInputResponse(if (hasTargetInput) 2 else 1) ?: ""
                val reward = rewardInput.toIntOrNull()

                val accepted = response.getToggleResponse(if (hasTargetInput) 3 else 2)

                // Null ve geçersiz kontrolü
                if (target.isEmpty()) {
                    player.sendMessage("§cHedef oyuncu adı boş olamaz!")
                    return@onSubmit
                }

                if (reward == null || reward <= 0) {
                    player.sendMessage("§cGeçerli bir ödül miktarı girin!")
                    return@onSubmit
                }

                if (!accepted) {
                    player.sendMessage("§cOnay vermeden teklif oluşturulamaz!")
                    return@onSubmit
                }

                if (Database.getMoney(player.name) < reward) {
                    player.sendMessage("§cYeterli paranız yok!")
                    return@onSubmit
                }

                // Artık reward kesin Int, nullable değil
                Bounty.set(player.name, target, reward)
                Database.removeMoney(player.name, reward)

                player.sendMessage("§aTeklif başarıyla verildi ve §e$reward RP§a hesabınızdan düşüldü.")
            } catch (ex: Exception) {
                player.sendMessage("§cBir hata oluştu: ${ex.message}")
            }
        }

        form.send(player)
    }
}
