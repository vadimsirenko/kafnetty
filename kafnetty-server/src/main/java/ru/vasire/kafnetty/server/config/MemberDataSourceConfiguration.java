package ru.vasire.kafnetty.server.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import ru.vasire.kafnetty.entity.ChatMessage;
import ru.vasire.kafnetty.entity.Client;
import ru.vasire.kafnetty.entity.Room;

import javax.sql.DataSource;
@Configuration
@EnableJpaRepositories(basePackages = "ru.vasire.kafnetty.server.repository",
        entityManagerFactoryRef = "memberEntityManagerFactory",
        transactionManagerRef= "memberTransactionManager")
public class MemberDataSourceConfiguration {
    @Bean
    @ConfigurationProperties("app.datasource.member")
    public DataSourceProperties chatMessageDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.member.configuration")
    public DataSource memberDataSource() {
        return chatMessageDataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager memberTransactionManager(
            final @Qualifier("memberEntityManagerFactory") LocalContainerEntityManagerFactoryBean memberEntityManagerFactory) {
        return new JpaTransactionManager(memberEntityManagerFactory.getObject());
    }

    @Bean(name = "memberEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean chatMessageEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(memberDataSource())
                .packages(ChatMessage.class, Room.class, Client.class)
                .build();
    }
}
