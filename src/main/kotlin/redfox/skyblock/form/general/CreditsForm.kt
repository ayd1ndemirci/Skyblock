package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.window.SimpleForm

object CreditsForm {

    fun send(player: Player) {
        val form = SimpleForm("Yapımcılar")
        form.addElement(ElementLabel("§cRed§fFox §dNetwork\n\n§r§3§lAna Proje Üretimi/Yönetimi\n§r§f- Aydın §e\"ayd1ndemirci\" §r§fDemirci\n- §fBurak §e\"seri4lize\" §fRana\n\n\n§d§lÖzel Teşekkürler\n"))
        form.addElement(ElementButton("Kapat"))
        form.send(player)
    }
}