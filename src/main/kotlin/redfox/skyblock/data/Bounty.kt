package redfox.skyblock.data

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bson.Document
import redfox.skyblock.data.MongoDB.db
import redfox.skyblock.utils.Utils.DEFAULT_PAGE_LIMIT

object Bounty {

    init {
        Configurator.setLevel("org.mongodb.driver", Level.OFF)
    }

    private val headBounty = db.getCollection("headBounty")

    enum class BountyViewMode {
        ALL,
        WARLORD,
        TARGET
    }

    fun getAll(page: Int, mode: BountyViewMode, target: String?, limit: Int = DEFAULT_PAGE_LIMIT): List<Document> {
        val query = when (mode) {
            BountyViewMode.ALL -> headBounty.find()
            BountyViewMode.WARLORD -> headBounty.find(Filters.eq("warlord", target))
            BountyViewMode.TARGET -> headBounty.find(Filters.eq("target", target))
        }

        return query
            .skip((page - 1) * limit)
            .limit(limit)
            .toList()
    }

    fun getReward(target: String): Int {
        var reward = 0
        headBounty.find(Filters.eq("target", target)).forEach { doc ->
            reward += doc.getInteger("reward", 0)
        }
        return reward
    }

    fun getPageCount(mode: BountyViewMode, target: String?, limit: Int = DEFAULT_PAGE_LIMIT): Int {
        val count = when (mode) {
            BountyViewMode.ALL -> headBounty.countDocuments()
            BountyViewMode.WARLORD -> headBounty.countDocuments(Filters.eq("warlord", target))
            BountyViewMode.TARGET -> headBounty.countDocuments(Filters.eq("target", target))
        }

        return if (count == 0L) 1 else ((count - 1) / limit + 1).toInt()
    }

    fun getByTarget(target: String): Document? {
        return headBounty.find(Filters.eq("target", target)).first()
    }

    fun set(warlord: String, target: String, reward: Int) {
        val bountyDoc = Document()
            .append("target", target)
            .append("warlord", warlord)
            .append("reward", reward)
            .append("timestamp", System.currentTimeMillis())

        headBounty.replaceOne(
            Filters.eq("target", target),
            bountyDoc,
            ReplaceOptions().upsert(true)
        )
    }

    fun remove(target: String): Boolean {
        val result = headBounty.deleteOne(Filters.eq("target", target))
        return result.deletedCount > 0
    }
}