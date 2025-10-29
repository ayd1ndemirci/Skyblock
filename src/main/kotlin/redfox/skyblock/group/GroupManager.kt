package redfox.skyblock.group

import cn.nukkit.Player
import cn.nukkit.utils.Config
import cn.nukkit.utils.ConfigSection
import redfox.skyblock.Core
import redfox.skyblock.data.Tag
import redfox.skyblock.enums.Process
import redfox.skyblock.manager.PermissionManager
import redfox.skyblock.utils.Utils

object GroupManager {

    var groups: MutableList<Group> = mutableListOf()

    fun load() {
        groups.clear()
        val config = Config("${Core.instance.dataFolder}/groups.yml", Config.YAML)
        config.all.forEach { (groupName, _) ->
            val section: ConfigSection = config.getSection(groupName)
            val group = Group(
                section.getString("name")!!,
                section.getString("id")!!,
                section.getString("nameTagFormat")!!,
                section.getString("chatFormat")!!,
                section.getStringList("aliases"),
                section.getStringList("permissions"),
                section.getStringList("inheritance")
            )
            groups.add(group)
        }
    }

    fun getGroup(name: String): Group? {
        return groups.find {
            it.name.equals(name, ignoreCase = true) ||
                    it.aliases.contains(name.lowercase()) ||
                    it.id.equals(name, ignoreCase = true)
        }
    }

    fun getGroupIds(): List<String> = groups.map { it.id }

    fun getDefaultGroup(): Group {
        val config = Core.instance.config
        return getGroup(config.getString("defaultGroup")) ?: run {
            val group = groups.first()
            setDefaultGroup(group)
            group
        }
    }

    fun setDefaultGroup(group: Group) {
        val config = Core.instance.config
        config.set("defaultGroup", group.id)
        config.save()
    }

    fun getPlayerGroup(player: Player): Group {
        return getPlayerGroup(player.name)
    }

    private fun getPlayerGroup(name: String): Group {
        val profile = Tag.getProfile(name)
        return profile?.let {
            getGroup(it.selectedGroup) ?: getDefaultGroup()
        } ?: run {
            Tag.createProfile(name)
            getDefaultGroup()
        }
    }

    fun getPlayerGroups(player: Player): List<Group> {
        return getPlayerGroups(player.name)
    }

    fun removePlayerGroup(player: Player, group: Group) {
        removePlayerGroup(player.name, group)
    }

    fun removePlayerGroup(name: String, group: Group) {
        val profile = Tag.getProfile(name)
        profile?.let { it ->
            if (it.selectedGroup == group.id) {
                val newGroup = it.groups.firstNotNullOfOrNull { getGroup(it) } ?: getDefaultGroup()
                it.selectedGroup = newGroup.id
            }
            it.groups = it.groups.filter { it != group.id }
            it.save()
        }
        PermissionManager.reloadPermissions()
    }

    fun getPlayerGroups(name: String): List<Group> {
        val profile = Tag.getProfile(name)
        return profile?.let {
            it.groups.mapNotNull { group -> getGroup(group) }
        } ?: run {
            Tag.createProfile(name)
            listOf(getDefaultGroup())
        }
    }

    fun setPlayerGroup(player: Player, group: Group) {
        setPlayerGroup(player.name, group)
    }

    fun setPlayerGroup(name: String, group: Group) {
        val profile = Tag.getProfile(name)
        if (!getPlayerGroups(name).contains(group)) {
            profile?.let {
                it.groups = it.groups.toMutableList().apply { add(group.id) }
                it.save()
            }
        }
        profile?.let {
            it.selectedGroup = group.id
            it.primaryGroup = group.id
            it.save()
        }
        PermissionManager.reloadPermissions()
    }

    fun addGroup(groupName: String): Process {
        return when {
            Utils.isInvalidGroupName(groupName) -> Process.INVALID_NAME
            getGroup(groupName) != null -> Process.ALREADY_EXISTS
            else -> {
                val config = Config("${Core.instance.dataFolder}/groups.yml", Config.YAML)
                config.set("$groupName.name", groupName)
                config.set("$groupName.id", groupName.lowercase())
                config.set("$groupName.nameTagFormat", "§7[§a$groupName§7] %nickname%")
                config.set("$groupName.chatFormat", "§7[§a$groupName§7] %nickname% §e» §f%message%")
                config.set("$groupName.aliases", mutableListOf<String>())
                config.set("$groupName.permissions", mutableListOf<String>())
                config.set("$groupName.inheritance", mutableListOf<String>())
                config.save()
                load()
                Process.SUCCESS
            }
        }
    }

    fun removeGroup(groupName: String): Process {
        return when {
            Utils.isInvalidGroupName(groupName) -> Process.INVALID_NAME
            getGroup(groupName) == null -> Process.NOT_FOUND
            else -> {
                val group = getGroup(groupName) ?: return Process.NOT_FOUND
                val config = Config("${Core.instance.dataFolder}/groups.yml", Config.YAML)
                config.remove(group.name)
                config.save()
                load()
                Process.SUCCESS
            }
        }
    }

    fun setChatFormat(group: Group, format: String) {
        val config = Config("${Core.instance.dataFolder}/groups.yml", Config.YAML)
        config.set("${group.id}.chatFormat", format)
        config.save()
        load()
    }
}