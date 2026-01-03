package com.fitness.system.controller;

import com.fitness.system.entity.Conversation;
import com.fitness.system.entity.Message;
import com.fitness.system.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息Controller
 */
@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    /**
     * 发送消息
     * @param message 消息信息
     * @return 结果
     */
    @PostMapping
    public ResponseEntity<Boolean> sendMessage(@RequestBody Message message) {
        boolean result = messageService.sendMessage(message);
        if (result) {
            return ResponseEntity.status(HttpStatus.CREATED).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    /**
     * 根据接收者ID和类型查询消息列表
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 消息列表
     */
    @GetMapping("/receiver/{receiverId}/{receiverType}")
    public ResponseEntity<List<Message>> getMessagesByReceiver(
            @PathVariable Long receiverId, 
            @PathVariable String receiverType) {
        List<Message> messages = messageService.getMessagesByReceiver(receiverId, receiverType);
        return ResponseEntity.ok(messages);
    }

    /**
     * 根据ID查询消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(message);
    }

    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 结果
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Boolean> markMessageAsRead(@PathVariable Long id) {
        boolean result = messageService.markMessageAsRead(id);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除消息
     * @param id 消息ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteMessage(@PathVariable Long id) {
        boolean result = messageService.deleteMessage(id);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取用户的对话列表
     * @param userId 用户ID
     * @return 对话列表
     */
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<Conversation>> getConversations(@PathVariable Long userId) {
        List<Conversation> conversations = messageService.getConversations(userId);
        return ResponseEntity.ok(conversations);
    }
    
    /**
     * 获取特定对话的消息历史
     * @param userId 用户ID
     * @param otherId 对话另一方ID
     * @param timestamp 时间戳，只返回此时间戳之后的消息
     * @return 消息列表
     */
    @GetMapping("/conversation/{userId}/{otherId}")
    public ResponseEntity<List<Message>> getMessagesByConversation(
            @PathVariable Long userId, 
            @PathVariable Long otherId,
            @RequestParam(required = false) Long timestamp) {
        List<Message> messages = messageService.getMessagesByConversation(userId, otherId, timestamp);
        return ResponseEntity.ok(messages);
    }
}