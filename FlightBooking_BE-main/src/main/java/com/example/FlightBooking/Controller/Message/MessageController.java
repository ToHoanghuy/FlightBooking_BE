package com.example.FlightBooking.Controller.Message;

import com.example.FlightBooking.Models.Message;
import com.example.FlightBooking.Repositories.MessageRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin
@Tag(name="Message Controller")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    // Get all messages
    @GetMapping
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    // Get messages between specific sender and receiver
    @GetMapping("/between/{senderId}/{receiverId}")
    public List<Message> getMessagesBetween(@PathVariable String senderId, @PathVariable String receiverId) {
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }

    // Get messages by receiver ID
    @GetMapping("/receiver/{receiverId}")
    public List<Message> getMessagesByReceiver(@PathVariable String receiverId) {
        return messageRepository.findByReceiverId(receiverId);
    }

    // Get chat history between two users
    @GetMapping("/history/{userId1}/{userId2}")
    public List<Message> getChatHistory(@PathVariable String userId1, @PathVariable String userId2) {
        List<Message> messagesFromUser1ToUser2 = messageRepository.findBySenderIdAndReceiverId(userId1, userId2);
        List<Message> messagesFromUser2ToUser1 = messageRepository.findBySenderIdAndReceiverId(userId2, userId1);
        messagesFromUser1ToUser2.addAll(messagesFromUser2ToUser1);
        messagesFromUser1ToUser2.sort((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()));
        return messagesFromUser1ToUser2;
    }

    // Get latest message for each user
    @GetMapping("/latest")
    public List<Message> getLatestMessages() {
        List<Message> allMessages = messageRepository.findAll();
        Map<String, Message> latestMessagesMap = allMessages.stream()
                .collect(Collectors.groupingBy(
                        Message::getReceiverId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt())),
                                optionalMessage -> optionalMessage.orElse(null)
                        )
                ));
        return latestMessagesMap.values().stream().collect(Collectors.toList());
    }

}
