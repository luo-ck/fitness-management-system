<template>
  <div class="inbox-container">
    <el-card class="inbox-card" shadow="hover">
      <template #header>
        <div class="inbox-title">
          <el-icon><Message /></el-icon>
          我的收件箱
        </div>
      </template>
      
      <!-- 消息列表 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="messages.length === 0" class="empty-container">
        <el-empty description="暂无消息" />
      </div>
      
      <div v-else class="message-list">
        <el-divider content-position="left">消息列表</el-divider>
        
        <el-card 
          v-for="message in messages" 
          :key="message.id" 
          class="message-item" 
          shadow="hover"
          :class="{ 'read': message.isRead, 'unread': !message.isRead }"
          @click="selectMessage(message)"
        >
          <div class="message-header">
            <div class="message-title-row">
              <h3>{{ message.title }}</h3>
              <span class="message-time">{{ formatTime(message.sendTime) }}</span>
            </div>
            <div class="message-sender">发件人: {{ message.senderName }}</div>
          </div>
          <div class="message-content-preview">{{ truncateText(message.content, 100) }}</div>
          <div class="message-actions">
            <el-button type="primary" size="small" @click.stop="viewMessage(message)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button type="success" size="small" @click.stop="markAsRead(message)" v-if="!message.isRead">
              <el-icon><CircleCheck /></el-icon>
              标记已读
            </el-button>
            <el-button type="danger" size="small" @click.stop="deleteMessage(message)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </el-card>
      </div>
    </el-card>
    
    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedMessage?.title"
      width="500px"
    >
      <div class="message-detail">
        <div class="detail-item">
          <span class="label">发件人: </span>
          <span class="value">{{ selectedMessage?.senderName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">发送时间: </span>
          <span class="value">{{ formatTime(selectedMessage?.sendTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态: </span>
          <span class="value status">{{ selectedMessage?.isRead ? '已读' : '未读' }}</span>
        </div>
        <el-divider />
        <div class="detail-content">{{ selectedMessage?.content }}</div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="success" @click="markAsRead(selectedMessage); dialogVisible = false" v-if="!selectedMessage?.isRead">
            标记已读
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { Message, View, CircleCheck, Delete } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '../services/api';

const router = useRouter();
const authStore = useAuthStore();

const loading = ref(false);
const messages = ref([]);
const dialogVisible = ref(false);
const selectedMessage = ref(null);

const user = computed(() => authStore.currentUser);

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return date.toLocaleString();
};

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

// 获取消息列表
const fetchMessages = async () => {
  if (!user.value?.userId) return;
  
  loading.value = true;
  try {
    const response = await api.get(`/messages/receiver/${user.value.userId}/user`);
    // 过滤消息，只保留来自管理员的信息
    const allMessages = response.data || [];
    messages.value = allMessages.filter(message => message.senderName === '管理员');
    console.log('过滤后的消息列表:', messages.value);
  } catch (err) {
    console.error('获取消息失败:', err);
    ElMessage.error('获取消息失败: ' + (err.response?.data?.message || err.message));
  } finally {
    loading.value = false;
  }
};

// 选择消息
const selectMessage = (message) => {
  selectedMessage.value = message;
  // 如果消息未读，自动标记为已读
  if (!message.isRead) {
    markAsRead(message);
  }
};

// 查看消息详情
const viewMessage = (message) => {
  selectedMessage.value = message;
  dialogVisible.value = true;
  // 如果消息未读，自动标记为已读
  if (!message.isRead) {
    markAsRead(message);
  }
};

// 标记消息为已读
const markAsRead = async (message) => {
  if (!message || message.isRead) return;
  
  try {
    const response = await api.put(`/messages/${message.id}/read`);
    if (response.data) {
      message.isRead = true;
      ElMessage.success('消息已标记为已读');
    }
  } catch (err) {
    console.error('标记消息已读失败:', err);
    ElMessage.error('标记消息已读失败: ' + (err.response?.data?.message || err.message));
  }
};

// 删除消息
const deleteMessage = async (message) => {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await api.delete(`/messages/${message.id}`);
    if (response.data) {
      messages.value = messages.value.filter(item => item.id !== message.id);
      ElMessage.success('消息删除成功');
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('删除消息失败:', err);
      ElMessage.error('删除消息失败: ' + (err.response?.data?.message || err.message));
    }
  }
};

// 页面加载时获取消息
onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login');
    return;
  }
  fetchMessages();
});
</script>

<style scoped>
.inbox-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.inbox-card {
  margin-bottom: 20px;
}

.inbox-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.loading-container {
  padding: 20px 0;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}

.message-list {
  padding: 20px 0;
}

.message-item {
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.message-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.message-item.read {
  border-left-color: #1989fa;
  background-color: #f0f9ff;
}

.message-item.unread {
  border-left-color: #67c23a;
  background-color: #f0f9eb;
  font-weight: bold;
}

.message-header {
  margin-bottom: 10px;
}

.message-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.message-title-row h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-sender {
  font-size: 14px;
  color: #606266;
}

.message-content-preview {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 15px;
}

.message-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

/* 消息详情样式 */
.message-detail {
  padding: 10px 0;
}

.detail-item {
  margin-bottom: 15px;
  font-size: 14px;
}

.detail-item .label {
  font-weight: bold;
  color: #606266;
  margin-right: 10px;
}

.detail-item .value {
  color: #303133;
}

.detail-item .status {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.detail-item .status:contains('已读') {
  background-color: #ecf5ff;
  color: #409eff;
}

.detail-item .status:contains('未读') {
  background-color: #f0f9eb;
  color: #67c23a;
}

.detail-content {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.8;
  color: #303133;
}
</style>