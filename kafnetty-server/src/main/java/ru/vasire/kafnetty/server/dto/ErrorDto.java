package ru.vasire.kafnetty.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDto extends BaseDto {
    private final Integer errorCode;
    private final String errorMessage;
    public ErrorDto(int errorCode, String errorMessage){
        super(MESSAGE_TYPE.ERROR, OPERATION_TYPE.NONE);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorDto createCommonError(String errorMessage){
        return new ErrorDto(1001, errorMessage);
    }
}
