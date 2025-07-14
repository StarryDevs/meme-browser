package starry.memebrowser.entity

import kotlinx.serialization.Serializable

@Serializable
data class MemeResponse(
    val data: List<Meme>,
    val total: Int,
    val page: Int,
    val limit: Int,
    val hasMore: Boolean
)
