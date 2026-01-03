import { defineStore } from 'pinia';

export const useMessageStore = defineStore('message', {
  state: () => ({
    // 全局未读消息计数
    unreadCount: 0,
    // 每个对话的未读消息计数
    conversationUnreadCounts: {}
  }),

  getters: {
    // 是否有未读消息
    hasUnreadMessages: (state) => state.unreadCount > 0
  },

  actions: {
    // 增加未读消息计数
    incrementUnreadCount(conversationId) {
      this.unreadCount++;
      if (conversationId) {
        this.conversationUnreadCounts[conversationId] = (this.conversationUnreadCounts[conversationId] || 0) + 1;
      }
    },

    // 减少未读消息计数
    decrementUnreadCount(conversationId) {
      if (this.unreadCount > 0) {
        this.unreadCount--;
      }
      if (conversationId && this.conversationUnreadCounts[conversationId] > 0) {
        this.conversationUnreadCounts[conversationId]--;
      }
    },

    // 重置特定对话的未读消息计数
    resetUnreadCount(conversationId) {
      if (conversationId && this.conversationUnreadCounts[conversationId]) {
        this.unreadCount -= this.conversationUnreadCounts[conversationId];
        this.conversationUnreadCounts[conversationId] = 0;
      }
    },

    // 重置所有未读消息计数
    resetAllUnreadCounts() {
      this.unreadCount = 0;
      this.conversationUnreadCounts = {};
    },

    // 设置特定对话的未读消息计数
    setConversationUnreadCount(conversationId, count) {
      const oldCount = this.conversationUnreadCounts[conversationId] || 0;
      this.unreadCount += (count - oldCount);
      this.conversationUnreadCounts[conversationId] = count;
    },

    // 获取特定对话的未读消息计数
    getConversationUnreadCount(conversationId) {
      return this.conversationUnreadCounts[conversationId] || 0;
    }
  }
});