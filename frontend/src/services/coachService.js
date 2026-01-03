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

export const coachService = {
  async getValidCoaches() {
    try {
      const response = await api.get('/coaches/valid');
      // 确保总是返回数组，处理后端可能返回单个对象的情况
      const data = Array.isArray(response.data) ? response.data : [response.data];
      return toCamelCase(data);
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.response?.data || error.message || '获取教练列表失败';
      throw errorMessage;
    }
  },

  async selectCoach(userId, coachId) {
    try {
      const response = await api.post('/user-coaches', {}, {
        params: { userId, coachId }
      });
      return toCamelCase(response.data);
    } catch (error) {
      const errorMessage = error.response?.data?.message || error.response?.data || error.message || '选择教练失败';
      throw errorMessage;
    }
  },

  async getUserCoaches(userId) {
    try {

      console.log('调用getUserCoaches，用户ID:', userId);
      const response = await api.get(`/user-coaches/user/${userId}`);
      console.log('getUserCoaches返回数据:', response.data);
      return toCamelCase(response.data);
    } catch (error) {
      console.error('getUserCoaches错误:', error);
      throw error.response?.data || error.message;
    }
  },

  async getCoachStudents(coachId) {
    try {
      console.log('调用getCoachStudents，教练ID:', coachId);
      const response = await api.get(`/user-coaches/coach/${coachId}`);
      console.log('getCoachStudents返回数据:', response.data);
      return toCamelCase(response.data);
    } catch (error) {
      console.error('getCoachStudents错误:', error);
      throw error.response?.data || error.message;
    }
  },

  async cancelCoach(userId, coachId) {
    try {
      const response = await api.delete(`/user-coaches`, {
        params: { userId, coachId }
      });
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  async getCoachRating(coachId) {
    try {
      const response = await api.get(`/coach-ratings/coach/${coachId}/average`);
      return response.data;
    } catch (error) {
      console.error('获取教练评分失败:', error);
      return 0.0;
    }
  }
};
