package ru.vasire.kafnetty.server.service.message;

import org.springframework.stereotype.Service;
import ru.vasire.kafnetty.server.dto.InfoDto;
import ru.vasire.kafnetty.server.dto.OPERATION_TYPE;

@Service
public final class InfoService {
    public static InfoDto getLogoffInfo(String messageText) {
        return new InfoDto(OPERATION_TYPE.LOGOFF, messageText);
    }

    public static InfoDto getLogonInfo(String nickName) {
        return new InfoDto(OPERATION_TYPE.LOGON, nickName);
    }
}


