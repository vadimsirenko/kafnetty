package org.kafnetty.dto.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChannelInfoDto extends ChannelBaseDto {
    private final String messageText;

    public ChannelInfoDto(OPERATION_TYPE operationType, String messageText) {
        super(MESSAGE_TYPE.INFO, operationType);
        this.messageText = messageText;
    }

    public ChannelInfoDto() {
        super(MESSAGE_TYPE.INFO, OPERATION_TYPE.NONE);
        this.messageText = "";
    }

    public ChannelInfoDto(String messageText) {
        this(OPERATION_TYPE.NONE, messageText);
    }

    public static ChannelInfoDto createLogonInfo(String nickName) {
        return new ChannelInfoDto(OPERATION_TYPE.LOGON, nickName);
    }

    public static ChannelInfoDto createLogoffInfo(String nickName) {
        return new ChannelInfoDto(OPERATION_TYPE.LOGOFF, nickName);
    }
}
