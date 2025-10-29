package redfox.skyblock.model

data class ClaimData(
    val date: String,
    val rewardIndex: Int,
    var claimed: Boolean
)