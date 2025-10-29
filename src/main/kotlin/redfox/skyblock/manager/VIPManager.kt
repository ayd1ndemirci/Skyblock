package redfox.skyblock.manager

import java.util.concurrent.TimeUnit

object VIPManager {
    const val PERMISSION_MESSAGE = "§8» §cBu komutu kullanma yetkiniz yok!"

    fun getVIPTypes(): List<String> = listOf("VIP", "VIPPlus", "MVIP")

    fun formatTime(unixTime: Long): String {
        val currentTime = System.currentTimeMillis() / 1000
        val remaining = unixTime - currentTime

        if (remaining <= 0) return "Süre doldu!"

        val days = TimeUnit.SECONDS.toDays(remaining)
        val hours = TimeUnit.SECONDS.toHours(remaining) % 24
        val minutes = TimeUnit.SECONDS.toMinutes(remaining) % 60
        val seconds = remaining % 60

        return "$days gün, $hours saat, $minutes dakika, $seconds saniye"
    }
}
