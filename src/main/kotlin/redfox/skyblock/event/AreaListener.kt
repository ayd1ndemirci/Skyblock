package redfox.skyblock.listener

import cn.nukkit.Player
import cn.nukkit.block.BlockID
import cn.nukkit.entity.item.EntityItem
import cn.nukkit.entity.item.EntitySplashPotion
import cn.nukkit.entity.projectile.*
import cn.nukkit.event.*
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.entity.*
import cn.nukkit.event.player.*
import cn.nukkit.event.Listener
import cn.nukkit.event.inventory.InventoryPickupItemEvent

class AreaListener : Listener {

    private val blockedWorlds = listOf("world", "MobArena", "bossarena", "arena")

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val world = player.level

        if (world.folderName in blockedWorlds && !player.isOp) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val world = player.level

        if (world.folderName in listOf("nether", "end")) {
            event.isCancelled = true
        }
        if (world.folderName in blockedWorlds && !player.isOp) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        val world = player.level

        if (world.folderName in blockedWorlds && !player.isOp) {
            event.isCancelled = true
        }
    }


    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        val player = event.player
        val world = player.level
        if (world.folderName in blockedWorlds && !player.isOp) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPickup(event: InventoryPickupItemEvent) {
        val itemEntity: EntityItem = event.item
        val inventory = event.inventory
        val player = inventory.holder as? Player ?: return

        val world = player.level

        if (world.folderName in listOf("world", "bossarena", "Arena") && !player.isOp) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity !is Player) return

        val cause = event.cause
        val worldName = entity.level.folderName

        if (worldName in listOf("world", "MobArena", "end", "nether")) {
            if (event is EntityDamageByEntityEvent) {
                event.isCancelled = true

            }
        }

        if (worldName in listOf("world", "MobArena", "arena") && cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager
        if (damager !is Player) return

        val world = damager.level
        val worldName = world.folderName

        if (worldName in blockedWorlds) {
            if (worldName == "arena" && entity.y > 76) {
                event.isCancelled = true
            } else if (worldName == "world" && entity is Player) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onFoodChange(event: PlayerFoodLevelChangeEvent) {
        val player = event.player
        if (player.level.folderName.equals("world", ignoreCase = true)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onShoot(event: EntityShootBowEvent) {
        val player = event.entity
        if (player !is Player) return

        val world = player.level
        if (world.folderName == "world") {
            event.isCancelled = true
        }
        if (world.folderName == "arena" && player.y > 76) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onProjectileLaunch(event: ProjectileLaunchEvent) {
        val shooter = event.shooter
        val entity = event.entity

        if (shooter is Player) {
            val world = shooter.level

            if (world.folderName.equals("world", ignoreCase = true) &&
                (entity is EntityEnderPearl || entity is EntitySnowball || entity is EntitySplashPotion || entity is EntityEgg)) {
                event.isCancelled = true
            }

            if (world.folderName.equals("arena", ignoreCase = true) && shooter.y > 76) {
                event.isCancelled = true
            }
        }
    }

}
