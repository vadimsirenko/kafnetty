package org.kafnetty.service;

import org.kafnetty.dto.BaseDto;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface KafnettyProducer {
    CompletableFuture<SendResult<UUID, BaseDto>> sendMessage(BaseDto baseDto, Consumer<BaseDto> successCallback);
}
