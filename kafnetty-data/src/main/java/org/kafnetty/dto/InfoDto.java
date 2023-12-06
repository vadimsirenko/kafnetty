package org.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kafnetty.type.OperationType;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InfoDto extends BaseDto {
    private final String messageText;

    public InfoDto(OperationType operationType, String messageText) {
        super(MessageType.INFO, operationType);
        this.messageText = messageText;
    }

    public InfoDto() {
        super(MessageType.INFO, OperationType.NONE);
        this.messageText = "";
    }

    public static InfoDto createLogonInfo(String nickName) {
        return new InfoDto(OperationType.LOGON, nickName);
    }

    public static InfoDto createLogoffInfo(String nickName) {
        return new InfoDto(OperationType.LOGOFF, nickName);
    }
}
