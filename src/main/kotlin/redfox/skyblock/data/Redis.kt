package redfox.skyblock.data

import redis.clients.jedis.JedisPool

object Redis {
    private val jedisPool = JedisPool("localhost", 6379)

    fun getPlayerServer(playerName: String): String? {
        jedisPool.resource.use { jedis ->
            return jedis.get("player:${playerName.lowercase()}:server")
        }
    }

    fun setPlayerServer(playerName: String, serverName: String) {
        jedisPool.resource.use { jedis ->
            jedis.set("player:${playerName.lowercase()}:server", serverName)
        }
    }

    fun deletePlayerServer(playerName: String) {
        jedisPool.resource.use { jedis ->
            jedis.del("player:${playerName.lowercase()}:server")
        }
    }

    fun incrementPlayerCount() {
        jedisPool.resource.use { jedis ->
            jedis.incr("active_players")
        }
    }

    fun decrementPlayerCount() {
        jedisPool.resource.use { jedis ->
            jedis.decr("active_players")
        }
    }

    fun getPlayerCount(): Long {
        jedisPool.resource.use { jedis ->
            val count = jedis.get("active_players")
            return count?.toLongOrNull() ?: 0L
        }
    }
    fun setPlayerCount(value: Long) {
        jedisPool.resource.use { jedis ->
            jedis.set("active_players", value.toString())
        }
    }

    fun safeDecrementPlayerCount() {
        jedisPool.resource.use { jedis ->
            val count = jedis.get("active_players")?.toLongOrNull() ?: 0L
            if (count > 0) {
                jedis.decr("active_players")
            }
        }
    }
}
