package com.proyecto.GestionCursos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

 public static final String EXCHANGE_NAME = "exchange.instructores";
    public static final String QUEUE_NAME = "usuarios.instructores.creados";
    public static final String ROUTING_KEY = "usuarios.instructores.creados";

    @Bean
    public TopicExchange instructoresExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue instructoresQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding bindingInstructores(Queue instructoresQueue, TopicExchange instructoresExchange) {
        return BindingBuilder
                .bind(instructoresQueue)
                .to(instructoresExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
