package redfox.skyblock.model

data class AuctionItem(
    val id: Int,
    val player: String,
    val price: Int,
    val instantPrice: Int,
    val item: String, // Base64 encoded item
    val createdAt: Long,
    val lastBidder: String?
) {
    fun isExpired(timeoutMs: Long): Boolean {
        return (System.currentTimeMillis() - createdAt) > timeoutMs
    }

    fun hasInstantPrice(): Boolean = instantPrice > 0

    fun getTimeRemaining(timeoutMs: Long): Long {
        val elapsed = System.currentTimeMillis() - createdAt
        return maxOf(0, timeoutMs - elapsed)
    }
}