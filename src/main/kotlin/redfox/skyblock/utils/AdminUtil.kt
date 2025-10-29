package redfox.skyblock.utils

object AdminUtil {

    const val BAN_TITLE = "§7<---------- §6RF BAN SYSTEM §7----------->"

    fun parseDuration(durationStr: String): Long {
        val time = durationStr.dropLast(1).toLongOrNull() ?: return -1
        return when (durationStr.last().lowercaseChar()) {
            'd' -> time * 60 * 1000        // dakika
            's' -> time * 60 * 60 * 1000   // saat
            'g' -> time * 24 * 60 * 60 * 1000 // gün
            'a' -> time * 30 * 24 * 60 * 60 * 1000 // ay
            else -> -1
        }
    }

}