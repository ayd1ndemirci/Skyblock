package redfox.skyblock.group

data class Group(
    val name: String,
    val id: String,
    val nameTagFormat: String,
    val chatFormat: String,
    val aliases: List<String>,
    val permissions: List<String>,
    val inheritance: List<String>
) {
    fun getAllPermissions(): List<String> {
        val perms = permissions.toMutableList()
        inheritance.forEach { inherit ->
            GroupManager.getGroup(inherit)?.getAllPermissions()?.let { perms.addAll(it) }
        }
        return perms
    }
}