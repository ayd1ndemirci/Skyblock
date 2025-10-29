package redfox.skyblock.form.island

import cn.nukkit.Player
import cn.nukkit.Server
import cn.nukkit.form.element.simple.ButtonImage
import cn.nukkit.form.element.simple.ElementButton
import cn.nukkit.form.response.SimpleResponse
import cn.nukkit.form.window.SimpleForm
import redfox.skyblock.Island
import redfox.skyblock.data.IslandDB
import redfox.skyblock.event.custom.IslandSpawnPositionChangeEvent
import redfox.skyblock.form.island.block.BlockPlayersToIslandForm
import redfox.skyblock.form.island.isperm.IslandPermissionForm
import redfox.skyblock.form.island.partner.LeavePartnerIslandForm
import redfox.skyblock.form.island.partner.PartnerMenuForm
import redfox.skyblock.listener.event.IslandTeleportEvent
import redfox.skyblock.permission.Permission
import redfox.skyblock.utils.IslandUtils

object IslandForm {

    private val partnersDontUse = listOf(
        "Ada Devret",
        "Ada İzin",
        "§cAda Sil"
    )

    fun send(player: Player, partner: Boolean = false) {
        val worldName = player.level.folderName
        val playerName = player.name

        val validWorld = if (!partner) {
            worldName == playerName
        } else {
            IslandDB.getPlayerPartner(playerName)?.let { it == worldName } ?: false
        }

        val buttons = listOf(
            "Adana Işınlan" to "textures/items/ender_pearl.png",
            "Ada Ziyareti" to "textures/ui/worldsIcon.png",
            "Ada Seviye" to "textures/items/experience_bottle.png",
            "Ada Seviye Sıralaması" to "textures/ui/up_arrow.png",
            "Adadan Oyuncu Tekmele" to "textures/ui/speed_effect.png",
            "Ada Doğma Noktasını Belirle" to "textures/blocks/cartography_table_top.png",
            "Ada Ayarları" to "textures/ui/icon_setting.png",
            "Adadan Oyuncu Engelle" to "textures/ui/hammer_l.png",
            "Ada Reklam" to "textures/items/goat_horn.png",
            "Warp Noktaları" to "textures/items/sign.png",
            "Ortaklık Menüsü" to "textures/ui/dressing_room_skins.png",
            "Ada İzin" to "textures/ui/permissions_visitor_hand.png",
            //"Ada Devret" to "textures/ui/refresh.png",
            "§cAdayı Sil" to "textures/ui/trash.png"
        )


        val options = if (!validWorld) {
            buttons.take(2).toMutableList()
        } else {
            buttons.toMutableList()
        }

        if (partner) {
            options.removeAll { it.first in partnersDontUse }
            options.add("§cAdadan Ayrıl" to "textures/ui/realms_red_x.png")
        }

        val form = SimpleForm("Ada Menüsü")

        options.forEach { (text, imagePath) ->
            form.addElement(ElementButton(text, ButtonImage(ButtonImage.Type.PATH, imagePath)))
        }

        form.send(player)

        form.onSubmit { _: Player?, response: SimpleResponse? ->
            if (response == null) return@onSubmit
            val selectedText = options.getOrNull(response.buttonId())?.first ?: return@onSubmit

            when (selectedText) {
                "Adana Işınlan" -> {
                    IslandTeleportEvent(player, null, partner).call()
                }

                "Ada Ziyareti" -> IslandVisitForm.send(player, partner)
                "Ada Seviye" -> IslandLevelForm.send(player, partner)
                "Ada Seviye Sıralaması" -> IslandTopLevelForm.send(player, partner)
                "Adadan Oyuncu Tekmele" -> {
                    val islandWorld = if (partner) IslandDB.getPlayerPartner(playerName) ?: playerName else playerName
                    val kickablePlayers = mutableListOf<String>()

                    val world = Server.getInstance().getLevelByName(islandWorld)
                    if (world != null) {
                        for (entity in world.players) {
                            if (entity is Player) {
                                val playerNameInWorld = entity.name
                                if (playerNameInWorld == playerName
                                    || Island.isPartner(playerNameInWorld, islandWorld)
                                    || player.hasPermission(IslandUtils.PLAYER_INVISIBLE)
                                    || player.hasPermission(IslandUtils.ISLAND_ADMIN_PERMISSION)
                                ) continue

                                kickablePlayers.add(playerNameInWorld)
                            }
                        }
                    }


                    if (kickablePlayers.isEmpty()) {
                        player.sendMessage("§cAdada tekmeyebileceğin kimse yok!")
                    } else IslandKickPlayersForm.send(player, partner)
                }

                "Ada Doğma Noktasını Belirle" -> IslandSpawnPositionChangeEvent(player, partner).call()
                "Ada Ayarları" -> IslandSettingsForm.send(player, partner)
                "Adadan Oyuncu Engelle" -> BlockPlayersToIslandForm.send(player, partner)
                "Ada Reklam" -> {
                    if (!player.hasPermission(Permission.VIP)
                        && !player.hasPermission(Permission.VIP_PLUS)
                        && !player.hasPermission(Permission.MVIP)
                    ) {
                        player.sendMessage("§cBu özelliği kullanabilmek için Özel Üyelik satın almanız gereklidir!")
                        return@onSubmit
                    }
                    IslandAdvertisementForm.send(player)
                }

                "Warp Noktaları" -> IslandWarpForm.send(player, partner)
                "Ortaklık Menüsü" -> PartnerMenuForm.send(player, partner)
                "Ada İzin" -> IslandPermissionForm.send(player, partner)
                /*"Ada Devret" -> {
                    val islandData = IslandDB.getIsland(playerName)
                    val partners = islandData?.getList("partners", String::class.java) ?: listOf()
                    if (partners.isNotEmpty()) {
                        IslandTransferForm.send(player)
                    } else player.sendMessage("§cAda devredebilmen için bir ortağının bulunması gerekli!")
                }*/
                "§cAdayı Sil" -> IslandDeleteForm.send(player)
                "§cAdadan Ayrıl" -> LeavePartnerIslandForm.send(player)
            }
        }
    }
}