package ru.vasire.kafnetty.server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClientDto extends BaseDto implements ClientMessage {
    private UUID id;
    private String login;
    private String email;
    private String nickName;
    private String token;
    private UUID roomId;

    public ClientDto() {
        super(MESSAGE_TYPE.CLIENT, OPERATION_TYPE.NONE);
    }
}
