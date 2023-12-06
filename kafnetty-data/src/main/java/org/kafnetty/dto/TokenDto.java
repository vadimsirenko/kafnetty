package org.kafnetty.dto;

import org.kafnetty.type.OperationType;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;

@Getter
@Setter
public class TokenDto extends BaseDto {
    private String token;

    public TokenDto(String token) {
        super(MessageType.TOKEN, OperationType.CREATE);
        this.token = token;
    }
}
