package org.onegang.access;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.onegang.access.entity.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;


@Configuration
@Profile("kafka")
public class KafkaConfig {
	
	public static final String TOPIC_APPROVAL = "ACCESS_APPROVAL";
	
	public static final String TOPIC_IMPLEMENTATION = "ACCESS_IMPLEMENTATION";
	

	@Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
 
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }
     
    @Bean
    public NewTopic approvalTopic() {
         return new NewTopic(TOPIC_APPROVAL, 1, (short) 1);
    }
    
    @Bean
    public NewTopic implementationTopic() {
         return new NewTopic(TOPIC_IMPLEMENTATION, 1, (short) 1);
    }
    
    @Bean
    public ProducerFactory<String, Request> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
          ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
          bootstrapAddress);
        configProps.put(
          ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, 
          StringSerializer.class);  
        configProps.put(
        	      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
        	      JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, Request> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
