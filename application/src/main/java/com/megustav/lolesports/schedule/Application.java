package com.megustav.lolesports.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application entry point
 *
 * @author MeGustav
 *         14/11/2017 20:21
 */
@SpringBootApplication(exclude = {
        HibernateJpaAutoConfiguration.class
})
@ComponentScan("com.megustav.lolesports")
public class Application {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * Application entry point
     */
    public static void main(String[] args) {
        info("Starting app...");
        new SpringApplicationBuilder(Application.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
        info("App started");
    }

    /**
     * Print bootstrap information
     *
     * @param message message to print
     */
    private static void info(String message) {
        log.info(message);
        System.out.println(message);
    }
}
