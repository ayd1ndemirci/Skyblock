package redfox.skyblock.data

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.data.MongoDB.db
import redfox.skyblock.group.GroupManager
import redfox.skyblock.model.Profile

object Tag {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    private val profiles = db.getCollection("tag")

    fun getProfile(name: String): Profile? {
        val doc = profiles.find(Filters.eq("name", name.lowercase())).first() ?: return null
        val primaryGroup = doc.getString("primaryGroup") ?: return null
        val selectedGroup = doc.getString("selectedGroup") ?: primaryGroup
        val groups = doc.getList("group", String::class.java) ?: emptyList()
        val permissions = doc.getList("permissions", String::class.java) ?: emptyList()
        val time = doc.getLong("time") ?: 0L
        return Profile(name.lowercase(), primaryGroup, selectedGroup, groups, permissions, time)
    }

    fun setProfile(name: String, profile: Profile) {
        val update = Updates.combine(
            Updates.set("primaryGroup", profile.primaryGroup),
            Updates.set("selectedGroup", profile.selectedGroup),
            Updates.set("group", profile.groups),
            Updates.set("permissions", profile.permissions),
            Updates.set("time", profile.time)
        )
        profiles.updateOne(
            Filters.eq("name", name.lowercase()),
            update,
            UpdateOptions().upsert(true)
        )
    }

    fun profileExists(name: String): Boolean {
        return profiles.find(Filters.eq("name", name.lowercase())).first() != null
    }

    fun createProfile(name: String) {
        if (profileExists(name)) return
        val defaultGroup = GroupManager.getDefaultGroup()
        val profile = Profile(
            name = name.lowercase(),
            primaryGroup = defaultGroup.id,
            selectedGroup = defaultGroup.id,
            groups = listOf(defaultGroup.id)
        )
        val doc = Document("name", profile.name)
            .append("primaryGroup", profile.primaryGroup)
            .append("selectedGroup", profile.selectedGroup)
            .append("group", profile.groups)
            .append("permissions", profile.permissions)
            .append("time", profile.time)
        profiles.insertOne(doc)
    }
}