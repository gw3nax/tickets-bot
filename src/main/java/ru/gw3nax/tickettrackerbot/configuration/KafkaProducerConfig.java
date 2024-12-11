package ru.gw3nax.tickettrackerbot.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.gw3nax.tickettrackerbot.configuration.properties.KafkaProducerProperties;
import ru.gw3nax.tickettrackerbot.dto.request.FlightRequest;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProducerProperties kafkaProperties;

    @Bean
    public DefaultKafkaProducerFactory<String, FlightRequest> kafkaProducerFactory() {
        Map<String, Object> prop = new HashMap<>();

        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServer());

        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        prop.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProperties.batchSize());
        prop.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, kafkaProperties.maxRequestSize());
        prop.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProperties.lingerMs());

        prop.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.acks());

        prop.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class);
        prop.put("security.protocol", "SASL_PLAINTEXT");
        prop.put("sasl.mechanism", "PLAIN");
        prop.put("sasl.jaas.config", String.format(
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";",
                kafkaProperties.credential().username().trim(),
                kafkaProperties.credential().password().trim()));
        return new DefaultKafkaProducerFactory<>(prop);
    }

    @Bean
    public KafkaTemplate<String, FlightRequest> kafkaTemplate(DefaultKafkaProducerFactory<String, FlightRequest> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }
}

