package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.custom.ElementInput
import cn.nukkit.form.window.CustomForm
import redfox.skyblock.utils.HitUtils

object SetHitForm {

    fun send(player: Player) {
        val form = CustomForm("Hit Form")

        val knockback = HitUtils.getKnockback().toString()
        val attackCooldown = HitUtils.getAttackCooldown().toString()

        form.addElement(ElementInput("Knockback", "§7Örn; 0.4351", knockback))
        form.addElement(ElementInput("Attack Cooldown", "§7Örn; 1", attackCooldown))

        form.send(player)

        form.onSubmit { _, response ->
            if (response == null) return@onSubmit

            val knockbackInput = response.getInputResponse(0)
            val attackCooldownInput = response.getInputResponse(1)

            // Try to parse inputs safely
            val kb = knockbackInput.toDoubleOrNull()
            val cd = attackCooldownInput.toIntOrNull()

            if (kb == null || cd == null) {
                player.sendMessage("§cLütfen geçerli bir sayı girin.")
                return@onSubmit
            }

            HitUtils.setKnockback(kb)
            HitUtils.setAttackCooldown(cd)

            player.sendMessage("§aHit ayarları başarıyla güncellendi.")
        }
    }
}
