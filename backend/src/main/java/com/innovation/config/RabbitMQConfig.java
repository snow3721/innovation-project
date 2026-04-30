package com.innovation.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ========== Exchange ==========
    public static final String MSG_EXCHANGE = "innovation.msg.exchange";

    // ========== Queue ==========
    public static final String QUEUE_SYSTEM  = "innovation.msg.system";
    public static final String QUEUE_AUDIT   = "innovation.msg.audit";
    public static final String QUEUE_REVIEW  = "innovation.msg.review";
    public static final String QUEUE_MILESTONE = "innovation.msg.milestone";
    public static final String QUEUE_ACHIEVEMENT = "innovation.msg.achievement";

    // ========== Routing Key ==========
    public static final String RK_SYSTEM  = "msg.system";
    public static final String RK_AUDIT   = "msg.audit";
    public static final String RK_REVIEW  = "msg.review";
    public static final String RK_MILESTONE = "msg.milestone";
    public static final String RK_ACHIEVEMENT = "msg.achievement";

    @Bean
    public TopicExchange msgExchange() {
        return ExchangeBuilder.topicExchange(MSG_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue queueSystem()  { return QueueBuilder.durable(QUEUE_SYSTEM).build(); }
    @Bean
    public Queue queueAudit()   { return QueueBuilder.durable(QUEUE_AUDIT).build(); }
    @Bean
    public Queue queueReview()  { return QueueBuilder.durable(QUEUE_REVIEW).build(); }
    @Bean
    public Queue queueMilestone()   { return QueueBuilder.durable(QUEUE_MILESTONE).build(); }
    @Bean
    public Queue queueAchievement() { return QueueBuilder.durable(QUEUE_ACHIEVEMENT).build(); }

    @Bean
    public Binding bindSystem()  { return BindingBuilder.bind(queueSystem()).to(msgExchange()).with(RK_SYSTEM); }
    @Bean
    public Binding bindAudit()   { return BindingBuilder.bind(queueAudit()).to(msgExchange()).with(RK_AUDIT); }
    @Bean
    public Binding bindReview()  { return BindingBuilder.bind(queueReview()).to(msgExchange()).with(RK_REVIEW); }
    @Bean
    public Binding bindMilestone()   { return BindingBuilder.bind(queueMilestone()).to(msgExchange()).with(RK_MILESTONE); }
    @Bean
    public Binding bindAchievement() { return BindingBuilder.bind(queueAchievement()).to(msgExchange()).with(RK_ACHIEVEMENT); }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
