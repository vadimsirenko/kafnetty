package ru.vasire.kafnetty.server.kafka;

public class ConsumerException extends RuntimeException {
    public ConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}
