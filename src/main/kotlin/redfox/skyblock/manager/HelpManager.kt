package redfox.skyblock.manager

object HelpManager {

    private val helpMap = mapOf(
        "Para Nasıl Kazanılır?" to "",
        "Klan Kurma" to "Bir klan kurmak için /klan kur <isim> yazabilirsiniz.",
        "Klan Savaşı" to "Klan savaşları için /klan savaş komutunu kullanın.",
        "Para Sistemi" to "Para kazanmak için görevleri tamamlayın veya satış yapın."
    )

    fun getAllHelps(): List<String> {
        return helpMap.keys.toList()
    }

    fun getHelp(title: String): String {
        return helpMap[title] ?: "Bu yardım başlığı bulunamadı."
    }
}
