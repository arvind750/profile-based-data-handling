package org.profiles.Controller;

import jakarta.validation.Valid;
import org.profiles.Model.Message;
import org.profiles.Repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final MessageRepository messageRepository;
    private final KafkaTemplate<String, Message> kafkaTemplate;

    @Value("${app.kafka.topic:messages}")
    private String topicName;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public MessageController(MessageRepository messageRepository, KafkaTemplate<String, Message> kafkaTemplate) {
        this.messageRepository = messageRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getMessages() {
        if (activeProfile == null || activeProfile.trim().isEmpty()) {
            throw new IllegalStateException("Active profile is not set. Please specify spring.profiles.active.");
        }
        String profile = activeProfile.trim();
        logger.info("Fetching messages for active profile: {}", profile);
        List<Message> messages = messageRepository.findAllByProfileName(profile);
        if (messages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(messages);
        }
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public Message addMessage(@Valid @RequestBody Message message) {
        if (activeProfile == null || activeProfile.trim().isEmpty()) {
            throw new IllegalStateException("Active profile is not set.");
        }
        String profile = activeProfile.trim();
        message.setProfileName(profile);
        Message saved = messageRepository.save(message);
        kafkaTemplate.send(topicName, saved); // Send to Kafka
        logger.info("Produced to Kafka topic [{}]: {}", topicName, saved);
        return saved;
    }

    @PutMapping("/{id}")
    public Message updateMessage(@PathVariable int id, @Valid @RequestBody Message updatedMessage) {
        if (activeProfile == null || activeProfile.trim().isEmpty()) {
            throw new IllegalStateException("Active profile is not set.");
        }
        Message existing = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message with ID " + id + " not found"));
        existing.setMessage(updatedMessage.getMessage());
        return messageRepository.save(existing);
    }

    @PutMapping("/age")
    public Message updateAge(@Valid @RequestBody Message updatedMessage) {
        if (activeProfile == null || activeProfile.trim().isEmpty()) {
            throw new IllegalStateException("Active profile is not set.");
        }
        Message existing = messageRepository.findById(updatedMessage.getId())
                .orElseThrow(() -> new IllegalArgumentException("Message with ID " + updatedMessage.getId() + " not found"));
        existing.setAge(updatedMessage.getAge());
        return messageRepository.save(existing);
    }
}
