package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import com.example.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountService accountService;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountService accountService) {
        this.messageRepository = messageRepository;
        this.accountService = accountService;
    }

    public Message createMessage(Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text must not be blank or over 255 characters");
        }
        Optional<Account> account = accountService.getAccountById(message.getPostedBy());
        if (account.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account does not exist");
        }
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }

    public int deleteMessage(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return 1;
        }
        return 0;
    }

    public int updateMessageText(Integer messageId, String messageText) {
        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            Message message = messageOptional.get();
            if (messageText == null || messageText.isBlank() || messageText.length() > 255) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text must not be blank or over 255 characters");
            }
            message.setMessageText(messageText);
            messageRepository.save(message);
            return 1;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message ID does not exist");
        }
    }

    public List<Message> getMessagesByAccountId(Integer accountId) {
        return messageRepository.findByPostedBy(accountId);
    }
}