package redfox.skyblock.listener

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.block.Block
import cn.nukkit.block.BlockChest
import cn.nukkit.block.BlockDispenser
import cn.nukkit.block.BlockDropper
import cn.nukkit.block.BlockEnderChest
import cn.nukkit.block.BlockFurnace
import cn.nukkit.block.BlockHopper
import cn.nukkit.block.BlockID
import cn.nukkit.block.BlockSignBase
import cn.nukkit.block.BlockWallSign
import cn.nukkit.blockentity.BlockEntitySign
import cn.nukkit.entity.item.EntityItem
import cn.nukkit.event.EventHandler
import cn.nukkit.event.Listener
import cn.nukkit.event.block.BlockBreakEvent
import cn.nukkit.event.block.BlockPistonEvent
import cn.nukkit.event.block.BlockPlaceEvent
import cn.nukkit.event.block.BlockUpdateEvent
import cn.nukkit.event.entity.EntityDamageByEntityEvent
import cn.nukkit.event.entity.EntityDamageEvent
import cn.nukkit.event.inventory.InventoryPickupItemEvent
import cn.nukkit.event.player.*
import cn.nukkit.level.Sound
import cn.nukkit.network.protocol.types.PlayerAbility
import com.google.gson.Gson
import org.bson.Document
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB
import redfox.skyblock.utils.IslandUtils // varsayalım GSON burada var
import redfox.skyblock.event.custom.IslandLevelChangeEvent
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.Utils

class IslandListener : Listener {

    companion object {
        val gson: Gson = Gson()
    }


    @EventHandler
    fun onToggleFlight(event: PlayerToggleFlightEvent) {
        val player = event.player
        val world = player.level.folderName

        if (player.name != world && Island.hasIsland(world)) {
            val island = IslandDB.getIsland(world) ?: return
            val settingsJson = island["settings"]?.toString() ?: return
            val settings = gson.fromJson(settingsJson, Map::class.java) as Map<String, Any>

            if (!Island.isPartner(player.name, world)
                && (settings["vipFly"] as? Boolean != true)
                && !player.isOp
                && !player.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION)
            ) {
                player.sendActionBar("§cBu adada VIP oyuncuların uçması kapalı!")
                player.allowFlight = false
                player.adventureSettings.set(PlayerAbility.FLYING, player.allowFlight)
                player.adventureSettings.update()
            }
        }

        val restrictedWorlds = listOf(
            Server.getInstance().defaultLevel.folderName,
            "MobArena", "nether", "end", "pvparena"
        )

