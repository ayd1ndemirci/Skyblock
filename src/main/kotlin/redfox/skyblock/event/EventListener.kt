package redfox.skyblock.event

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.BlockID
import cn.nukkit.entity.projectile.EntityArrow
import cn.nukkit.entity.projectile.EntityThrownTrident
import cn.nukkit.event.EventHandler
import cn.nukkit.event.EventPriority
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.entity.EntityExplodeEvent
import cn.nukkit.event.inventory.InventoryPickupItemEvent
import cn.nukkit.event.player.*
import cn.nukkit.event.server.DataPacketSendEvent
import cn.nukkit.level.Sound
import cn.nukkit.network.protocol.ModalFormRequestPacket
import cn.nukkit.network.protocol.types.PlayerAbility
import cn.nukkit.network.protocol.types.SpawnPointType
import cn.nukkit.scheduler.NukkitRunnable
import cn.nukkit.scheduler.Task
import com.mongodb.client.model.Filters
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair
import redfox.skyblock.Core
import redfox.skyblock.data.Database
import redfox.skyblock.data.MongoDB
import redfox.skyblock.data.Mute
import redfox.skyblock.event.custom.PlayerGroupChangeEvent
import redfox.skyblock.event.custom.PlayerRankExpiredEvent
import redfox.skyblock.form.general.DailyRewardForm
import redfox.skyblock.group.GroupManager
import redfox.skyblock.manager.CombatManager
import redfox.skyblock.manager.PermissionManager
import redfox.skyblock.manager.QuestionBotManager
import redfox.skyblock.manager.WhitelistManager
import redfox.skyblock.permission.Permission
import redfox.skyblock.scoreboard.player.ScorePlayerManager
import redfox.skyblock.service.RewardService
import redfox.skyblock.utils.HitUtils
import redfox.skyblock.utils.InventoryUtils
import redfox.skyblock.utils.SkinUtils
import redfox.skyblock.utils.Utils
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.random.nextInt

class EventListener : Listener {

    private val lastWarningTime: MutableMap<String, MutableMap<String, Long>> = mutableMapOf()
    private val lastChatTime = mutableMapOf<String, Long>()
    private val spamCount = mutableMapOf<String, Int>()
    private val lastCommandTime = mutableMapOf<String, Long>()
    val dbThreadPool = Executors.newFixedThreadPool(4)


    private fun sendWarning(player: Player, message: String) {
        val currentTime = System.currentTimeMillis()
        val warnings = lastWarningTime[player.name]
        if (warnings == null) return
        val lastTime = warnings.getOrDefault(message, 0L)

        if (currentTime - lastTime >= 3000) {
            player.sendMessage(message)
            warnings[message] = currentTime
        }
    }

    private fun sendToast(player: Player, title: String, content: String) {
        val currentTime = System.currentTimeMillis()
        val warnings = lastWarningTime[player.name]
        if (warnings == null) return
        val lastTime = warnings.getOrDefault(title, 0L)

        if (currentTime - lastTime >= 10000) {
            player.sendToast(title, content)
            warnings[title] = currentTime
        }
    }


    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.teleport(Server.getInstance().defaultLevel.safeSpawn)
        ScorePlayerManager.addPlayer(event.player)
        val player = event.player
        //Redis.setPlayerServer(player.name, "Skyblock")
        //Redis.incrementPlayerCount()
        PermissionManager.handlePermissions(player)
        player.nameTag = GroupManager.getPlayerGroup(player).nameTagFormat.replace("%nickname%", player.name)
        SkinUtils.savePlayerHead(player)
        if (RewardService.canClaim(player)) DailyRewardForm.send(player)
        val inventory = player.inventory.contents
        for (item in inventory.values) {
            InventoryUtils.processItem(player.name, item)
        }
        lastWarningTime[player.name] = mutableMapOf()
        Utils.joinTimes[player.name] = System.currentTimeMillis()

        if (player.hasPermission(Permission.SET_JOIN_MESSAGE)) {
            val joinMessage = Database.getJoinMessage(player.name) ?: "oyuna katıldı!"
            val msg = "§8» §r${player.name} §a$joinMessage"
            event.setJoinMessage(msg)

            Server.getInstance().onlinePlayers.values.forEach { online ->
                online.level.addSound(online.position, Sound.RANDOM_LEVELUP)
            }
        } else event.setJoinMessage("")

        Utils.updateAutoInvCache(player.name, Database.isSettingEnabled(player.name, "autoinv"))

