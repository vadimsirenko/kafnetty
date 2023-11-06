package org.kafnetty.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserProfileDto {
    private UUID id;
    private String login;
    private String nickName;
    private String email;
    @JsonIgnoreProperties
    private UUID roomId;
}