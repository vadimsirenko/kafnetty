package org.kafnetty.dto;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;

@Getter
@Setter
public class TokenDto extends BaseDto {
    private String token;

    public TokenDto(String token) {
        super(MessageType.TOKEN, OperationType.CREATE);
        this.token = token;
    }
}
