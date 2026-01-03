<template>
  <div class="conversation-list-container">
    <el-card class="conversation-list-card" shadow="hover">
      <template #header>
        <div class="conversation-list-title">
          <el-icon><ChatDotRound /></el-icon>
          我的消息
        </div>
      </template>
      
      <!-- 搜索框 -->
      <el-input
        v-model="searchKeyword"
        placeholder="搜索对话对象"
        prefix-icon="Search"
        clearable
        @input="handleSearch"
        class="search-input"
      ></el-input>
      
      <!-- 对话列表 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="conversations.length === 0" class="empty-container">
        <el-empty description="暂无对话" />
      </div>
      
      <div v-else class="conversation-list">
        <el-card
          v-for="conversation in filteredConversations"
          :key="conversation.otherId"
          class="conversation-item"
          shadow="hover"
          :class="{ 'active': selectedConversationId === conversation.otherId }"
          @click="selectConversation(conversation)"
        >
          <div class="conversation-info">
            <div class="conversation-header">
              <h3 class="conversation-name">{{ conversation.otherName }}</h3>
              <span class="conversation-type">
                {{ conversation.otherType === 'user' ? '学员' : conversation.otherType === 'coach' ? '教练' : '管理员' }}
              </span>
            </div>
            <div class="conversation-preview">
              <span class="last-message">{{ getLastMessagePreview(conversation) }}</span>
              <span class="conversation-time">{{ formatTime(conversation.lastMessageTime) }}</span>
            </div>
            <div v-if="conversation.unreadCount > 0" class="unread-badge">
              {{ conversation.unreadCount }}
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '../store/authStore';
import { useMessageStore } from '../store/messageStore';
import { ChatDotRound, Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import api from '../services/api';
import { coachService } from '../services/coachService';

const authStore = useAuthStore();
const messageStore = useMessageStore();
const loading = ref(false);
const conversations = ref([]);
const searchKeyword = ref('');
const selectedConversationId = ref(null);

// 当前用户
const user = computed(() => authStore.currentUser);

// 过滤后的对话列表
const filteredConversations = computed(() => {
  if (!searchKeyword.value) {
    return conversations.value;
  }
  return conversations.value.filter(conversation => 
    conversation.otherName.toLowerCase().includes(searchKeyword.value.toLowerCase())
  );
});

// 获取对话列表
const fetchConversations = async () => {
  console.log('开始获取对话列表，当前用户信息:', user.value);
  console.log('localStorage中的user:', localStorage.getItem('user'));
  console.log('localStorage中的userType:', localStorage.getItem('userType'));
  
  // 直接从localStorage获取用户信息，确保获取到最新的数据
  const localStorageUser = JSON.parse(localStorage.getItem('user') || 'null');
  const localStorageUserType = localStorage.getItem('userType') || 'user';
  
  console.log('从localStorage获取的用户信息:', localStorageUser);
  console.log('从localStorage获取的用户类型:', localStorageUserType);
  
  // 获取当前用户ID，支持userId或coachId，直接从localStorageUser获取
  let userId = localStorageUser?.userId || localStorageUser?.coachId;
  
  // 如果直接获取失败，尝试从authStore的user获取
  if (!userId) {
    userId = user.value?.userId || user.value?.coachId;
  }
  
  console.log('最终获取的用户ID:', userId);
  
  if (!userId) {
    console.error('用户ID无效: undefined');
    // 尝试从user对象中获取其他可能的ID字段
    console.log('尝试获取其他ID字段:', user.value?.id || localStorageUser?.id);
    userId = user.value?.id || localStorageUser?.id;
    if (!userId) {
      console.error('无法获取用户ID，user对象:', user.value);
      return;
    }
  }
  
  loading.value = true;
  try {
    // 获取当前用户类型，优先使用localStorage中的userType
    const userType = localStorageUserType;
    console.log('当前用户类型:', userType, '用户ID:', userId);
    
    // 初始化为空数组，只显示已绑定的用户或教练
    let fetchedConversations = [];
    
    // 获取现有对话，用于合并对话历史
    let existingConversations = [];
    try {
      console.log('获取现有对话列表，URL:', `/messages/conversations/${userId}`);
      const response = await api.get(`/messages/conversations/${userId}`);
      existingConversations = response.data || [];
      // 使用服务器返回的未读消息计数
      console.log('从服务器获取的对话列表:', existingConversations);
    } catch (err) {
      console.error('获取现有对话列表失败:', err);
      // 继续执行，使用空数组
    }
    
    if (userType === 'coach') {
      // 教练只显示已绑定的学员
      try {
        // 教练使用coachId而不是userId
        const coachId = userId; // 直接使用已经获取到的userId，因为教练登录后，userId就是coachId
        console.log('获取学员列表，教练ID:', coachId);
        
        // 确保教练ID有效
        if (!coachId) {
          console.error('教练ID无效:', coachId);
          return;
        }
        
        const students = await coachService.getCoachStudents(coachId);
        console.log('获取到的学员列表:', students);
        
        // 为每个学员创建或合并对话
        students.forEach(student => {
          console.log('处理学员:', student);
          // 查找该学员是否已有对话
          const existingConversation = existingConversations.find(conv => 
            conv.otherId === student.userId && conv.otherType === 'user'
          );
          
          if (existingConversation) {
            // 如果已有对话，使用现有对话信息，包括unreadCount
            fetchedConversations.push({
              ...existingConversation,
              otherName: student.name || student.username // 使用学员的最新名称
            });
          } else {
            // 如果没有对话，创建新对话，unreadCount为0
            fetchedConversations.push({
              otherId: student.userId,
              otherName: student.name || student.username,
              otherType: 'user',
              lastMessage: null,
              lastMessageTime: null,
              unreadCount: 0
            });
          }
        });
      } catch (err) {
        console.error('获取学员列表失败:', err);
        // 不影响主流程，继续执行
      }
    } else if (userType === 'user') {
      // 用户只显示已绑定的教练
      try {
        console.log('获取教练列表，用户ID:', userId);
        
        // 确保用户ID有效
        if (!userId) {
          console.error('用户ID无效:', userId);
          return;
        }
        
        const userCoaches = await coachService.getUserCoaches(userId);
        console.log('获取到的教练列表:', userCoaches);
        
        // 为每个教练创建或合并对话
        userCoaches.forEach(coach => {
          console.log('处理教练:', coach);
          // 查找该教练是否已有对话
          const existingConversation = existingConversations.find(conv => 
            conv.otherId === coach.coachId && conv.otherType === 'coach'
          );
          
          if (existingConversation) {
            // 如果已有对话，使用现有对话信息，包括unreadCount
            fetchedConversations.push({
              ...existingConversation,
              otherName: coach.name || coach.username // 使用教练的最新名称
            });
          } else {
            // 如果没有对话，创建新对话，unreadCount为0
            fetchedConversations.push({
              otherId: coach.coachId,
              otherName: coach.name || coach.username,
              otherType: 'coach',
              lastMessage: null,
              lastMessageTime: null,
              unreadCount: 0
            });
          }
        });
      } catch (err) {
        console.error('获取教练列表失败:', err);
        // 不影响主流程，继续执行
      }
    }
    
    conversations.value = fetchedConversations;
    
    // 更新全局未读消息计数
    updateGlobalUnreadCount();
  } catch (err) {
    console.error('获取对话列表失败:', err);
    ElMessage.error('获取对话列表失败: ' + (err.response?.data?.message || err.message));
  } finally {
    loading.value = false;
  }
};

// 更新全局未读消息计数
const updateGlobalUnreadCount = () => {
  // 重置所有未读计数
  messageStore.resetAllUnreadCounts();
  
  // 遍历所有对话，更新未读计数
  conversations.value.forEach(conversation => {
    if (conversation.unreadCount > 0) {
      messageStore.setConversationUnreadCount(conversation.otherId, conversation.unreadCount);
    }
  });
  
  console.log('更新后的全局未读消息计数:', messageStore.unreadCount);
};

// 搜索对话
const handleSearch = () => {
  // 搜索逻辑已在computed属性中实现
};

// 选择对话
const selectConversation = (conversation) => {
  selectedConversationId.value = conversation.otherId;
  // 发送事件，通知父组件选择了对话
  emit('select-conversation', conversation);
};

// 获取最后一条消息预览
const getLastMessagePreview = (conversation) => {
  return conversation.lastMessage || '暂无消息';
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return date.toLocaleString();
};

// 定义事件
const emit = defineEmits(['select-conversation']);

// 页面加载时获取对话列表
onMounted(() => {
  if (authStore.isAuthenticated) {
    fetchConversations();
  }
});

// 暴露方法给父组件
defineExpose({
  fetchConversations
});
</script>

<style scoped>
.conversation-list-container {
  padding: 20px;
  max-width: 400px;
  margin: 0 auto;
}

.conversation-list-card {
  margin-bottom: 20px;
}

.conversation-list-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  margin-bottom: 20px;
}

.loading-container {
  padding: 20px 0;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}

.conversation-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.conversation-item {
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.conversation-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.conversation-item.active {
  border-left-color: #409eff;
  background-color: #ecf5ff;
}

.conversation-info {
  position: relative;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.conversation-name {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.conversation-type {
  font-size: 12px;
  color: #666;
  background-color: #f0f0f0;
  padding: 2px 8px;
  border-radius: 10px;
}

.conversation-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.last-message {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 10px;
}

.conversation-time {
  font-size: 12px;
  color: #999;
}

.unread-badge {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #f56c6c;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: bold;
}
</style>
