package org.kafnetty.dto.channel;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
public class ChannelClientDto extends ChannelBaseDto {
    private UUID id;
    private String login;
    private String email;
    private String nickName;
    private String token;
    private UUID roomId;

    public ChannelClientDto() {
        super(MESSAGE_TYPE.CLIENT, OPERATION_TYPE.NONE);
    }
}
