<template>
  <div class="chat-view-container">
    <!-- 顶部导航栏 -->
    <div class="chat-view-header">
      <el-button 
        type="primary" 
        plain 
        @click="goBack" 
        class="back-button"
      >
        <el-icon><ArrowLeft /></el-icon>
        返回教练控制台
      </el-button>
      <h1 class="chat-view-title">消息中心</h1>
    </div>
    
    <div class="chat-view-content">
      <!-- 对话列表 -->
      <div class="conversation-section">
        <ConversationList ref="conversationListRef" @select-conversation="selectConversation" />
      </div>
      
      <!-- 聊天对话框 -->
      <div class="dialog-section">
        <ChatDialog 
          :current-conversation="currentConversation" 
          @message-sent="handleMessageSent"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ArrowLeft } from '@element-plus/icons-vue';
import ConversationList from './ConversationList.vue';
import ChatDialog from './ChatDialog.vue';

const router = useRouter();
const route = useRoute();
const currentConversation = ref(null);
const conversationListRef = ref(null);

// 选择对话
const selectConversation = (conversation) => {
  currentConversation.value = conversation;
};

// 处理消息发送事件，更新对话列表
const handleMessageSent = () => {
  if (conversationListRef.value) {
    conversationListRef.value.fetchConversations();
  }
};

// 返回教练控制台
const goBack = () => {
  router.push('/coach/dashboard');
};

// 处理URL参数，自动选择对话
const handleUrlParams = () => {
  const { otherId, otherName, otherType } = route.query;
  if (otherId && otherName && otherType) {
    currentConversation.value = {
      otherId: Number(otherId),
      otherName,
      otherType
    };
  }
};

// 监听路由变化，处理参数
watch(() => route.query, () => {
  handleUrlParams();
}, { immediate: true });

onMounted(() => {
  handleUrlParams();
});
</script>

<style scoped>
.chat-view-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.chat-view-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px 20px;
  background-color: white;
  border-bottom: 1px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

.chat-view-title {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.chat-view-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.conversation-section {
  width: 350px;
  border-right: 1px solid #e0e0e0;
  overflow-y: auto;
  background-color: white;
}

.dialog-section {
  flex: 1;
  overflow-y: auto;
  background-color: white;
}
</style>