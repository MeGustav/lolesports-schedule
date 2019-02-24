package com.megustav.lolesports.schedule.configuration

import freemarker.template.Configuration
import no.api.freemarker.java8.Java8ObjectWrapper
import org.springframework.context.annotation.Bean

import java.nio.charset.StandardCharsets

/**
 * @author MeGustav
 *         02/04/2018 17:04
 */
@org.springframework.context.annotation.Configuration
open class FreemarkerConfiguration {

    @Bean
    open fun configuration(): Configuration = Configuration(Configuration.VERSION_2_3_23).apply {
        setClassForTemplateLoading(FreemarkerConfiguration::class.java, "/template/")
        defaultEncoding = StandardCharsets.UTF_8.name()
        objectWrapper = Java8ObjectWrapper(Configuration.getVersion())
    }

}
