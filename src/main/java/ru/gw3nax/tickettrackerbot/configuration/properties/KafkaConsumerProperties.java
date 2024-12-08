package ru.gw3nax.tickettrackerbot.configuration.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "kafka-consumer")
public record KafkaConsumerProperties(
        @NotEmpty
        String bootstrapServer,
        @NotNull
        Integer fetchMaxByte,
        @NotNull
        Integer maxPollRecords,
        @NotNull
        Integer maxPollInterval,
        @NotNull
        Boolean enableAutoCommit,
        @NotEmpty
        String isolationLevel,
        @NotNull
        Boolean allowAutoCreate,
        @NotEmpty
        String groupId,
        TopicProp topicProp
) {

    public record TopicProp(
            @NotEmpty
            String name,
            @NotNull
            Integer partitions,
            @NotNull
            Integer replicas
    ) {

    }
}



