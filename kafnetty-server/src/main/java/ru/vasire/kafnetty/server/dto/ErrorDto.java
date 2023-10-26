package ru.vasire.kafnetty.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.vasire.kafnetty.server.types.MESSAGE_TYPE;
import ru.vasire.kafnetty.server.types.OPERATION_TYPE;

@Data
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
