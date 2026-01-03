import { defineStore } from 'pinia';
import { authService } from '../services/authService';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: authService.getCurrentUser(),
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.user,
    currentUser: (state) => state.user,
    userType: () => localStorage.getItem('userType') || 'user'
  },

  actions: {
    async login(username, password) {
      try {
        this.loading = true;
        this.error = null;
        const data = await authService.login(username, password);
        this.user = data.user || { username };
        return data;
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async coachLogin(username, password) {
      try {
        // 调试日志：输出接收到的用户名
        console.log('AuthStore: Received username:', username);
        console.log('AuthStore: Username length:', username.length);
        
        this.loading = true;
        this.error = null;
        const data = await authService.coachLogin(username, password);
        this.user = data.coach || { username };
        return data;
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    async register(userData) {
      try {
        this.loading = true;
        this.error = null;
        const data = await authService.register(userData);
        return data;
      } catch (error) {
        this.error = error;
        throw error;
      } finally {
        this.loading = false;
      }
    },

    logout() {
      authService.logout();
      localStorage.removeItem('userType');
      this.user = null;
    },

    clearError() {
      this.error = null;
    },
    
    setCurrentUser(userData) {
      this.user = userData;
    }
  }
});
