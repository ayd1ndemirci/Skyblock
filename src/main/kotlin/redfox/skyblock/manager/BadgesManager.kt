package redfox.skyblock.manager

import redfox.skyblock.data.Database

object BadgesManager {
    data class Badge(val name: String, val info: String, val image: String)

    private val badges = listOf(
        Badge(
            "Yıl Dönümü",
            "Sunucumuza ilk girdiğin günün yıl dönüm rozeti\nAlmak için sunucumuza ilk giriğin gün ile girmen gerekiyor.",
            "textures/ui/gift_square.png"
        ),
        Badge("Milyoner", "10.000.000 ve üstü paraya sahip olanlara verilen rozet.", "textures/ui/MCoin.png"),
        Badge("Acımasız", "250 ve üstü öldürmeye sahip olanlara verilen rozet.", "textures/ui/strength_effect.png"),
        Badge(
            "Sadık",
            "60 gün ve üzeri login streaka sahip olanlara verilen rozet",
            "textures/ui/permissions_op_crown.png"
        ),
        Badge(
            "Başarım Ustası",
            "Bütün başarımları tamamlayan oyunculara verilen rozet",
            "textures/ui/village_hero_effect.png"
        ),
        Badge("Popüler", "20 ve üzeri arkadaşı olan oyunculara verilen rozet", "textures/ui/icon_staffpicks.png"),
        Badge("Rütbeci", "En üst rütbeye ulaşan oyunculara verilen rozet", "textures/ui/icon_best3.png"),
        Badge(
            "Zaman Ustası",
            "Oyun içi aktiflik süresi 30 gün olan oyunculara verilen rozet",
            "textures/ui/icon_timer.png"
        ),
        Badge(
            "Teslimat Ustası",
            "250.000 eşyayı teslimat ile satan oyunculara verilen rozet",
            "textures/ui/icon_carrot.png"
        ),
        Badge("Craft Ustası", "100.000 item craftlayan oyunculara verilen rozet", "textures/ui/icon_crafting.png"),
        Badge("Büyü Ustası", "1000 ve üzeri büyü basan oyunculara verilen rozet", "textures/items/book_enchanted.png"),
        //Badge("Dedektif", "50 den fazla oyuncu banlatana verilir.", "textures/ui/bad_omen_effect.png"),
    )

    fun allBadges() = badges

    fun playerHasBadge(playerName: String, badgeName: String): Boolean {
        val badges = Database.getBadges(playerName.lowercase())
        return badges.contains(badgeName)
    }

    fun getBadgeDescription(badgeName: String): String {
        return badges.find { it.name == badgeName }?.info ?: "Açıklama bulunamadı."
    }

    fun getBadgeImage(badgeName: String): String {
        return badges.find { it.name == badgeName }?.image ?: "textures/ui/default.png"
    }
}
