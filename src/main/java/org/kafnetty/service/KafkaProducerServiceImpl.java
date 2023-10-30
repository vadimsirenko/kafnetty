package org.kafnetty.service;

import org.kafnetty.dto.kafka.KafkaMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

    @Override
    public void sendMessage(KafkaMessageDto kafkaMessageDto) {
        log.info("send message: " + kafkaMessageDto.getId());
    }
}
