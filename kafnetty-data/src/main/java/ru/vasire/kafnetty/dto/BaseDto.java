package ru.vasire.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.vasire.kafnetty.types.MESSAGE_TYPE;
import ru.vasire.kafnetty.types.OPERATION_TYPE;

@Getter
@Setter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChatMessageDto.class, name = "MESSAGE"),
        @JsonSubTypes.Type(value = MessageListDto.class, name = "MESSAGE_LIST"),
        @JsonSubTypes.Type(value = UserProfileDto.class, name = "USER_PROFILE"),
        @JsonSubTypes.Type(value = RoomDto.class, name = "ROOM"),
        @JsonSubTypes.Type(value = RoomListDto.class, name = "ROOM_LIST"),
        @JsonSubTypes.Type(value = ClientDto.class, name = "CLIENT"),
        @JsonSubTypes.Type(value = ErrorDto.class, name = "ERROR"),
        @JsonSubTypes.Type(value = InfoDto.class, name = "INFO")
})
public abstract class BaseDto<T extends BaseDto> {
    private MESSAGE_TYPE messageType;
    private OPERATION_TYPE operationType = OPERATION_TYPE.NONE;
    private Long ts;

    private static ObjectMapper MAPPER = new ObjectMapper();

    public BaseDto(MESSAGE_TYPE messageType, OPERATION_TYPE operationType) {
        this.messageType = messageType;
        this.operationType = operationType;
        this.ts = System.currentTimeMillis();
    }

    public BaseDto() {
        this.messageType = MESSAGE_TYPE.UNKNOWN;
        this.operationType = OPERATION_TYPE.NONE;
        this.ts = System.currentTimeMillis();
    }

    public String toJson(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static BaseDto decode(String jsonMessage){
        try {
            return MAPPER.readValue(jsonMessage, BaseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