        dbThreadPool.submit {
            val playerNameLower = player.name.lowercase()

            Database.createIfNotExist(playerNameLower)

            val vipCollection = MongoDB.db.getCollection("vips")
            val vipDoc = vipCollection.find(Filters.eq("player", playerNameLower)).first()

            if (vipDoc != null) {
                val currentTime = when (val timeVal = vipDoc["time"]) {
                    is Long -> timeVal
                    is Int -> timeVal.toLong()
                    else -> 0L
                }
                val vipType = vipDoc.getString("vipType")

                if (currentTime <= System.currentTimeMillis() / 1000) {
                    vipCollection.deleteOne(Filters.eq("player", playerNameLower))

                    Server.getInstance().scheduler.scheduleTask(Core.instance, Runnable {
                        player.sendMessage("§c${vipType} üyeliğinizin süresi bitmiştir!")
                    })
                }
            }

            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            val playerData = Database.get(playerNameLower)

            val lastJoinStr = playerData?.getString("lastJoin").orEmpty()
            val lastJoinDate = runCatching {
                LocalDateTime.parse(lastJoinStr, formatter).toLocalDate()
            }.getOrNull()

            val yesterday = now.minusDays(1).toLocalDate()

            when {
                lastJoinDate == yesterday -> {
                    val oldStreak = playerData?.getInteger("loginStreak") ?: 1
                    val newStreak = oldStreak + 1
                    val reward = 5000 + (newStreak - 1) * 200

                    Database.set(playerNameLower, "loginStreak", newStreak)
                    Database.addMoney(playerNameLower, reward)
                    player.sendMessage("§8[§a§lLR§r§8] §r§fŞu anda $newStreak günlük bir giriş serisindesin. Para ödülü: $reward TL")
                }

                lastJoinDate == null || lastJoinDate.isBefore(yesterday) -> {
                    Database.set(playerNameLower, "loginStreak", 1)
                    if (lastJoinDate != null) {
                        player.sendMessage("§8[§c§lLR§r§8] §cGiriş serinizi devam ettiremediniz, yeniden başlıyorsunuz.")
                    }
                }
            }

            Database.set(playerNameLower, "lastJoin", now.format(formatter))

        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        //Redis.deletePlayerServer(player.name)
        //Redis.decrementPlayerCount()
        ScorePlayerManager.removePlayer(event.player)
        lastWarningTime.remove(player.name)
        val attachment = PermissionManager.permissions[event.player.uniqueId] ?: return
        event.player.removeAttachment(attachment)

        val joinTime = Utils.joinTimes.remove(player.name) ?: return
        val sessionPlayTimeSec = ((System.currentTimeMillis() - joinTime) / 1000).toInt()

        val currentPlayTime = Database.get(player.name)?.getInteger("playTime") ?: 0
        Database.set(player.name, "playTime", currentPlayTime + sessionPlayTimeSec)

        event.setQuitMessage("")

        if (CombatManager.isInCombat(player)) {
            player.health = 0f
            CombatManager.removePlayer(player.name)
        } else {
            CombatManager.removePlayer(player.name)
        }
        Utils.invalidateAutoInvCache(player.name)
    }


