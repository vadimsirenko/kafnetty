package org.kafnetty.dto;

import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;

@Getter
@Setter
public class UserDto extends BaseDto {
    private String email;
    private String full_name;
    private String nickName;
    private String password;
    private String clusterId;

    public UserDto() {
        super(MessageType.USER, OperationType.NONE);
    }
}
