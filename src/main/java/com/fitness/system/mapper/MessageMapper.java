package com.fitness.system.mapper;

import com.fitness.system.entity.Conversation;
import com.fitness.system.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    /**
     * 插入消息
     * @param message 消息信息
     * @return 结果
     */
    int insertMessage(Message message);

    /**
     * 根据接收者ID和类型查询消息列表
     * @param receiverId 接收者ID
     * @param receiverType 接收者类型
     * @return 消息列表
     */
    List<Message> selectMessagesByReceiver(Long receiverId, String receiverType);

    /**
     * 根据ID查询消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    Message selectMessageById(Long id);

    /**
     * 标记消息为已读
     * @param id 消息ID
     * @return 结果
     */
    int updateMessageToRead(Long id);

    /**
     * 删除消息
     * @param id 消息ID
     * @return 结果
     */
    int deleteMessageById(Long id);
    
    /**
     * 获取用户的对话列表
     * @param userId 用户ID
     * @return 对话列表
     */
    List<Conversation> selectConversations(Long userId);
    
    /**
     * 获取特定对话的消息历史
     * @param userId 当前用户ID
     * @param otherId 对话另一方ID
     * @param timestamp 时间戳，只返回此时间戳之后的消息
     * @return 消息列表
     */
    List<Message> selectMessagesByConversation(Long userId, Long otherId, Long timestamp);
}