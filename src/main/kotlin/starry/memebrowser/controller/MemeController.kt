package starry.memebrowser.controller

import io.ktor.http.*
import io.ktor.server.response.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import starry.auxframework.web.annotation.PathVariable
import starry.auxframework.web.annotation.RequestMapping
import starry.auxframework.web.annotation.RequestParam
import starry.auxframework.web.annotation.RestController
import starry.auxframework.web.handler.IRoutingContext
import starry.memebrowser.service.MemeService

@RestController
class MemeController(private val memeService: MemeService) {

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " " .repeat(4)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @RequestMapping("/memes")
    suspend fun IRoutingContext.memes(
        @RequestParam page: Int = 1,
        @RequestParam limit: Int = 20
    ) {
        if (page < 1) {
            call.respond(status = HttpStatusCode.BadRequest, message = "请求参数必须满足 page >= 1")
            return
        }
        if (limit < 1) {
            call.respond(status = HttpStatusCode.BadRequest, message = "请求参数必须满足 limit >= 1")
            return
        }
        call.respondOutputStream(ContentType.Application.Json) {
            json.encodeToStream(memeService.getMemes(page, limit), this)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @RequestMapping("/memes/search")
    suspend fun IRoutingContext.search(
        @RequestParam page: Int = 1,
        @RequestParam limit: Int = 20,
        @RequestParam query: String? = null,
        @RequestParam tag: String? = null
    ) {
        val tags = call.request.queryParameters.getAll("tags[]") ?: emptyList()
        call.respondOutputStream(ContentType.Application.Json) {
            json.encodeToStream(memeService.searchMemes(page, limit, query, tag, tags), this)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    @RequestMapping("/memes/image/{name}")
    suspend fun IRoutingContext.search(
        @PathVariable name: String
    ) {
        val file = memeService.imageDirectory.resolve(name)
        if (!file.isFile) {
            call.respond(HttpStatusCode.NotFound, "资源未找到")
            return
        }
        file.inputStream().use { inputStream ->
            call.respondOutputStream(ContentType.defaultForFile(file)) {
                inputStream.copyTo(this)
            }
        }
    }


}
