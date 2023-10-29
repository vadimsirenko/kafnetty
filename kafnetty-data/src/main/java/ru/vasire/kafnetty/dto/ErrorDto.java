package ru.vasire.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.vasire.kafnetty.types.MESSAGE_TYPE;
import ru.vasire.kafnetty.types.OPERATION_TYPE;

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

    public ErrorDto() {
        this(1001, "");
    }
}