    @EventHandler
    fun onChat(event: PlayerChatEvent) {
        val player: Player = event.player
        val message: String = event.message
        val name = player.name.lowercase()

        if (Utils.chatLocked) {
            sendWarning(player, "§cSohbet şuanda kilitli.")
            Utils.sound(player, "item.trident.hit_ground")
            event.isCancelled = true
            return
        }

        if (Mute.isMuted(name)) {
            val endMillis = Mute.getEnd(name)
            val now = System.currentTimeMillis()
            if (endMillis <= now) {
                Mute.unmute(name)
            } else {
                event.isCancelled = true
                val reason = Mute.getReason(name)
                val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val formattedEnd = formatter.format(Date(endMillis))
                sendWarning(player, "§cSohbeti §4'$reason' §csebebi ile §4$formattedEnd §ctarihine kadar kullanamazsın")
                return
            }
        }

        if (message.equals(QuestionBotManager.getAnswer(), ignoreCase = true) && !QuestionBotManager.isAnswered()) {
            val dinner = QuestionBotManager.getDinner()
            Server.getInstance().broadcastMessage(
                "§e?\n§e? §b${player.name} §asoruya ilk doğru cevabı verdi ve §2${dinner} RF §akazandı!\n§e?"
            )
            Database.addMoney(player, dinner)
            QuestionBotManager.setAnswered(true)
            event.isCancelled = true
            return
        }

        val now = System.currentTimeMillis()
        val last = lastChatTime.getOrDefault(name, 0L)
        val cooldownMillis = 2000

        if (!Utils.isAdmin(player)) {
            if (now - last < cooldownMillis) {
                val remainingTime = (cooldownMillis - (now - last)) / 1000.0
                val remainingTimeFormatted = String.format("%.1f", remainingTime)
                val count = spamCount.getOrDefault(name, 0) + 1
                spamCount[name] = count

                event.isCancelled = true
                player.sendMessage("§cYeniden mesaj göndermek için §4§o$remainingTimeFormatted §r§csaniye beklemen gerek")

                if (count >= 5) {
                    player.kick("§cSpam yaptığın için atıldın.", false)
                    lastChatTime.remove(name)
                    spamCount.remove(name)
                }
                return
            }
            lastChatTime[name] = now
            spamCount.remove(name)
        }

        val group = GroupManager.getPlayerGroup(player)
        val format = group.chatFormat

        if (message.startsWith("!")) {
            val newMessage = message.removePrefix("!").trim()
            val formattedMessage = "§8[§c§l!§r§8]§r " + format
                .replace("%nickname%", player.name)
                .replace("%message%", newMessage)
            event.format = formattedMessage
        } else {
            val formattedMessage = "§8[§a#§8]§r " + format
                .replace("%nickname%", player.name)
                .replace("%message%", message)
            event.isCancelled = true
            for (target in player.level.players.values) {
                target.sendMessage(formattedMessage)
                Core.instance.logger.info(formattedMessage)
            }
        }
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val message = event.message.lowercase()

        if (CombatManager.isInCombat(player)) {
            player.sendMessage("§cSavaş sırasında komut kullanamazsın!")
            event.isCancelled = true
        }

        if (player.level.folderName.equals("arena", ignoreCase = true)) {
            val blockedCommands = listOf("/fly", "/size", "/heal", "/tpa", "/tpak", "/tpar")
            if (blockedCommands.any { message.startsWith(it) }) {
                if (player.isOp) return
                player.sendMessage("§8» §cBu komutu arena dünyasında kullanamazsın!")
                event.isCancelled = true
                return
            }
        }
        val name = player.name.lowercase()
        val now = System.currentTimeMillis()
        val last = lastCommandTime.getOrDefault(name, 0L)
        val cooldownMillis = 2000
        if (!Utils.isAdmin(player)) {
            if (now - last < cooldownMillis) {
                val remainingTime = (cooldownMillis - (now - last)) / 1000.0
                val remainingTimeFormatted = String.format("%.1f", remainingTime)
                event.isCancelled = true
                player.sendMessage("§cYeniden mesaj göndermek için §4§o$remainingTimeFormatted §r§csaniye beklemen gerek")
                return
            }
        }


        lastCommandTime[name] = now
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val player = event.entity as? Player ?: return
        val isArenaWorld = player.level.name.equals("arena", ignoreCase = true)
        if (event is EntityDamageByEntityEvent && event.damager is Player) {
            if (!isArenaWorld) {
                event.isCancelled = true
                sendWarning(event.damager as Player, "§cBu dünyada oyunculara saldıramazsın!")
                return
            }
            val damager = event.damager
            val victim = event.entity
            if (damager is Player && victim is Player) {
                CombatManager.startCombat(damager)
                CombatManager.startCombat(victim)
            }

            event.knockBack = HitUtils.getKnockback().toFloat()
            event.attackCooldown = HitUtils.getAttackCooldown()
            return
        }

        if (!isArenaWorld) event.isCancelled = true
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val deadPlayer = event.entity
        val lastDamage = deadPlayer.lastDamageCause
        if (lastDamage is EntityDamageByEntityEvent) {
            val damager = lastDamage.damager
            if (damager is Player) {
                val killerName = damager.name.lowercase()
                val victimName = deadPlayer.name.lowercase()

                CombatManager.removePlayer(victimName)

                Database.addKills(killerName, 1)
                Database.addMoney(killerName, 500)

                Database.addDeaths(victimName, 1)

                return
            }
        }
        CombatManager.removePlayer(deadPlayer.name.lowercase())
    }


