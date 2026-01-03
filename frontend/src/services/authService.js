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

export const authService = {
  async login(username, password) {
    try {
      const response = await api.post('/auth/login', {
        username,
        passwordHash: password
      });
      const camelCaseData = toCamelCase(response.data);
      if (camelCaseData && camelCaseData.token) {
        localStorage.setItem('token', camelCaseData.token);
        localStorage.setItem('user', JSON.stringify(camelCaseData.user || { username }));
        localStorage.setItem('userType', 'user'); // 明确设置用户类型
      }
      return camelCaseData;
    } catch (error) {
      throw error.response?.data || error.message;
    }
  },

  async coachLogin(username, password) {
    try {
      // 调试日志：输出接收到的用户名
      console.log('CoachLoginService: Received username:', username);
      console.log('CoachLoginService: Username length:', username.length);
      console.log('CoachLoginService: Username char codes:', Array.from(username).map(char => char.charCodeAt(0)));
      
      const response = await api.post('/auth/coach/login', {
        username,
        passwordHash: password
      });
      console.log('CoachLoginService: Response status:', response.status);
      console.log('CoachLoginService: Response data:', response.data);
      
      const camelCaseData = toCamelCase(response.data);
      if (camelCaseData && camelCaseData.token) {
        localStorage.setItem('token', camelCaseData.token);
        localStorage.setItem('user', JSON.stringify(camelCaseData.coach || { username }));
        localStorage.setItem('userType', 'coach'); // 明确设置用户类型为教练
      }
      return camelCaseData;
    } catch (error) {
      console.error('CoachLoginService: Error:', error);
      console.error('CoachLoginService: Error response:', error.response);
      console.error('CoachLoginService: Error status:', error.response?.status);
      console.error('CoachLoginService: Error data:', error.response?.data);
      throw error.response?.data || error.message || '登录失败，请检查用户名和密码';
    }
  },

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('userType'); // 退出登录时清除用户类型
  },

  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  async register(userData) {
    try {
      const response = await api.post('/auth/register', userData);
      const camelCaseData = toCamelCase(response.data);
      return camelCaseData;
    } catch (error) {
      throw error.response?.data || error.message || '注册失败';
    }
  }
};
