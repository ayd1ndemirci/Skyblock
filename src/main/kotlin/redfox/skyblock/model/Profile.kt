package redfox.skyblock.model

import com.google.gson.Gson
import redfox.skyblock.data.Tag

data class Profile(
    val name: String,
    var primaryGroup: String,
    var selectedGroup: String,
    var groups: List<String> = emptyList(),
    var permissions: List<String> = emptyList(),
    var time: Long = 0L
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun save() {
        Tag.setProfile(name, this)
    }
}