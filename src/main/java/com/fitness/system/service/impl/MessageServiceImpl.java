package com.fitness.system.service.impl;

import com.fitness.system.entity.Conversation;
import com.fitness.system.entity.Message;
import com.fitness.system.mapper.MessageMapper;
import com.fitness.system.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public boolean sendMessage(Message message) {
        int result = messageMapper.insertMessage(message);
        return result > 0;
    }

    @Override
    public List<Message> getMessagesByReceiver(Long receiverId, String receiverType) {
        return messageMapper.selectMessagesByReceiver(receiverId, receiverType);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageMapper.selectMessageById(id);
    }

    @Override
    public boolean markMessageAsRead(Long id) {
        int result = messageMapper.updateMessageToRead(id);
        return result > 0;
    }

    @Override
    public boolean deleteMessage(Long id) {
        int result = messageMapper.deleteMessageById(id);
        return result > 0;
    }

    @Override
    public List<Conversation> getConversations(Long userId) {
        return messageMapper.selectConversations(userId);
    }

    @Override
    public List<Message> getMessagesByConversation(Long userId, Long otherId, Long timestamp) {
        return messageMapper.selectMessagesByConversation(userId, otherId, timestamp);
    }
}