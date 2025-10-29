package redfox.skyblock.utils

object AutoSell {
    private val data = mutableMapOf<String, MutableMap<String, Boolean>>()

    fun getAll(playerName: String): Map<String, Boolean> {
        return data[playerName] ?: emptyMap()
    }

    fun get(playerName: String, type: String): Boolean {
        return data[playerName]?.get(type) ?: false
    }

    fun set(playerName: String, type: String, value: Boolean) {
        val settings = data.getOrPut(playerName) { mutableMapOf() }
        settings[type] = value
    }
}
