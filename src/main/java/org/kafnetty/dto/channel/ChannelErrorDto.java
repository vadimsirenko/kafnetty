package org.kafnetty.dto.channel;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.kafnetty.type.MESSAGE_TYPE;
import org.kafnetty.type.OPERATION_TYPE;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ChannelErrorDto extends ChannelBaseDto {
    private final Integer errorCode;
    private final String errorMessage;

    public ChannelErrorDto(int errorCode, String errorMessage) {
        super(MESSAGE_TYPE.ERROR, OPERATION_TYPE.NONE);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ChannelErrorDto() {
        this(1001, "");
    }

    public static ChannelErrorDto createCommonError(String errorMessage) {
        return new ChannelErrorDto(1001, errorMessage);
    }
}
