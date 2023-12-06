package org.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.kafnetty.type.MessageType;
import org.kafnetty.type.OperationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDto extends BaseDto {
    private final Integer errorCode;
    private final String errorMessage;

    public ErrorDto(int errorCode, String errorMessage) {
        super(MessageType.ERROR, OperationType.NONE);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorDto() {
        this(1001, "");
    }

    public static ErrorDto createCommonError(String errorMessage) {
        return new ErrorDto(1001, errorMessage);
    }
}
