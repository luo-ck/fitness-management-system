package com.fitness.system.service;

import com.fitness.system.entity.Conversation;
import com.fitness.system.entity.Message;
import java.util.List;

public interface IMessageService {
    /**
     * 发送消息
     * @param message 消息信息
     * @return 结果
     */
    boolean sendMessage(Message message);
    
    /**
     * 根据接收者ID和类型查询消息列表
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 消息列表
     */
    List<Message> getMessagesByReceiver(Long receiverId, String receiverType);
    
    /**
     * 根据ID查询消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    Message getMessageById(Long id);
    
    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 结果
     */
    boolean markMessageAsRead(Long id);
    
    /**
     * 删除消息
     * @param id 消息ID
     * @return 结果
     */
    boolean deleteMessage(Long id);
    
    /**
     * 获取用户的对话列表
     * @param userId 用户ID
     * @return 对话列表
     */
    List<Conversation> getConversations(Long userId);
    
    /**
     * 获取特定对话的消息历史
     * @param userId 当前用户ID
     * @param otherId 对话另一方ID
     * @param timestamp 时间戳，只返回此时间戳之后的消息
     * @return 消息列表
     */
    List<Message> getMessagesByConversation(Long userId, Long otherId, Long timestamp);
}