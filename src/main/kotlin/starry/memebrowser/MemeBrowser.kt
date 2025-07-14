package starry.memebrowser

import starry.auxframework.application.AuxApplicationBuilder
import starry.auxframework.context.bean.ApplicationListener
import starry.auxframework.util.getLogger

class MemeBrowser : ApplicationListener {

    val logger = getLogger()

    override fun finishLoading() {
        logger.info("Loaded!")
    }

}

fun main(args: Array<String>) {
    AuxApplicationBuilder()
        .applicationClass(MemeBrowser::class)
        .basePackages("starry.memebrowser")
        .build()
        .start(args)
}
