package ka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class HelloWorldProducer {
    public static void main(String[] args) {
        Properties kaProperties = new Properties();
        kaProperties.put("bootstrap.servers", "localhost:19092,localhost:29092,localhost:39092");
        kaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        try (Producer<String, String> producer =  new KafkaProducer<>(kaProperties)) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("kinaction_helloworld"
                    , null, "hello world again!");
            producer.send(producerRecord);
        }
    }
}
