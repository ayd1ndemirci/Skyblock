package redfox.skyblock.form.web

import WebHistoryForm
import cn.nukkit.Player
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm

object WebForm {

    fun send(player: Player) {
        val form = SimpleForm("Web Paneli")
        form.addElement(ElementButton("Bakiyeni Gör"))
        form.addElement(ElementButton("Şifreni Gör"))
        form.addElement(ElementButton("Şifreni Sıfırla"))
        form.addElement(ElementButton("Satın Alma Geçmişi"))

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit

            when (response.buttonId()) {
                0 -> WebCreditForm.send(player)
                1 -> WebPasswordForm.send(player)
                2 -> WebPasswordResetForm.send(player)
                3 -> WebHistoryForm.send(player)
            }
        }
    }
}
