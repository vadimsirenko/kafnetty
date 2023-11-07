package org.kafnetty.dto.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.dto.channel.*;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = KafkaMessageDto.class, name = "MESSAGE"),
        @JsonSubTypes.Type(value = KafkaClientDto.class, name = "CLIENT"),
        @JsonSubTypes.Type(value = KafkaRoomDto.class, name = "ROOM")
})
public abstract class KafkaBaseDto<T extends KafkaBaseDto> {
    private static ObjectMapper MAPPER = new ObjectMapper();
    private MESSAGE_TYPE messageType;
    private UUID kafkaMessageId = UUID.randomUUID();
    private String clusterId;

    private OPERATION_TYPE operationType = OPERATION_TYPE.NONE;
    private Long ts;

    public KafkaBaseDto(MESSAGE_TYPE messageType, OPERATION_TYPE operationType) {
        this.messageType = messageType;
        this.operationType = operationType;
        this.ts = System.currentTimeMillis();
    }

    public KafkaBaseDto() {
        this.messageType = MESSAGE_TYPE.UNKNOWN;
        this.operationType = OPERATION_TYPE.NONE;
        this.ts = System.currentTimeMillis();
    }

    public static KafkaBaseDto decode(String jsonMessage) {
        try {
            return MAPPER.readValue(jsonMessage, KafkaBaseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