    @EventHandler
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        if (player.levelName == "arena") {
            val arenaLevel = player.server.getLevelByName("arena") ?: return
            val spawnPos = arenaLevel.safeSpawn
            val respawnPos = ObjectObjectImmutablePair(spawnPos, SpawnPointType.PLAYER)
            event.respawnPosition = respawnPos
        }
    }

    /*@EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (Utils.isAdmin(event.player)) return
        event.isCancelled = true
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            sendWarning(event.player, "§cBu dünyada bloklarla etkileşime giremezsin!")
            return
        }
    }*/

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val player = event.player
        if (Utils.isRepairing(player.name)) {
            sendWarning(event.player, "§cTamir işlemi devam ederken eşya atamazsın.")
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInventoryPickupItem(event: InventoryPickupItemEvent) {
        val inventory = event.inventory

        val holder = inventory.holder

        if (holder is Player) {
            if (Utils.isRepairing(holder.name)) {
                sendWarning(holder, "§cTamir işlemi devam ederken eşya alamazsın.")
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun autoInventory(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val player = event.player
        val block = event.block
        if (block.id == BlockID.COBBLESTONE || block.id == BlockID.STONE) {
            if (Random.nextInt(100) < 15) {
                player.addExperience(Random.nextInt(1, 3))
            }
        }
        if (Utils.isAutoInvEnabled(player.name)) {
            val inventory = player.inventory
            for (dropItem in event.drops) {
                if (!inventory.canAddItem(dropItem)) {
                    sendToast(player, "§cEnvanterin Dolu", "§fYeni eşyalar alabilmek için envanterini boşalt.")
                    Utils.sound(player, "note.harp")
                    return
                }
                inventory.addItem(dropItem)
            }
            event.drops = emptyArray()
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player
        val ip = player.address
        /*if (!player.address.equals(Core.instance.config.getString("proxy-ip"))) {
            event.setKickMessage("§cBu sunucuya sadece hub üzerinden girebilisin.")
            event.isCancelled = true
        }*/

        if (player.loginChainData.xuid == null) {
            event.setKickMessage("§cXUID olmadan suncuuya giremezsin.")
            event.isCancelled = true
        }
        if (WhitelistManager.isActive && !WhitelistManager.players.contains(player.name.lowercase()) && !player.isOp) {
            event.setKickMessage("§cSunucu şu anda bakımda!\n§3Discord: §bredfoxmc.com/discord")
            event.isCancelled = true
        }

        val sameIpCount = Server.getInstance().onlinePlayers.values.count {
            it.address == ip && it.uniqueId != player.uniqueId
        }

        if (sameIpCount >= 2) {
            event.setKickMessage("§cBu IP adresinden fazla [2] oyuncu bağlanamaz.")
            event.isCancelled = true
            Core.Companion.instance.logger.info("IP sınırı[2] nedeniyle ${player.name} bağlantısı engellendi (IP: $ip)")
        }
        player.teleport(Server.getInstance().defaultLevel.safeSpawn)
    }

    @EventHandler
    fun onExplode(event: EntityExplodeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        val defaultWorld = player.server.defaultLevel.name
        val targetWorld = event.to.level.name

        if (targetWorld == defaultWorld) {
            if (player.gamemode != Player.ADVENTURE) {
                player.setGamemode(2)
            }
        } else {
            if (player.gamemode != Player.SURVIVAL) {
                player.setGamemode(0)
            }
        }

        if (targetWorld == "arena") player.setScale(1f)
        if (targetWorld == "arena") {
            player.adventureSettings.set(PlayerAbility.FLYING, false)
            player.adventureSettings.update()
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun entityDamageEvent(event: EntityDamageEvent) {
        if (!event.isCancelled()) {
            if (event is EntityDamageByChildEntityEvent) {
                val event1 = event
                if (event1.getEntity() is Player) {
                    if (event1.damager is Player) {
                        if (event1.child is EntityArrow || event1.child is EntityThrownTrident) {
                            event1.damager.getLevel().addSound(
                                event1.damager,
                                Sound.RANDOM_ORB,
                                1.0f,
                                1.0f,
                                *arrayOf<Player>(event1.damager as Player)
                            )
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onDataPacketSend(ev: DataPacketSendEvent) {
        val pk = ev.packet
        val p = ev.player ?: return

        if (pk is ModalFormRequestPacket && p.isConnected) {
            p.server.scheduler.scheduleDelayedTask(Core.instance, object : NukkitRunnable() {
                override fun run() {
                    p.foodData.food = p.foodData.food
                    p.experience = p.experience

                    p.server.scheduler.scheduleRepeatingTask(Core.instance, object : Task() {
                        var time = 5

                        override fun onRun(i: Int) {
                            time--

                            if (p.isOnline) {
                                p.foodData.food = p.foodData.food
                                p.experience = p.experience
                            }

                            if (time <= 0) {
                                handler?.cancel()
                            }
                        }
                    }, 10, true)
                }
            }, 1, true)
        }
    }

    @EventHandler
    private fun onGroupChange(event: PlayerGroupChangeEvent) {
        val player = event.player
        player.nameTag = event.newGroup.nameTagFormat.replace("%nickname%", player.name)
        player.sendMessage("§8» §2§o${event.newGroup.name} §r§atagına geçiş yaptın.")
    }

    @EventHandler
    private fun onRankExpired(event: PlayerRankExpiredEvent) {
        val player = event.player
        val group = GroupManager.getDefaultGroup()
        GroupManager.removePlayerGroup(player, GroupManager.getPlayerGroup(player))
        GroupManager.setPlayerGroup(player, group)
        val groupChangeEvent = PlayerGroupChangeEvent(player, group, player)
        Server.getInstance().pluginManager.callEvent(groupChangeEvent)
    }

}