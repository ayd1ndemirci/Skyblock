package redfox.skyblock.form.home

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.data.HomeDB
import redfox.skyblock.utils.HomeUtil

object SetHomeForm {

    fun send(player: Player) {
        val existingHomes = HomeDB.getHomes(player)
        val max = HomeUtil.getLimit(player)

        if (existingHomes.size >= max) {
            player.sendMessage("§cMaksimum ev limitine ulaştın! (${existingHomes.size}/$max)")
            return
        }

        val form = CustomForm("Ev Oluştur")
        form.addElement(ElementInput("\n§7Ev adı giriniz", "örnek: evim"))

        form.onSubmit { _, response ->
            val homeName = response.getInputResponse(0)?.trim() ?: ""

            if (homeName.isEmpty()) {
                player.sendMessage("§cEv adı boş olamaz!")
                return@onSubmit
            }

            if (existingHomes.containsKey(homeName)) {
                player.sendMessage("§cZaten '$homeName' adında bir evin var!")
                return@onSubmit
            }

            HomeDB.setHome(player, homeName)
            player.sendMessage("§a'$homeName' adlı ev başarıyla oluşturuldu!")
            HomeForm.send(player)
        }

        form.send(player)
    }
}
