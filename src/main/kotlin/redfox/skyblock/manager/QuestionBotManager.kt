package redfox.skyblock.manager

import java.util.*

object QuestionBotManager {

    private val answers: Map<String, String> = mapOf(
        "Minecraft'ın kaç yapımcısı vardır?" to "2",
        "Lav kovası fırında kaç saniye boyunca yanar?" to "1000",
        "Minecraft'ın ilk ismi nedir?" to "cavegame",
        "Türkiye Cumhuriyeti'nin ilk Cumhurbaşkanı kimdir?" to "atatürk",
        "Minecraft'da toplam kaç adet yün vardır?" to "16",
        "Hangi taş elektrik iletemez?" to "kırıktaş",
        "Türkiye'nin başkenti neresidir?" to "ankara",
        "İstanbul hangi coğrafi bölgemizde yer almaktadır?" to "marmara",
        "Tarihte Türk adıyla kurulan ilk Türk devleti hangisidir?" to "göktürk",
        "Bir Gün Kaç Saniyedir?" to "86400",
        "Evrende en büyük uydusu olan gezegen hangisidir?" to "jüpiter",
        "Yerden yukarı block koyma sınırı kaçtır?" to "256",
        "İksir efektleri ne içince silinir?" to "süt",
        "Endonezya Devleti Hangi Kıtadadır?" to "asya",
        "Mustafa Kemal Atatürk hangi tarihte doğmuştur?" to "19 mayıs 1881",
        "Türkiye'de kaç tane coğrafi bölge bulunmaktadır?" to "7",
        "Minecraft'ta 1 gün kaç dakika sürer?" to "14",
        "Minecraft'ın düz dünyasında en sona kadar yürümek kaç saat sürer?" to "820",
        "Minecraft'ta eskiden en güçlü kılıç nedir?" to "altın kılıç",
        "Bir elektrik devresinde direnç hangi harfle gösterilir?" to "r",
        "16 çubuk ve 13 kömürden en fazla kaç meşale yapılabilir?" to "52",
        "1 metre kaç milimetredir?" to "1000",
        "Nota bloğunda kaç farklı nota bulunur?" to "24",
        "Kıbrıs Barış harekatı hangi tarihte gerçekleşmiştir?" to "1974",
        "Endermanlerin saldırmaması için ne takılır?" to "balkabağı",
        "Havadan aşağıya bırakılan bir TNT kaçıncı blokta patlar?" to "77",
        "Elytralar nerede bulunur?" to "end city",
        "Ses, en hızlı hangi ortamda yayılır?" to "katı",
        "Evrendeki en büyük yıldızın adı nedir?" to "uy scuti",
        "Duvara asılı bir haritanın sağı her zaman hangi yönü gösterir?" to "doğu",
        "Mercekler ışığın hangi özelliği kullanılarak yapılmıştır?" to "kırılma",
        "Bir gün kaç dakikadır?" to "1440",
        "RedFox kaç yılında kurulmuştur?" to "2024",
        "Kaç tane kızıltaş meşalesi 1 tane ışıktaşının gücüne sahiptir?" to "160",
        "Çanakkale Savaşı sırasında 215 kg'lık mermiyi tek başına kaldıran Türk askeri kimdir?" to "seyit onbaşı",
        "Bazı aylar 30, bazıları 31 çeker; kaç ayda 28 gün vardır?" to "12",
        "Gülü ile meşhur olan ilimiz hangisidir?" to "ısparta",
        "Nether portalı için en az kaç obsidyen gereklidir?" to "10",
        "Gece saat sekizde yatıyorum ve yatarken guguklu saatimi sabah dokuza kuruyorum kaç saat uyurum?" to "1",
        "Hangi ülkenin iki tane başkenti vardır?" to "güney afrika",
        "Hangi ilimizin deniz kıyısı vardır?" to "düzce",
        "Anıtkabir Ankara'nın hangi ilçesindedir?" to "çankaya",
        "Hz. Nuh gemisine her hayvandan kaçar adet aldı?" to "2"
    )

    private const val TITLE_UP = "§7<---------- §cRed§fFox §eSoru Botu §7---------->"

    private var question: String? = null
    private var answer: String? = null
    private var dinner: Int = 0
    private var generated: Boolean = false
    private var answered: Boolean = false

    fun generateQuestion() {
        val random = Random()
        dinner = random.nextInt(3501) + 500
        val isMath = random.nextBoolean()

        if (isMath) {
            val random1 = random.nextInt(4501)
            val random2 = random.nextInt(3001)
            val operations = arrayOf("+", "-", "x")
            val operation = operations[random.nextInt(operations.size)]

            val result = when (operation) {
                "+" -> random1 + random2
                "-" -> random1 - random2
                else -> random1 * random2
            }

            question = "$TITLE_UP\n\n§8* §g$random1 $operation $random2 §6işleminin sonucu nedir?\n" +
                    "§8* §c${if (random.nextBoolean()) "Bu soru biraz zor gibi gözüküyor!" else "Bu soruyu yapabilecek misin?"}\n\n$TITLE_UP"
            answer = result.toString()
        } else {
            val keys = answers.keys.toList()
            val selectedQuestion = keys[random.nextInt(keys.size)]
            question =
                "$TITLE_UP\n\n§8* §6$selectedQuestion\n§8* §cSorulara cevap verirken yazım kurallarına dikkat et!\n\n$TITLE_UP"
            answer = answers[selectedQuestion]
        }

        generated = true
        answered = false
    }

    fun getQuestion(): String? = question
    fun getAnswer(): String? = answer
    fun getDinner(): Int = dinner
    fun isGenerated(): Boolean = generated
    fun isAnswered(): Boolean = answered

    fun setAnswered(answered: Boolean) {
        this.answered = answered
    }

    fun reset() {
        question = null
        answer = null
        dinner = 0
        generated = false
        answered = false
    }
}
