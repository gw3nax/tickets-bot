package ru.gw3nax.tickettrackerbot.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.gw3nax.tickettrackerbot.configuration.properties.KafkaConsumerProperties;
import ru.gw3nax.tickettrackerbot.dto.response.FlightResponse;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConsumerProperties kafkaProperties;

    @Bean
    public DefaultKafkaConsumerFactory<String, FlightResponse> kafkaConsumerFactory() {
        JsonDeserializer<FlightResponse> deserializer = new JsonDeserializer<>(FlightResponse.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        Map<String, Object> consumerProps = new HashMap<>();

        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServer());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.groupId());
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        consumerProps.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, kafkaProperties.allowAutoCreate());
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.enableAutoCommit());
        consumerProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, kafkaProperties.isolationLevel());
        consumerProps.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, RoundRobinAssignor.class.getName());
        consumerProps.put("security.protocol", "SASL_PLAINTEXT");
        consumerProps.put("sasl.mechanism", "PLAIN");
        consumerProps.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required "
                + "username=\"" + kafkaProperties.credential().username() + "\" password=\"" + kafkaProperties.credential().password()+ "\";");
        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FlightResponse> kafkaListener(
            DefaultKafkaConsumerFactory<String, FlightResponse> kafkaConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, FlightResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(kafkaConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

}

