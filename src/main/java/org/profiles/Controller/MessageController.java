package org.profiles.Controller;

import org.profiles.Model.Message;
import org.profiles.Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Get messages by profile
    @GetMapping
    public List<Message> getMessages() {
        System.out.println("Active profile: " + activeProfile.trim());
        return messageRepository.findAllByProfileName(activeProfile.trim());


    }

    // Post new message
    @PostMapping
    public Message addMessage(@RequestBody Message message) {
        message.setProfileName(activeProfile.trim()); // force profile
        return messageRepository.save(message);
    }

    // Update message by ID
    @PutMapping("/{id}")
    public Message updateMessage(@PathVariable int id, @RequestBody Message updatedMessage) {
        Message existing = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        existing.setMessage(updatedMessage.getMessage());
        return messageRepository.save(existing);
    }

}
