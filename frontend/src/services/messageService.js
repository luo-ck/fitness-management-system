import api from './api';

// 转换下划线格式为驼峰格式
const toCamelCase = (obj) => {
  if (Array.isArray(obj)) {
    return obj.map(item => toCamelCase(item));
  } else if (obj !== null && typeof obj === 'object') {
    return Object.keys(obj).reduce((acc, key) => {
      const camelKey = key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
      acc[camelKey] = toCamelCase(obj[key]);
      return acc;
    }, {});
  }
  return obj;
};

export const messageService = {
  /**
   * 发送消息
   * @param {Object} messageData 消息数据
   * @returns {Promise<Object>} 发送结果
   */
  async sendMessage(messageData) {
    try {
      const response = await api.post('/messages', messageData);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '发送消息失败';
    }
  },

  /**
   * 根据接收者ID和类型查询消息列表
   * @param {number} receiverId 接收者ID
   * @param {string} receiverType 接收者类型（user/coach/admin）
   * @returns {Promise<Array>} 消息列表
   */
  async getMessagesByReceiver(receiverId, receiverType) {
    try {
      const response = await api.get(`/messages/receiver/${receiverId}/${receiverType}`);
      // 确保总是返回数组
      const data = Array.isArray(response.data) ? response.data : [response.data];
      return toCamelCase(data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '获取消息列表失败';
    }
  },

  /**
   * 根据ID查询消息详情
   * @param {number} id 消息ID
   * @returns {Promise<Object>} 消息详情
   */
  async getMessageById(id) {
    try {
      const response = await api.get(`/messages/${id}`);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '获取消息详情失败';
    }
  },

  /**
   * 标记消息为已读
   * @param {number} id 消息ID
   * @returns {Promise<Object>} 操作结果
   */
  async markMessageAsRead(id) {
    try {
      const response = await api.put(`/messages/${id}/read`);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '标记消息已读失败';
    }
  },

  /**
   * 删除消息
   * @param {number} id 消息ID
   * @returns {Promise<Object>} 操作结果
   */
  async deleteMessage(id) {
    try {
      const response = await api.delete(`/messages/${id}`);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '删除消息失败';
    }
  }
};