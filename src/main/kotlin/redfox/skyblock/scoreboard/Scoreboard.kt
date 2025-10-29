package redfox.skyblock.scoreboard

import redfox.skyblock.scoreboard.placeholder.PlaceholderAPI

class Scoreboard(
    val name: String,
    val title: String,
    val lines: List<String>
) {
    private val placeholders: MutableMap<Int, List<String>> = mutableMapOf()
    private val placeholderRegistryBindMap: MutableMap<String, Int> = mutableMapOf()

    init {
        lines.forEachIndexed { index, text ->
            val params = PlaceholderAPI.splitPlaceholders(text)
            if (params.isNotEmpty()) {
                placeholders[index] = params
                params.forEach { placeholderRegistryBindMap[it] = index }
            }
        }
    }

    fun getPlaceholderParamsLine(params: String): Int? = placeholderRegistryBindMap[params]
}