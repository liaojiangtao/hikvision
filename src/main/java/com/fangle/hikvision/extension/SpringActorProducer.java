package com.fangle.hikvision.extension;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

/**
 * An actor producer that lets Spring create the Actor instances.
 */
public class SpringActorProducer implements IndirectActorProducer {

    private final ApplicationContext applicationContext;
    private final String actorBeanName;
    private Object arg0;

    public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
    }

    public SpringActorProducer(ApplicationContext applicationContext, String actorBeanName, Object arg0) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
        this.arg0 = arg0;
    }

    @Override
    public Actor produce() {
        return arg0 != null ? (Actor) applicationContext.getBean(actorBeanName, arg0)
                : (Actor) applicationContext.getBean(actorBeanName);
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }
}