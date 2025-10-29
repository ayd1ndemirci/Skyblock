package redfox.skyblock.utils

import cn.nukkit.Player
import redfox.skyblock.Core
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.Executors
import javax.imageio.ImageIO

object SkinUtils {

    private val executor = Executors.newSingleThreadExecutor()

    fun savePlayerHead(player: Player) {
        executor.execute {
            try {
                val skin = player.skin
                val data = skin.skinData.data

                val image = BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)

                var index = 0
                for (y in 0 until 64) {
                    for (x in 0 until 64) {
                        val i = index * 4
                        if (i + 3 >= data.size) continue
                        val r = data[i].toInt() and 0xFF
                        val g = data[i + 1].toInt() and 0xFF
                        val b = data[i + 2].toInt() and 0xFF
                        val a = data[i + 3].toInt() and 0xFF
                        val rgba = (a shl 24) or (r shl 16) or (g shl 8) or b
                        image.setRGB(x, y, rgba)
                        index++
                    }
                }

                val head = BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB)
                for (y in 8 until 16) {
                    for (x in 8 until 16) {
                        head.setRGB(x - 8, y - 8, image.getRGB(x, y))
                    }
                }

                for (y in 8 until 16) {
                    for (x in 40 until 48) {
                        val color = image.getRGB(x, y)
                        val alpha = (color shr 24) and 0xFF
                        if (alpha > 0) {
                            head.setRGB(x - 40, y - 8, color)
                        }
                    }
                }

                val finalImage = BufferedImage(330, 360, BufferedImage.TYPE_INT_ARGB)
                val g = finalImage.createGraphics()
                g.drawImage(head.getScaledInstance(330, 360, java.awt.Image.SCALE_SMOOTH), 0, 0, null)
                g.dispose()

                val folder = File(Core.instance.dataFolder, "heads")
                if (!folder.exists()) folder.mkdirs()
                val file = File(folder, "${player.name}.png")
                ImageIO.write(finalImage, "png", file)

            } catch (e: Exception) {
                Core.instance.logger.warning("Skin kaydederken hata oluştu: ${e.message}")
            }
        }
    }
}
