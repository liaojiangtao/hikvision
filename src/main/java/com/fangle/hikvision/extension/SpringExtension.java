package com.fangle.hikvision.extension;

import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;

/**
 * Extension to tell Akka how to create beans via Spring.
 */
public class SpringExtension implements Extension {

    private ApplicationContext applicationContext;

    /**
     * Used to initialize the Spring application context for the extension.
     */
    public void initialize(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Create a Props for the specified actorBeanName using the SpringActorProducer
     * class.
     */
    public Props props(String actorBeanName, Object arg0) {
        return Props.create(SpringActorProducer.class, applicationContext, actorBeanName, arg0);
    }

    public Props props(String actorBeanName) {
        return Props.create(SpringActorProducer.class, applicationContext, actorBeanName);
    }
}