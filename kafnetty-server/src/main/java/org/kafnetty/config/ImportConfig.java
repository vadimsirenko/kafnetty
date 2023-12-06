package org.kafnetty.config;

import org.kafnetty.EnableKafnettyService;
import org.kafnetty.netty.EnableKafnettyNettyComponents;
import org.kafnetty.store.EnableKafnettyStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Import({
        EnableKafnettyStore.class,
        EnableKafnettyService.class,
        EnableKafnettyNettyComponents.class
})
@Configuration
@EnableScheduling
public class ImportConfig {
}
