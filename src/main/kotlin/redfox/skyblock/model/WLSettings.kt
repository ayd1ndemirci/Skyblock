package redfox.skyblock.model

data class WLSettings(
    var active: Boolean = false,
    var players: MutableList<String> = mutableListOf()
)
