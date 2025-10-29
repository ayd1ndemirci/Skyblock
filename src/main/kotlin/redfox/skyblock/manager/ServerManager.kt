package redfox.skyblock.manager

import cn.nukkit.Server
import redfox.skyblock.Core
import redfox.skyblock.config.WLConfig
import redfox.skyblock.data.Mute
import redfox.skyblock.event.EventListener
import redfox.skyblock.event.ItemDurability
import redfox.skyblock.event.ModerationListener
import redfox.skyblock.event.OtherListener
import redfox.skyblock.group.GroupManager
import redfox.skyblock.listener.AreaListener
import redfox.skyblock.listener.IslandListener
import redfox.skyblock.task.*
import redfox.skyblock.utils.HitUtils

object ServerManager {

    fun run() {
        //  CommandManager.unregister()
        CommandManager.register()
        registerEvents()
        registerTasks()
        ShopManager.load()
        Mute.clearExpiredMutes()
        HitUtils.load()
        GroupManager.load()
        PermissionManager.addPermsToOnlinePlayers()
        WLConfig.load()
        //AuctionManager.initialize()
    }

    fun registerEvents() {
        Server.getInstance().pluginManager.registerEvents(ModerationListener(), Core.instance)
        Server.getInstance().pluginManager.registerEvents(EventListener(), Core.instance)
        Server.getInstance().pluginManager.registerEvents(IslandListener(), Core.instance)
        Server.getInstance().pluginManager.registerEvents(AreaListener(), Core.instance)
        Server.getInstance().pluginManager.registerEvents(ItemDurability(), Core.instance)
        Server.getInstance().pluginManager.registerEvents(OtherListener(), Core.instance)
    }

    fun registerTasks() {
        Server.getInstance().scheduler.scheduleRepeatingTask(ItemClearTask(), 20)
        Server.getInstance().scheduler.scheduleRepeatingTask(AutoBroadcastTask(), 20 * 600)
        Server.getInstance().scheduler.scheduleRepeatingTask(QuestionBotTask(), 20 * 60)
        Server.getInstance().scheduler.scheduleRepeatingTask(ExpireDateCheckTask(), 72000)
        Server.getInstance().scheduler.scheduleRepeatingTask(PlayerCountSyncTask(), 20 * 120)
        Server.getInstance().scheduler.scheduleRepeatingTask(SaveBlockCountTask(), 20 * 60)
        start()
    }

    var restartTask: RestartTask? = null

    fun start() {
        stop()
        restartTask = RestartTask(4 * 60 * 60)
        restartTask?.start()
    }

    fun start(seconds: Int) {
        stop()
        restartTask = RestartTask(seconds)
        restartTask?.start()
    }

    fun stop() {
        restartTask?.stop()
        restartTask = null
    }

    fun forceRestartIn(seconds: Int) = start(seconds)
}