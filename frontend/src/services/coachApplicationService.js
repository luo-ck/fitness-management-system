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

export const coachApplicationService = {
  /**
   * 提交教练申请
   * @param {Object} applicationData 申请数据
   * @returns {Promise<Object>} 申请结果
   */
  async submitApplication(applicationData) {
    try {
      const response = await api.post('/coach-applications', applicationData);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '提交申请失败';
    }
  },

  /**
   * 根据用户ID查询申请
   * @param {number} userId 用户ID
   * @returns {Promise<Object>} 申请信息
   */
  async getApplicationByUserId(userId) {
    try {
      const response = await api.get(`/coach-applications/user/${userId}`);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '获取申请失败';
    }
  },

  /**
   * 查询所有申请
   * @returns {Promise<Array>} 申请列表
   */
  async getAllApplications() {
    try {
      const response = await api.get('/coach-applications');
      // 确保总是返回数组
      const data = Array.isArray(response.data) ? response.data : [response.data];
      return toCamelCase(data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '获取申请列表失败';
    }
  },

  /**
   * 根据ID查询申请详情
   * @param {number} id 申请ID
   * @returns {Promise<Object>} 申请详情
   */
  async getApplicationById(id) {
    try {
      const response = await api.get(`/coach-applications/${id}`);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '获取申请详情失败';
    }
  },

  /**
   * 审批通过申请
   * @param {number} id 申请ID
   * @param {Object} approveData 审批数据
   * @returns {Promise<Object>} 审批结果
   */
  async approveApplication(id, approveData) {
    try {
      const response = await api.put(`/coach-applications/approve/${id}`, approveData);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '审批通过失败';
    }
  },

  /**
   * 审批拒绝申请
   * @param {number} id 申请ID
   * @param {Object} rejectData 拒绝数据，包含反馈信息
   * @returns {Promise<Object>} 审批结果
   */
  async rejectApplication(id, rejectData) {
    try {
      const response = await api.put(`/coach-applications/reject/${id}`, rejectData);
      return toCamelCase(response.data);
    } catch (error) {
      throw error.response?.data?.message || error.response?.data || error.message || '拒绝申请失败';
    }
  }
};