package redfox.skyblock.command

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.command.CommandSender
import cn.nukkit.command.data.CommandEnum
import cn.nukkit.command.data.CommandParameter
import cn.nukkit.command.defaults.VanillaCommand
import cn.nukkit.command.tree.ParamList
import cn.nukkit.command.utils.CommandLogger
import redfox.skyblock.config.WLConfig
import redfox.skyblock.manager.WhitelistManager
import redfox.skyblock.permission.Permission

class WhitelistCommand : VanillaCommand(
    "whitelist", "Whitelist komutu",
    "/whitelist <ac|kapat|ekle|cikar|liste|bilgi|reload>", arrayOf("bakim", "wl")
) {
    init {
        permission = Permission.WHITELIST_COMMAND
        permissionMessage = "§8» §cBu komut yönetici komutudur."

        this.commandParameters.clear()

        this.commandParameters["ekle"] = arrayOf(
            CommandParameter.newEnum("ekle", arrayOf("ekle")),
            CommandParameter.newEnum(
                "oyuncu", false,
                CommandEnum("onlinePlayers") {
                    Server.getInstance().onlinePlayers.values.map { it.name.lowercase() }
                }
            )
        )


        this.commandParameters["cikar"] = arrayOf(
            CommandParameter.newEnum("cikar", arrayOf("cikar")),
            CommandParameter.newEnum(
                "oyuncu", false,
                CommandEnum("whitelistPlayers") { WhitelistManager.players.toList() }
            )
        )

        for (cmd in arrayOf("liste", "bilgi", "reload", "ac", "kapat")) {
            this.commandParameters[cmd] = arrayOf(
                CommandParameter.newEnum(cmd, arrayOf(cmd))
            )
        }

        this.enableParamTree()
    }

    override fun execute(
        sender: CommandSender,
        commandLabel: String,
        result: Map.Entry<String, ParamList>,
        log: CommandLogger
    ): Int {
        if (!testPermission(sender) || !sender.isOp) {
            sender.sendMessage(permissionMessage)
            return 0
        }
        val args = result.value
        return when (result.key) {
            "liste" -> {
                val list = WhitelistManager.players
                if (list.isEmpty()) {
                    log.addMessage("§cWhitelist boş.")
                } else {
                    log.addMessage("§bOyuncular (${list.size}): ${list.joinToString(", ")}")
                }
                log.output()
                1
            }

            "ac" -> {
                WhitelistManager.setActive(true)
                log.addSuccess("§8» §aWhitelist açıldı.")
                Server.getInstance().onlinePlayers.values.forEach { player ->
                    if (player is Player && !player.isOp && !WhitelistManager.players.contains(player.name.lowercase())) {
                        player.close("§eSunucu bakıma alındı\n§3Discord: §bredfoxmc.com/discord")
                    }
                }
                log.output()
                1
            }

            "kapat" -> {
                WhitelistManager.setActive(false)
                log.addSuccess("§8» §eWhitelist kapatıldı.").output()
                1
            }

            "ekle" -> {
                if (!args.hasResult(1)) {
                    log.addMessage("§8» §cLütfen oyuncu ismi yazın").output()
                    return 0
                }

                val rawName = args.getResult<Any>(1)
                val name = when (rawName) {
                    is String -> rawName
                    is List<*> -> rawName.firstOrNull()?.toString() ?: ""
                    else -> ""
                }.lowercase()

                if (name.isBlank()) {
                    log.addMessage("§8» §cLütfen oyuncu ismi yazınız.").output()
                    return 0
                }

                if (!WhitelistManager.players.contains(name)) {
                    WhitelistManager.players.add(name)
                    WLConfig.save()
                    log.addSuccess("§8» §2$name whitelist'e eklendi.").output()
                } else {
                    log.addSuccess("§8» §2$name zaten whitelist'te.").output()
                }
                1
            }


            "cikar" -> {
                if (!args.hasResult(1)) {
                    log.addMessage("§8» §cLütfen oyuncu ismi yazınız.").output()
                    return 0
                }

                val rawName = args.getResult<Any>(1)
                val name = when (rawName) {
                    is String -> rawName
                    is List<*> -> rawName.firstOrNull()?.toString() ?: ""
                    else -> ""
                }.lowercase()

                if (name.isBlank()) {
                    log.addMessage("§8» §cLütfen oyuncu ismi yazınız.").output()
                    return 0
                }

                val removed = WhitelistManager.players.remove(name)
                if (removed) {
                    WLConfig.save()
                    log.addSuccess("§8» §2$name whitelist'ten çıkarıldı.").output()
                } else {
                    log.addSuccess("§8» §2$name whitelist'te bulunamadı.").output()
                }

                1
            }


            "reload" -> {
                WLConfig.load()
                log.addSuccess("§8» §aWhitelist dosyası yeniden yüklendi.")
                if (WhitelistManager.isActive) {
                    Server.getInstance().onlinePlayers.values.forEach { player ->
                        if (player is Player && !player.isOp && !WhitelistManager.players.contains(player.name.lowercase())) {
                            player.close("§eSunucu bakıma alındı\n§3Discord: §bredfoxmc.com/discord")
                        }
                    }
                }
                log.output()
                1
            }

            "bilgi" -> {
                log.addMessage("§aDurum: ${if (WhitelistManager.isActive) "§2Açık" else "§cKapalı"}")
                log.addMessage("§aOyuncu sayısı: §2${WhitelistManager.players.size}")
                log.output()
                1
            }

            else -> {
                log.addMessage("§cBilinmeyen komut. /whitelist <ac|kapat|ekle|cikar|liste|bilgi|reload>").output()
                0
            }
        }
    }

}