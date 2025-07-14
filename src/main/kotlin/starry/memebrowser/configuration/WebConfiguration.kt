package starry.memebrowser.configuration

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import starry.auxframework.context.annotation.Configuration
import starry.auxframework.web.service.WebConfigurator

@Configuration
class WebConfiguration : WebConfigurator {

    override fun configure(application: Application): Unit = application.run {
        routing {
            staticResources("/", "public")
        }
    }

}
