package org.kafnetty.kafka.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TopicConfig {
    private final String messageTopic;
    private final String roomTopic;
    private final String clientTopic;
}
