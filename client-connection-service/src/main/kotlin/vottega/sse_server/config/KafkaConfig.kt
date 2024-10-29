package vottega.sse_server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.consumer")
class KafkaConfig {
    lateinit var bootstrapServers: String
    lateinit var groupId: String
    lateinit var keyDeserializer: String
    lateinit var valueDeserializer: String
    lateinit var autoOffsetReset: String
}