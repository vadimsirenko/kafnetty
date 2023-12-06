package org.kafnetty.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OperationType {
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
