package com.megustav.lolesports.schedule.configuration;

import freemarker.template.Configuration;
import no.api.freemarker.java8.Java8ObjectWrapper;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;

/**
 * Configuration for freemarker
 *
 * @author MeGustav
 *         02/04/2018 17:04
 */
@org.springframework.context.annotation.Configuration
public class FreemarkerConfiguration {

    /**
     * @return freemarker configuration bean
     */
    @Bean
    public Configuration configuration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setClassForTemplateLoading(FreemarkerConfiguration.class, "/template/");
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setObjectWrapper(new Java8ObjectWrapper(Configuration.getVersion()));
        return configuration;
    }

}