        if (world in restrictedWorlds) {
            player.adventureSettings.set(PlayerAbility.FLYING, player.allowFlight)
            player.adventureSettings.update()
        }
    }

    @EventHandler
    fun onInventoryPickup(event: InventoryPickupItemEvent) {
        val itemEntity: EntityItem = event.item
        val ownerName = itemEntity.owner ?: return // item sahibini bilmek zor olabilir, yoksa atlayabiliriz

        val inventory = event.inventory
        val player = inventory.holder as? Player ?: return

        val world = player.level.folderName
        if (world == player.name || IslandDB.getPlayerPartner(player.name) == world) return

        val island = IslandDB.getIsland(world) ?: return
        val settingsJson = island["settings"]?.toString() ?: return
        val settings = gson.fromJson(settingsJson, Map::class.java) as Map<*, *>

        if (settings["pickup"] as? Boolean == false) {
            if (!Island.hasPermission(player.name, "PickItem", world)
                && !player.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION)
            ) {
                event.isCancelled = true
                player.sendActionBar("§cBu adada ziyaretcilerin yerden eşya alıp/atması kapalı!")
            }
        }
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        val player = event.player
        val world = player.level.folderName

        if (world == player.name || IslandDB.getPlayerPartner(player.name) == world) return

        val island = IslandDB.getIsland(world) ?: return
        val settingsJson = island["settings"]?.toString() ?: return
        val settings = gson.fromJson(settingsJson, Map::class.java) as Map<*, *>

        if (settings["pickup"] as? Boolean == false) {
            if (!Island.hasPermission(player.name, "PickItem", world)
                && !player.hasPermission("island.admin.permission")
            ) {
                event.isCancelled = true
                player.sendActionBar("§cBu adada ziyaretcilerin yerden eşya alıp/atması kapalı!")
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val block = event.block
        val worldName = player.level.folderName
        val playerName = player.name

        val typeId = block.id

        val isCactus = typeId == Block.CACTUS
        val isHopper = typeId == Block.HOPPER

        if (isCactus || isHopper) {
            val blockType = if (isCactus) "cactus" else "hopper"

            val level = block.level
            val x = block.x.toInt()
            val y = block.y.toInt()
            val z = block.z.toInt()

            val belowBlockId = level.getBlockIdAt(x, y - 1, z)

            val northBlockId = level.getBlockIdAt(x, y, z - 1)
            val southBlockId = level.getBlockIdAt(x, y, z + 1)
            val westBlockId = level.getBlockIdAt(x - 1, y, z)
            val eastBlockId = level.getBlockIdAt(x + 1, y, z)

            val isValidPlacement = belowBlockId == Block.SAND || belowBlockId == Block.RED_SAND

            // Eğer kumun etrafında (sağ, sol, ön, arka) herhangi bir blok varsa limite ekleme yapma
            if (northBlockId != Block.AIR ||
                southBlockId != Block.AIR ||
                westBlockId != Block.AIR ||
                eastBlockId != Block.AIR
            ) {
                // Etraf bloklu, limite ekleme yapılmaz, devam et
            } else {
                // Etraf boşsa limite ekleme yapılır
                if (isValidPlacement) {
                    val current = Island.getBlockCount(worldName, blockType)
                    val max = Island.MAX_LIMITS[blockType] ?: -1

                    if (max != -1 && current >= max) {
                        event.isCancelled = true
                        player.sendActionBar("§cAda ${blockType.uppercase()} sınırına ulaştın ($current/$max)!")
                        return
                    }
                    Island.incrementBlockCount(worldName, blockType)
                }
            }
        }

        val blockName = block.toItem().name.lowercase()
        if (blockName in Island.getBannedBlocks()) return

        val xpControl = fun(islandName: String, player: Player, block: Block) {
            Island.addXP(islandName, 1)
        }

        if (worldName == playerName) {
            xpControl(playerName, player, block)
            return
        }

        if (IslandDB.getIsland(worldName) != null) {
            if (Island.isPartner(playerName, worldName)
                || Island.hasPermission(playerName, "Place", worldName)
                || player.isOp
                || player.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION)
            ) {
                xpControl(worldName, player, block)
            } else {
                player.sendActionBar("§cOrtak olmadığın adaya dokunamazsın!")
                event.isCancelled = true
            }
        }
    }


    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val world = player.level.folderName
        val block = event.block
        val typeId = block.id

        val aboveBlock1 = block.level.getBlockIdAt(block.x.toInt(), block.y.toInt() + 1, block.z.toInt())
        val aboveBlock2 = block.level.getBlockIdAt(block.x.toInt(), block.y.toInt() + 2, block.z.toInt())
        if ((aboveBlock1 == Block.SAND || aboveBlock1 == Block.RED_SAND) && aboveBlock2 == Block.CACTUS) {
            Island.decrementBlockCount(world, "cactus")
        }

        if (typeId == Block.CACTUS) {
            val belowBlockId = block.level.getBlockIdAt(block.x.toInt(), block.y.toInt() - 1, block.z.toInt())
            if (belowBlockId == Block.SAND || belowBlockId == Block.RED_SAND) {
                Island.decrementBlockCount(world, "cactus")
            }
        }

        if (typeId == Block.SAND || typeId == Block.RED_SAND) {
            for (dy in 1..3) {
                val aboveBlockId = block.level.getBlockIdAt(block.x.toInt(), block.y.toInt() + dy, block.z.toInt())
                if (aboveBlockId == Block.CACTUS) {
                    Island.decrementBlockCount(world, "cactus")
                    break // sadece ilk kaktüs için sil
                } else if (aboveBlockId != Block.AIR) {
                    break // üstünde başka blok varsa döngüden çık
                }
            }
        }

        if (typeId == Block.HOPPER) {
            Island.decrementBlockCount(world, "hopper")
        }

        val blockName = block.toItem().name.lowercase()
        if (blockName in Island.getBannedBlocks()) return

        if (world == player.name) {
            Island.removeXP(player.name, 1, player)
        } else if (IslandDB.getIsland(world) != null) {
            if (Island.isPartner(player.name, world)
                || Island.hasPermission(player.name, "Break", world)
                || player.isOp
                || player.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION)
            ) {
                Island.removeXP(world, 1, player)
            } else {
                player.sendActionBar("§cOrtak olmadığın adaya dokunamazsın!")
                event.isCancelled = true
            }
        }
    }


    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        val entity = event.entity

        if (event is EntityDamageByEntityEvent && entity is Player && event.damager is Player) {
            if (IslandDB.getIsland(entity.level.folderName) != null) {
                event.isCancelled = true
            }
        }

        if (event.cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if (IslandDB.getIsland(entity.level.folderName) != null) {
                entity.setOnFire(0)
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onIslandLevelChange(event: IslandLevelChangeEvent) {
        val player = event.player
        val oldLevel = event.oldLevel
        val newLevel = event.newLevel

        player.level.players.values.forEach { p ->
            if (oldLevel < newLevel) {
                p.sendTitle("§3Ada Seviyesi Arttı", "§aAda artık §2$newLevel §aseviye")
                Utils.sound(p, "random.levelup")
            } else {
                p.sendTitle("§3Ada Seviyesi Düştü", "§cAda artık §4$newLevel §cseviye")
                p.sendMessage("§cAda seviyesi düştü, yeni seviye $newLevel")
                Utils.sound(p, "note.bd")
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.block ?: return
        val worldName = player.level.folderName
        val playerName = player.name

        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return

        if (block is BlockSignBase) {
            if (worldName != playerName && IslandDB.getIsland(worldName) != null) {
                if (!Island.isPartner(playerName, worldName)
                    && !player.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION)
                    && !player.isOp
                ) {
                    player.sendActionBar("§cOrtak olmadığın adaya dokunamazsın!")
                    event.isCancelled = true
                    return
                }
            }
        }

        val blockedBlocks = listOf(
            BlockChest::class,
            BlockEnderChest::class,
            BlockHopper::class,
            BlockFurnace::class,
            BlockDispenser::class,
            BlockDropper::class,
        )

        if (blockedBlocks.any { it.isInstance(block) }) {
            if (worldName != playerName && IslandDB.getIsland(worldName) != null) {
                if (!Island.isPartner(playerName, worldName)
                    && !player.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION)
                    && !player.isOp
                ) {
                    player.sendActionBar("§cOrtak olmadığın adaya dokunamazsın!")
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onBlockUpdate(event: BlockUpdateEvent) {
        val block = event.block
        val worldName = block.level.folderName

        if (block.id == Block.CACTUS || block.id == Block.RED_SAND) {
            val level = block.level
            val x = block.x.toInt()
            val y = block.y.toInt()
            val z = block.z.toInt()

            val belowBlock = level.getBlockIdAt(x, y - 1, z)

            if (belowBlock == Block.SAND || belowBlock == Block.RED_SAND) {
                val northBlock = level.getBlockIdAt(x, y, z - 1)
                val southBlock = level.getBlockIdAt(x, y, z + 1)
                val westBlock = level.getBlockIdAt(x - 1, y, z)
                val eastBlock = level.getBlockIdAt(x + 1, y, z)

                val hasAdjacentBlock = northBlock != Block.AIR ||
                        southBlock != Block.AIR ||
                        westBlock != Block.AIR ||
                        eastBlock != Block.AIR

                if (hasAdjacentBlock) {
                    Island.decrementBlockCount(worldName, "cactus")
                }
            }
        }
    }

    @EventHandler
    fun onBlockPiston(event: BlockPistonEvent) {
        val level = event.block.level
        val worldName = level.folderName

        if (event.isExtending) {
            for (block in event.blocks) {
                if (block.id == Block.SAND || block.id == Block.RED_SAND) {
                    val x = block.x.toInt()
                    val y = block.y.toInt()
                    val z = block.z.toInt()

                    val aboveBlock = level.getBlock(x, y + 1, z)
                    if (aboveBlock.id == Block.CACTUS) {
                        Island.decrementBlockCount(worldName, "cactus")
                    }
                }
            }
        } else {
            for (destroyedBlock in event.destroyedBlocks) {
                if (destroyedBlock.id == Block.CACTUS) {
                    Island.decrementBlockCount(worldName, "cactus")
                }
            }
        }
    }
}