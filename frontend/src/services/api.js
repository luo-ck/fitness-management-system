import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
});

api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    // 确保始终使用UTF-8编码，但如果是FormData则不设置Content-Type
    if ((config.method === 'post' || config.method === 'put' || config.method === 'patch') && !(config.data instanceof FormData)) {
      config.headers['Content-Type'] = 'application/json; charset=utf-8';
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // 检查当前登录的用户类型
      const isUser = localStorage.getItem('user') !== null;
      const isCoach = localStorage.getItem('coach') !== null;
      const isAdmin = localStorage.getItem('admin') !== null;
      
      // 清除所有登录信息
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('coach');
      localStorage.removeItem('admin');
      
      // 根据用户类型重定向到对应的登录页面
      if (isAdmin) {
        window.location.href = '/admin/login';
      } else if (isCoach) {
        window.location.href = '/coach/login';
      } else {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
