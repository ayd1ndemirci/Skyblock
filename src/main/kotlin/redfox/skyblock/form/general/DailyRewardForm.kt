package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.manager.RewardManager
import redfox.skyblock.service.RewardService
import redfox.skyblock.utils.Utils

object DailyRewardForm {

    fun send(player: Player) {
        val rewardEntry = RewardService.getTodaysReward(player) ?: run {
            player.sendMessage("§cBugün verilecek ödül bulunamadı.")
            return
        }

        val allRewards = RewardManager.getAllRewards()
        val rewardIndex = allRewards.indexOfFirst { it == rewardEntry }
        if (rewardIndex == -1) {
            player.sendMessage("§cÖdül listesinde hata oluştu.")
            return
        }

        val form = SimpleForm("Günlük Ödül")
        form.addElement(ElementLabel("§4§oNot: §cEğer menüyü yanlışlıkla kapatırsan §7/gunlukodul §ckomutunu kullanabilirsin."))
        val button = if (rewardEntry.imagePath != null) {
            ElementButton(rewardEntry.name, ButtonImage(ButtonImage.Type.PATH, rewardEntry.imagePath))
        } else {
            ElementButton(rewardEntry.name)
        }
        form.addElement(button)

        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit
            if (!RewardService.canClaim(player)) {
                player.sendMessage("§cZaten bugün ödülünü almışsın.")
                return@onSubmit
            }
            rewardEntry.reward.giveTo(player)
            RewardService.markClaimed(player, rewardIndex)  // Burada doğru index gönderiliyor
            player.sendMessage("§aGünlük ödülün verildi: §f${rewardEntry.name}")
            Utils.sound(player, "note.harp")
        }
    }

}
