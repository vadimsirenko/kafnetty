package ru.vasire.kafnetty.types;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OPERATION_TYPE {
    @JsonProperty("CREATE")
    CREATE,
    @JsonProperty("UPDATE")
    UPDATE,
    @JsonProperty("DELETE")
    DELETE,
    @JsonProperty("RECEIVE")
    RECEIVE,
    @JsonProperty("NONE")
    NONE,
    @JsonProperty("LOGOFF")
    LOGOFF,
    @JsonProperty("LOGON")
    LOGON
}
