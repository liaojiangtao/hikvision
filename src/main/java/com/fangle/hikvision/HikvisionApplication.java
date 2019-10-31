package com.fangle.hikvision;

import akka.actor.ActorSystem;
import com.fangle.hikvision.extension.SpringExtension;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.fangle.hikvision.**"})
@SpringBootApplication
@ImportAutoConfiguration(value = {EhomeConfig.class})
public class HikvisionApplication {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * spring 集成Akka
     *
     * @return
     */
    @Bean
    public SpringExtension springExtension() {
        return new SpringExtension();
    }

    /**
     * Akka配置
     *
     * @return
     */
    @Bean
    public Config akkaConfiguration() {
        return ConfigFactory.load();
    }

    /**
     * Actor system singleton for this application.
     */
    @Bean
    public ActorSystem actorSystem() {

        ActorSystem system = ActorSystem.create("AkkaTaskProcessing", akkaConfiguration());
        springExtension().initialize(applicationContext);
        return system;
    }

    public static void main(String[] args) {
        SpringApplication.run(HikvisionApplication.class, args);
    }

}
