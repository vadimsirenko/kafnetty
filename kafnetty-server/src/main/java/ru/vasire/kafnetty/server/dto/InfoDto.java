package ru.vasire.kafnetty.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import ru.vasire.kafnetty.server.types.MESSAGE_TYPE;
import ru.vasire.kafnetty.server.types.OPERATION_TYPE;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InfoDto extends BaseDto {
    private final String messageText;
    public InfoDto(OPERATION_TYPE operationType, String messageText){
        super(MESSAGE_TYPE.INFO, operationType);
        this.messageText = messageText;
    }

    public InfoDto() {
        super(MESSAGE_TYPE.INFO, OPERATION_TYPE.NONE);
        this.messageText = "";
    }
    public InfoDto(String messageText) {
        this(OPERATION_TYPE.NONE, messageText);
    }

    public static InfoDto createLogonInfo (String nickName){
        return new InfoDto(OPERATION_TYPE.LOGON, nickName);
    }

    public static InfoDto createLogoffInfo (String nickName){
        return new InfoDto(OPERATION_TYPE.LOGOFF, nickName);
    }
}
