package org.kafnetty.type;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MessageType {
    @JsonProperty("MESSAGE")
    MESSAGE,
    @JsonProperty("MESSAGE_LIST")
    MESSAGE_LIST,
    @JsonProperty("ROOM")
    ROOM,
    @JsonProperty("ROOM_LIST")
    ROOM_LIST,
    @JsonProperty("USER")
    USER,
    @JsonProperty("TOKEN")
    TOKEN,
    @JsonProperty("INFO")
    INFO,
    @JsonProperty("ERROR")
    ERROR,
    @JsonProperty("UNKNOWN")
    UNKNOWN
}
