package org.profiles.Kafka;

import org.profiles.Model.Message;
import org.profiles.Repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    private MessageRepository messageRepository;

    @KafkaListener(topics = "${app.kafka.topic:messages}", groupId = "message_group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(Message message) {
        logger.info("Consumed Kafka message: {}", message);
        messageRepository.save(message);
    }
}
