package redfox.skyblock.form.general

import cn.nukkit.Player
import cn.nukkit.form.element.ElementLabel
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm

object ListingForm {
    fun <T> send(
        player: Player, form: SimpleForm, list: List<T>, page: Int, pageCount: Int, noneFound: String,
        map: ((t: T, i: Int) -> String), open: ((t: T) -> Unit), openPage: ((page: Int) -> Unit),
        back: (() -> Unit)?
    ) {
        form.addElement(ElementLabel("§eSayfa: ${page + 1}/$pageCount"))

        if (list.isEmpty()) form.addElement(ElementButton(noneFound))
        list.forEachIndexed { index, faction -> form.addElement(ElementButton(map(faction, index))) }

        val opts = mutableListOf<Int>()
        if (page > 0) {
            form.addElement(ElementButton("Önceki Sayfa"))
            opts.add(page - 1)
        }
        if (page < pageCount - 1) {
            form.addElement(ElementButton("Sonraki Sayfa"))
            opts.add(page + 1)
        }
        if (back != null) {
            form.addElement(ElementButton("Geri", ButtonImage(ButtonImage.Type.PATH, "textures/ui/arrow_left.png")))
            opts.add(-1)
        }

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            val accept = response.buttonId()
            when (accept) {
                0 -> if (list.isEmpty()) {
                    back?.invoke()
                } else open(list[0])

                list.size + 1 -> if (opts[0] == -1 && back != null) back() else openPage(opts[0])
                list.size + 2 -> if (opts[1] == -1 && back != null) back() else openPage(opts[1])
                list.size + 3 -> if (opts[2] == -1 && back != null) back() else openPage(opts[2])
                else -> open(list[accept])
            }
        }
    }
}