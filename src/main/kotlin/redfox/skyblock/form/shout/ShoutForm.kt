package redfox.skyblock.form.shout

import cn.nukkit.Player
import cn.nukkit.form.window.ModalForm

object ShoutForm {

    fun send(player: Player) {
        ModalForm("Haykır Menüsü")
            .content(
                "Haykırma Kuralları" +
                        "\nMadde 1: Haykır sistemi ile herhangi bir klanı veya oyuncuları kışkırtmak veya toxiclemek maksadı ile atılan haykırılar yasaktır." +
                        "\nMadde 2: Başka bir sunucu hakkında reklam yapmak, bahsi geçilen sunucunun adresi, ismi veya konusu açılmak yasaktır." +
                        "\nMadde 3: Kurallarda bulunan tüm kurallar bu sistem içinde geçerlidir. Kural dışı bir haykırı yayınlarsanız ceza uygulanır." +
                        "\nCeza: Bu maddelerden herhangi birisini ihlal ederseniz hiç bir şekilde haykır sistemini kullanamazsınız. Bu sistemin affı olmayacaktır.".trimIndent()
            )
            .yesText("Haykır")
            .noText("Vazgeç")
            .onYes { p ->
                SendShoutForm.send(player)
            }
            .send(player)
    }
}