package com.dijul.demo.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name("order.created")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentCompletedTopic(){
        return TopicBuilder.name("payment.completed")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic shippingReadyTopic() {
        return TopicBuilder.name("order.ready_for_shipping")
                .partitions(1)
                .replicas(1)
                .build();
    }

}
