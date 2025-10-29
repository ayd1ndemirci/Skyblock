package redfox.skyblock.manager

import org.apache.logging.log4j.LogManager
import org.bson.Document
import redfox.skyblock.data.Auction
import redfox.skyblock.model.AuctionItem
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlinx.coroutines.*
import redfox.skyblock.model.CacheStats

object AuctionManager {

    private val logger = LogManager.getLogger(AuctionManager::class.java)
    private val cache = ConcurrentHashMap<Int, AuctionItem>()
    private val lock = ReentrantReadWriteLock()
    private val nextIdCounter = AtomicInteger(0)

    @Volatile
    private var isInitialized = false

    fun initialize() {
        if (isInitialized) {
            logger.warn("AuctionManager is already initialized!")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                logger.info("Starting auction cache initialization...")
                val startTime = System.currentTimeMillis()

                loadAuctionsFromDatabase()

                val endTime = System.currentTimeMillis()
                logger.info("Auction cache initialized successfully! Loaded ${cache.size} auctions in ${endTime - startTime}ms")
                isInitialized = true

            } catch (e: Exception) {
                logger.error("Failed to initialize auction cache", e)
            }
        }
    }

    private suspend fun loadAuctionsFromDatabase() = withContext(Dispatchers.IO) {
        val documents = Auction.getAll()
        var maxId = -1

        documents.forEach { doc ->
            val auctionItem = documentToAuctionItem(doc)
            cache[auctionItem.id] = auctionItem

            if (auctionItem.id > maxId) {
                maxId = auctionItem.id
            }
        }

        nextIdCounter.set(maxId + 1)
        logger.info("Loaded ${cache.size} auctions from database")
    }

    fun createAuction(player: String, price: Int, instantPrice: Int, itemBase64: String): Int {
        ensureInitialized()

        val id = nextIdCounter.getAndIncrement()
        val auctionItem = AuctionItem(
            id = id,
            player = player,
            price = price,
            instantPrice = instantPrice,
            item = itemBase64,
            createdAt = System.currentTimeMillis(),
            lastBidder = null
        )

        lock.write {
            cache[id] = auctionItem
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Auction.createAuction(player, price, instantPrice, itemBase64)
            } catch (e: Exception) {
                logger.error("Failed to save auction $id to database", e)
                lock.write {
                    cache.remove(id)
                }
            }
        }

        return id
    }

    fun getAuction(id: Int): AuctionItem? {
        ensureInitialized()
        return lock.read {
            cache[id]
        }
    }

    fun getAllAuctions(): List<AuctionItem> {
        ensureInitialized()
        return lock.read {
            cache.values.toList()
        }
    }

    fun getAuctionsByPlayer(player: String): List<AuctionItem> {
        ensureInitialized()
        return lock.read {
            cache.values.filter { it.player.equals(player, ignoreCase = true) }
        }
    }

    fun getAuctionsInPriceRange(minPrice: Int, maxPrice: Int): List<AuctionItem> {
        ensureInitialized()
        return lock.read {
            cache.values.filter { it.price in minPrice..maxPrice }
        }
    }

    fun setLastBidder(id: Int, player: String, newPrice: Int): Boolean {
        ensureInitialized()

        val updated = lock.write {
            val auction = cache[id]
            if (auction != null) {
                cache[id] = auction.copy(lastBidder = player, price = newPrice)
                true
            } else false
        }

        if (updated) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Auction.setLastBidder(id, player)
                } catch (e: Exception) {
                    logger.error("Failed to update auction $id in database", e)
                }
            }
        }

        return updated
    }

    fun deleteAuction(id: Int): Boolean {
        ensureInitialized()

        val removed = lock.write {
            cache.remove(id) != null
        }

        if (removed) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Auction.deleteAuction(id)
                } catch (e: Exception) {
                    logger.error("Failed to delete auction $id from database", e)
                }
            }
        }

        return removed
    }

    fun clearExpiredAuctions(timeoutMs: Long) {
        ensureInitialized()

        val now = System.currentTimeMillis()
        val expiredIds = mutableListOf<Int>()

        lock.read {
            cache.values.forEach { auction ->
                if ((now - auction.createdAt) > timeoutMs) {
                    expiredIds.add(auction.id)
                }
            }
        }

        if (expiredIds.isNotEmpty()) {
            lock.write {
                expiredIds.forEach { id ->
                    cache.remove(id)
                }
            }

            logger.info("Removed ${expiredIds.size} expired auctions from cache")

            // Async database cleanup
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Auction.clearExpiredAuctions(timeoutMs)
                } catch (e: Exception) {
                    logger.error("Failed to clear expired auctions from database", e)
                }
            }
        }
    }

    fun getCacheStats(): CacheStats {
        ensureInitialized()
        return lock.read {
            CacheStats(
                totalAuctions = cache.size,
                nextId = nextIdCounter.get(),
                isInitialized = isInitialized
            )
        }
    }

    fun refreshCache() {
        logger.info("Force refreshing auction cache...")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                lock.write {
                    cache.clear()
                }
                loadAuctionsFromDatabase()
                logger.info("Cache refreshed successfully")
            } catch (e: Exception) {
                logger.error("Failed to refresh cache", e)
            }
        }
    }

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("AuctionManager is not initialized! Call initialize() first.")
        }
    }

    private fun documentToAuctionItem(doc: Document): AuctionItem {
        return AuctionItem(
            id = doc.getInteger("_id"),
            player = doc.getString("player"),
            price = doc.getInteger("price"),
            instantPrice = doc.getInteger("instantPrice"),
            item = doc.getString("item"),
            createdAt = doc.getLong("createdAt"),
            lastBidder = doc.getString("lastBidder")
        )
    }
}