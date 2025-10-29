package redfox.skyblock.service

import cn.nukkit.Player
import cn.nukkit.utils.Config
import redfox.skyblock.manager.RewardManager
import redfox.skyblock.model.ClaimData
import redfox.skyblock.model.RewardEntry
import java.io.File
import java.time.LocalDate



object RewardService {

    private val configFile = File("plugins/Core/daily_rewards.yml")
    private val config = Config(configFile, Config.YAML)

    private val claimedData: MutableMap<String, ClaimData> = mutableMapOf()

    init {
        val saved = config.getSection("claimedDates")
        if (saved != null) {
            for ((key, value) in saved) {
                val section = value as? Map<*, *>
                if (section != null) {
                    val date = section["date"] as? String ?: continue
                    val rewardIndex = (section["rewardIndex"] as? Int) ?: continue
                    val claimed = section["claimed"] as? Boolean ?: false
                    claimedData[key] = ClaimData(date, rewardIndex, claimed)

                }
            }
        }
    }

    fun canClaim(player: Player): Boolean {
        val today = LocalDate.now().toString()
        val claim = claimedData[player.uniqueId.toString()]
        return !(claim?.date == today && claim.claimed)
    }

    fun markClaimed(player: Player, rewardIndex: Int) {
        val today = LocalDate.now().toString()
        claimedData[player.uniqueId.toString()] = ClaimData(today, rewardIndex, true)
        saveConfig()
    }

    private fun saveConfig() {
        val mapToSave = claimedData.mapValues { (_, claim) ->
            mapOf(
                "date" to claim.date,
                "rewardIndex" to claim.rewardIndex,
                "claimed" to claim.claimed
            )
        }
        config.set("claimedDates", mapToSave)
        config.save()
    }

    fun getTodaysReward(player: Player): RewardEntry? {
        val rewards = RewardManager.getAllRewards()
        val playerKey = player.uniqueId.toString()
        val today = LocalDate.now().toString()

        val claim = claimedData[playerKey]
        if (claim != null && claim.date == today) {
            return rewards.getOrNull(claim.rewardIndex)
        }

        val randomIndex = (rewards.indices).random()
        claimedData[playerKey] = ClaimData(today, randomIndex, false)
        saveConfig()

        return rewards.getOrNull(randomIndex)
    }

}