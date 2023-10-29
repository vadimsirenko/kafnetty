package ru.vasire.kafnetty.dto;

import lombok.Getter;
import lombok.Setter;
import ru.vasire.kafnetty.types.MESSAGE_TYPE;
import ru.vasire.kafnetty.types.OPERATION_TYPE;

import java.util.UUID;

@Getter
@Setter
public class ClientDto extends BaseDto {
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
