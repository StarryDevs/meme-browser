package starry.memebrowser.configuration

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing
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
