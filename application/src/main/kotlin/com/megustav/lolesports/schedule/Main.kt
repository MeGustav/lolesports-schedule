package com.megustav.lolesports.schedule

import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.ApplicationPidFileWriter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan

/**
 * @author MeGustav
 *         2019-02-24 14:11
 */
private val log = LoggerFactory.getLogger(Application::class.java)

@SpringBootApplication(exclude = [HibernateJpaAutoConfiguration::class])
@ComponentScan("com.megustav.lolesports")
@EnableConfigurationProperties
open class Application

fun main(args: Array<String>) {
    log.info("Starting app...")
    SpringApplicationBuilder(Application::class.java)
            .bannerMode(Banner.Mode.OFF)
            .listeners(ApplicationPidFileWriter("application.pid"))
            .run(*args)
    log.info("App started")
}
