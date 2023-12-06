package org.kafnetty.store;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("org.kafnetty.store")
@EntityScan("org.kafnetty.store.entity")
@EnableJpaRepositories("org.kafnetty.store.repository")
public class EnableKafnettyStore {
}
