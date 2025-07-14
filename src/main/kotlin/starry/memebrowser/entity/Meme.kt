package starry.memebrowser.entity

import kotlinx.serialization.Serializable

@Serializable
data class Meme(
    val id: String,
    val title: String,
    val imageUrl: String,
    val originalText: String,
    val recognizedText: String? = null,
    val content: String,
    val tags: Set<String>,
    val createdAt: String
) {
    fun tags(vararg newTags: String) = copy(tags = tags + newTags)
}
