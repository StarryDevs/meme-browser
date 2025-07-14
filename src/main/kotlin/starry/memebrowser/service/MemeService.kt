package starry.memebrowser.service

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import me.xdrop.fuzzywuzzy.FuzzySearch
import starry.auxframework.context.annotation.Value
import starry.auxframework.context.annotation.stereotype.Service
import starry.memebrowser.entity.Meme
import starry.memebrowser.entity.MemeResponse
import java.io.File
import java.nio.file.Path

@Service
class MemeService(@Value("@{user.dir:\"./\"}") userDir: String) {

    val directory: File = Path.of(userDir, ".memes").toFile().apply(File::mkdirs)
    val memeDirectory: File = directory.resolve("memes").apply(File::mkdirs)
    val imageDirectory: File = directory.resolve("images").apply(File::mkdirs)

    fun getMemeIndices() = memeDirectory.listFiles()
        .filter { it.name.endsWith(".json") }
        .mapNotNull { it.nameWithoutExtension.toIntOrNull() }
        .sorted()

    @OptIn(ExperimentalSerializationApi::class)
    fun getMeme(id: Int) = memeDirectory.resolve("$id.json").inputStream().use {
        Json.decodeFromStream<Meme>(it)
    }.tags("$$id")

    @OptIn(ExperimentalSerializationApi::class)
    fun getMemes(range: List<Int>) = sequence {
        for (index in range) {
            try {
                yield(getMeme(index))
            } catch (_: Throwable) {
            }
        }
    }

    fun getMemes(page: Int, limit: Int): MemeResponse {
        val start = (page - 1) * limit
        val end = (page - 1) * limit + limit
        val indices = getMemeIndices()
        val data = getMemes(indices.drop(start).take(limit)).toList()
        return MemeResponse(
            data,
            indices.size,
            page,
            limit,
            end < indices.size
        )
    }

    fun searchMemes(
        page: Int = 1,
        limit: Int = 20,
        query: String? = null,
        tag: String? = null,
        tags: List<String>
    ): MemeResponse {
        val indices = getMemeIndices()
        val indexed = getMemes(indices).filter {
            (tag == null || tag in it.tags) && tags.all { tag -> tag in it.tags }
        }.filter {
            query == null || FuzzySearch.ratio(query, it.title) > 0.95
        }.withIndex()
        val start = (page - 1) * limit
        val end = (page - 1) * limit + limit
        val result = mutableListOf<Meme>()
        var size = 0
        for ((index, meme) in indexed) {
            size++
            if (index in start..end) {
                result += meme
            }
        }
        return MemeResponse(
            result,
            size,
            page,
            limit,
            end < size
        )
    }

}
