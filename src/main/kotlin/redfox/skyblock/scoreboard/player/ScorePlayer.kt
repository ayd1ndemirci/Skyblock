package redfox.skyblock.scoreboard.player

import cn.nukkit.Player
import redfox.skyblock.scoreboard.Scoreboard
import redfox.skyblock.scoreboard.api.ScoreFactory
import redfox.skyblock.scoreboard.placeholder.PlaceholderAPI

class ScorePlayer(
    private val player: Player,
    private var scoreboard: Scoreboard
) {
    init {
        refresh()
    }

    fun update(params: String) {
        val line = scoreboard.getPlaceholderParamsLine(params) ?: return
        ScoreFactory.editLine(player, line, PlaceholderAPI.setPlaceholders(player, scoreboard.lines[line]))
    }

    private fun refresh() {
        val lines: MutableMap<Int, String> = mutableMapOf()
        scoreboard.lines.forEachIndexed { index, line ->
            lines[index] = PlaceholderAPI.setPlaceholders(player, line)
        }

        ScoreFactory.sendScore(player, scoreboard.title)
        ScoreFactory.setLines(player, lines)
    }

    fun changeScoreboard(scoreboard: Scoreboard) {
        this.scoreboard = scoreboard
        ScoreFactory.removeToPlayer(player)
        refresh()
    }
}