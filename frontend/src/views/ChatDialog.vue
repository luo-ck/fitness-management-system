<template>
  <div class="chat-dialog-container">
    <el-card class="chat-dialog-card" shadow="hover">
      <template #header>
        <div class="chat-dialog-title">
          <el-icon><Back /></el-icon>
          <h2>{{ props.currentConversation?.otherName }}</h2>
          <span class="conversation-type">
            {{ props.currentConversation?.otherType === 'user' ? '学员' : props.currentConversation?.otherType === 'coach' ? '教练' : '管理员' }}
          </span>
        </div>
      </template>
      
      <!-- 消息列表 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="messages.length === 0" class="empty-container">
        <el-empty description="暂无消息" />
        <div class="empty-tip">开始与{{ 
          props.currentConversation?.otherType === 'user' ? '学员' : 
          props.currentConversation?.otherType === 'coach' ? '教练' : '管理员' 
        }}交流吧！</div>
      </div>
      
      <div v-else ref="messageContainer" class="message-list">
        <div
          v-for="(message, index) in messages"
          :key="message.id || index"
          class="message-item"
          :class="{
            'sent': isSentByCurrentUser(message),
            'received': !isSentByCurrentUser(message)
          }"
        >
          <!-- 统一的消息内容渲染，不再按类型区分顺序 -->
          <div class="message-content" :class="{
            'training-plan-message': message.messageType === 'training_plan',
            'feedback-message': message.messageType === 'training_feedback'
          }">
            <!-- 普通消息 -->
            <template v-if="message.messageType === 'message'">
              <div class="message-text">{{ message.content }}</div>
            </template>
            
            <!-- 训练计划消息 -->
            <template v-else-if="message.messageType === 'training_plan'">
              <div class="message-header">
                <el-icon><Notebook /></el-icon>
                <h3>训练计划</h3>
              </div>
              <div class="training-plan-preview">
                <p><strong>日期：</strong>{{ formatDate(message.sendTime) }}</p>
                <p><strong>内容：</strong>{{ message.content }}</p>
              </div>
              <el-button
                type="primary"
                size="small"
                @click="viewTrainingPlan(message.relatedId)"
                class="view-plan-btn"
              >
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
            </template>
            
            <!-- 反馈消息 -->
            <template v-else-if="message.messageType === 'training_feedback'">
              <div class="message-header">
                <el-icon><StarFilled /></el-icon>
                <h3>训练反馈</h3>
              </div>
              <div class="feedback-content">
                <!-- 解析反馈消息，提取训练计划信息和反馈内容 -->
                <template v-if="message.content">
                  <!-- 检查消息是否包含训练计划标识 -->
                  <template v-if="message.content.startsWith('训练计划')">
                    <!-- 提取训练计划信息和反馈内容 -->
                    <p class="feedback-plan-info"><strong>训练计划：</strong>{{ message.content.split(' 的反馈：')[0] }}</p>
                    <div class="feedback-rating">
                      <el-rate :model-value="parseInt(message.content.split(' 的反馈：')[1]?.split('|')[1] || 5)" disabled show-score />
                    </div>
                    <p><strong>感受：</strong>{{ message.content.split(' 的反馈：')[1]?.split('|')[0] || '' }}</p>
                    <p v-if="message.content.split(' 的反馈：')[1]?.split('|')[2]" class="feedback-comments"><strong>评价：</strong>{{ message.content.split(' 的反馈：')[1].split('|')[2] }}</p>
                  </template>
                  <template v-else>
                    <!-- 兼容旧格式 -->
                    <div class="feedback-rating">
                      <el-rate :model-value="parseInt(message.content?.split('|')[1] || 5)" disabled show-score />
                    </div>
                    <p><strong>感受：</strong>{{ message.content?.split('|')[0] || '' }}</p>
                    <p v-if="message.content?.split('|')[2]" class="feedback-comments"><strong>评价：</strong>{{ message.content.split('|')[2] }}</p>
                  </template>
                </template>
              </div>
            </template>
            
            <!-- 所有消息类型都显示发送时间 -->
            <div class="message-time">{{ formatTime(message.sendTime) }}</div>
          </div>
        </div>
      </div>
      
      <!-- 消息输入框 -->
      <div class="message-input-container">
        <div class="input-wrapper">
          <el-input
            v-model="messageInput"
            placeholder="输入消息..."
            type="textarea"
            :rows="2"
            maxlength="500"
            show-word-limit
            resize="none"
            @keyup.enter.ctrl="sendMessage"
            @keyup.enter.exact="sendMessage"
          ></el-input>
          <el-button
            type="primary"
            @click="sendMessage"
            :disabled="!messageInput.trim()"
            class="send-button"
          >
            <el-icon><ChatDotRound /></el-icon>
            发送
          </el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 训练计划详情弹窗 -->
    <el-dialog
      v-model="trainingPlanDialogVisible"
      title="训练计划详情"
      width="800px"
    >
      <div v-if="loadingTrainingPlan" class="loading-container">
        <el-skeleton :rows="8" animated />
      </div>
      <div v-else-if="currentTrainingPlan" class="training-plan-detail">
        <h3>训练计划 {{ formatDate(currentTrainingPlan.planDate) }}</h3>
        <el-divider />
        <h4>运动项目列表</h4>
        <div v-if="currentTrainingPlan.exercises && currentTrainingPlan.exercises.length > 0" class="exercises-list">
          <el-table :data="currentTrainingPlan.exercises" style="width: 100%">
            <el-table-column prop="orderIndex" label="序号" width="80" />
            <el-table-column label="运动名称" width="120">
              <template #default="scope">
                {{ getExerciseNameById(scope.row.exerciseId) }}
              </template>
            </el-table-column>
            <el-table-column prop="sets" label="组数" width="100" />
            <el-table-column prop="reps" label="次数" width="100" />
            <el-table-column prop="duration" label="时长(分钟)" width="120" />
            <el-table-column label="教学视频" width="150">
              <template #default="scope">
                <el-button
                  v-if="scope.row.videoId"
                  type="primary"
                  size="small"
                  @click="viewVideo(scope.row.videoId)"
                >
                  <el-icon><VideoPlay /></el-icon>
                  查看视频
                </el-button>
                <span v-else>无</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else class="empty-exercises">
          <el-empty description="暂无运动项" />
        </div>
      </div>
      <template #footer>
        <el-button @click="trainingPlanDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue';
import { useAuthStore } from '../store/authStore';
import { useMessageStore } from '../store/messageStore';
import { Back, Notebook, View, StarFilled, ChatDotRound, VideoPlay } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import api from '../services/api';
import { messageService } from '../services/messageService';

const props = defineProps({
  currentConversation: {
    type: Object,
    default: null
  }
});

// 定义事件
const emit = defineEmits(['message-sent']);

const authStore = useAuthStore();
const messageStore = useMessageStore();
const loading = ref(false);
const messages = ref([]);
const messageInput = ref('');
const messageContainer = ref(null);
const loadingTrainingPlan = ref(false);
const trainingPlanDialogVisible = ref(false);
const currentTrainingPlan = ref(null);
const pollingTimer = ref(null);
const lastMessageTime = ref(null); // 记录上次消息时间，用于优化轮询

// 当前用户
const user = computed(() => authStore.currentUser);

// 运动ID到名称的映射表
const exerciseIdToNameMap = {
  1: '跑步',
  2: '游泳',
  3: '举重',
  4: '俯卧撑',
  5: '仰卧起坐',
  6: '深蹲',
  7: '引体向上',
  8: '跳绳',
  9: '平板卧推'
};

// 监听当前对话变化，获取消息历史
watch(() => props.currentConversation, (newConversation) => {
  if (newConversation) {
    fetchMessages(newConversation);
  }
}, { immediate: true });

// 获取消息历史
const fetchMessages = async (conversation, timestamp = null) => {
  // 获取当前用户ID，支持userId或coachId
  const userId = user.value?.userId || user.value?.coachId;
  if (!userId || !conversation?.otherId) return;
  
  // 确保conversation.otherId是有效数字
  const otherId = Number(conversation.otherId);
  if (isNaN(otherId)) {
    console.error('无效的对方ID:', conversation.otherId);
    return;
  }
  
  loading.value = true;
  try {
    let url = `/messages/conversation/${userId}/${otherId}`;
    if (timestamp) {
      url += `?timestamp=${timestamp}`;
    }
    
    console.log('获取消息历史，URL:', url);
    const response = await api.get(url);
    const newMessages = response.data || [];
    console.log('获取到的消息:', newMessages);
    
    // 合并新消息与现有消息
    const allMessages = timestamp && messages.value.length > 0 
      ? [...messages.value, ...newMessages] 
      : newMessages;
      
    // 添加调试日志，检查排序前的消息
    console.log('排序前的消息:', allMessages.map((msg, index) => ({
      index,
      type: msg.messageType,
      time: msg.sendTime,
      timestamp: new Date(msg.sendTime).getTime(),
      senderId: msg.senderId,
      receiverId: msg.receiverId
    })));
    
    // 确保所有消息都按发送时间升序排序，最早的消息在最上面，最新的消息在最下面
    messages.value = [...allMessages].sort((a, b) => {
      // 处理sendTime字段缺失或格式问题
      if (!a.sendTime && !b.sendTime) return 0;
      if (!a.sendTime) return -1;
      if (!b.sendTime) return 1;
      
      // 将sendTime转换为时间戳进行比较，确保所有格式的时间都能正确排序
      let timeA, timeB;
      
      try {
        timeA = new Date(a.sendTime).getTime();
        if (isNaN(timeA)) {
          // 尝试直接解析字符串中的数字（如果是Unix时间戳格式）
          timeA = parseInt(a.sendTime);
          if (isNaN(timeA)) {
            timeA = 0;
          }
        }
      } catch (e) {
        timeA = 0;
      }
      
      try {
        timeB = new Date(b.sendTime).getTime();
        if (isNaN(timeB)) {
          // 尝试直接解析字符串中的数字（如果是Unix时间戳格式）
          timeB = parseInt(b.sendTime);
          if (isNaN(timeB)) {
            timeB = 0;
          }
        }
      } catch (e) {
        timeB = 0;
      }
      
      console.log(`比较消息 - A: 类型${a.messageType}, 时间${a.sendTime}, 时间戳${timeA}`);
      console.log(`比较消息 - B: 类型${b.messageType}, 时间${b.sendTime}, 时间戳${timeB}`);
      console.log(`比较结果: ${timeA - timeB}`);
      
      // 按时间戳升序排序，确保最早的消息在最上面，最新的消息在最下面
      return timeA - timeB;
    });
    
    // 添加调试日志，检查排序结果
    console.log('排序后的消息顺序（按sendTime）:', messages.value.map((msg, index) => ({
      index,
      type: msg.messageType,
      time: msg.sendTime,
      timestamp: new Date(msg.sendTime).getTime(),
      content: msg.content.substring(0, 20) + '...'
    })));
    
    if (timestamp && messages.value.length > 0) {
      // 轮询获取新消息的情况
      // 只计算未读的新消息
      const receivedMessages = newMessages.filter(msg => msg.senderId !== userId);
      const unreadReceivedMessages = receivedMessages.filter(msg => !msg.isRead);
      console.log('未读的新消息:', unreadReceivedMessages);
      
      // 更新未读消息计数
      unreadReceivedMessages.forEach(msg => {
        messageStore.incrementUnreadCount(otherId);
      });
    } else {
      // 初始加载的情况
      // 标记所有未读消息为已读
      const unreadMessages = newMessages.filter(msg => msg.senderId !== userId && !msg.isRead);
      for (const msg of unreadMessages) {
        await messageService.markMessageAsRead(msg.id);
      }
      
      // 重置该对话的未读消息计数
      messageStore.resetUnreadCount(otherId);
      
      // 发送事件，通知父组件更新对话列表，以获取最新的未读消息计数
      emit('message-sent');
    }
    
    // 更新最后消息时间，从排序后的消息列表中获取
    if (messages.value.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1];
      lastMessageTime.value = new Date(lastMsg.sendTime).getTime();
      console.log('更新最后消息时间:', lastMessageTime.value);
    }
    
    // 滚动到底部
    nextTick(() => {
      if (messageContainer.value) {
        messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
      }
    });
  } catch (err) {
    console.error('获取消息历史失败:', err);
    ElMessage.error('获取消息历史失败: ' + (err.response?.data?.message || err.message));
  } finally {
    loading.value = false;
  }
};

// 开始轮询
const startPolling = () => {
  if (pollingTimer.value) return;
  
  // 初始获取一次，只有当conversation存在时才获取
  if (props.currentConversation) {
    fetchMessages(props.currentConversation);
  }
  
  // 设置轮询定时器，每10秒获取一次新消息，每次都重新获取所有消息以确保排序正确
  pollingTimer.value = setInterval(() => {
    if (props.currentConversation) {
      fetchMessages(props.currentConversation); // 不传递timestamp参数，每次都重新获取所有消息
    }
  }, 10000);
};

// 停止轮询
const stopPolling = () => {
  if (pollingTimer.value) {
    clearInterval(pollingTimer.value);
    pollingTimer.value = null;
  }
};

// 发送消息
const sendMessage = async () => {
  // 获取当前用户ID，支持userId或coachId
  const userId = user.value?.userId || user.value?.coachId;
  if (!messageInput.value.trim() || !userId || !props.currentConversation?.otherId) return;
  
  try {
    const messageData = {
      senderId: userId,
      senderName: user.value.name || user.value.username,
      receiverId: props.currentConversation.otherId,
      receiverType: props.currentConversation.otherType,
      messageType: 'message',
      content: messageInput.value.trim(),
      title: '消息',
      isRead: false
      // 移除手动设置的sendTime，由服务器自动设置
    };
    
    console.log('准备发送消息:', messageData);
    await api.post('/messages', messageData);
    console.log('消息发送成功');
    
    // 清空输入框
    messageInput.value = '';
    
    // 为了确保服务器有足够时间处理消息，添加一个小延迟
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 重新获取消息列表
    console.log('重新获取消息列表');
    await fetchMessages(props.currentConversation);
    console.log('消息列表更新完成');
    
    // 发送事件，通知父组件更新对话列表
    emit('message-sent');
    console.log('发送message-sent事件');
    
  } catch (err) {
    console.error('发送消息失败:', err);
    ElMessage.error('发送消息失败: ' + (err.response?.data?.message || err.message));
  }
};

// 判断消息是否由当前用户发送
const isSentByCurrentUser = (message) => {
  // 获取当前用户ID，支持userId或coachId
  const userId = user.value?.userId || user.value?.coachId;
  return message.senderId === userId;
};

// 查看训练计划详情
const viewTrainingPlan = async (planId) => {
  if (!planId) return;
  
  loadingTrainingPlan.value = true;
  try {
    const response = await api.get(`/training-plans/${planId}`);
    currentTrainingPlan.value = response.data;
    trainingPlanDialogVisible.value = true;
  } catch (err) {
    console.error('获取训练计划详情失败:', err);
    ElMessage.error('获取训练计划详情失败: ' + (err.response?.data?.message || err.message));
  } finally {
    loadingTrainingPlan.value = false;
  }
};

// 查看教学视频
const viewVideo = async (videoId) => {
  if (!videoId) return;
  
  try {
    // 获取视频信息，包含视频URL
    const response = await api.get(`/trainer/videos/${videoId}`);
    const video = response.data;
    if (video && video.videoUrl) {
      // 使用新窗口打开视频
      window.open(video.videoUrl, '_blank');
    } else {
      ElMessage.warning('视频信息获取失败');
    }
  } catch (err) {
    console.error('获取视频信息失败:', err);
    ElMessage.error('获取视频信息失败: ' + (err.response?.data?.message || err.message));
  }
};

// 根据运动ID获取运动名称
const getExerciseNameById = (exerciseId) => {
  return exerciseIdToNameMap[exerciseId] || `运动${exerciseId}`;
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) {
      console.error('无效的日期格式:', dateStr);
      return '';
    }
    return date.toLocaleDateString('zh-CN');
  } catch (error) {
    console.error('格式化日期失败:', error, '日期:', dateStr);
    return '';
  }
};

// 格式化时间 - 修复时区问题和时间显示错误
const formatTime = (time) => {
  if (!time) return '';
  
  try {
    let date;
    
    // 尝试不同的时间格式解析
    if (typeof time === 'string') {
      // 处理ISO格式时间
      if (time.includes('T')) {
        date = new Date(time);
      } 
      // 处理yyyy-MM-dd HH:mm:ss格式
      else if (time.match(/\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}/)) {
        // 确保正确解析为本地时间
        const [datePart, timePart] = time.split(' ');
        const [year, month, day] = datePart.split('-').map(Number);
        const [hours, minutes, seconds] = timePart.split(':').map(Number);
        // 直接创建本地时间，避免时区转换问题
        date = new Date(year, month - 1, day, hours, minutes, seconds);
      } 
      // 处理其他格式
      else {
        date = new Date(time);
      }
    } 
    // 如果是数字，假设是Unix时间戳
    else if (typeof time === 'number') {
      date = new Date(time);
    } 
    // 如果已经是Date对象，直接使用
    else if (time instanceof Date) {
      date = time;
    }
    
    if (!date || isNaN(date.getTime())) {
      console.error('无效的时间格式:', time);
      return '';
    }
    
    // 获取本地时间的小时和分钟
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    console.log(`格式化时间 - 原始: ${time}, 转换后: ${date.toISOString()}, 本地时间: ${date.toLocaleString()}, 显示: ${hours}:${minutes}`);
    
    return `${hours}:${minutes}`;
  } catch (error) {
    console.error('格式化时间失败:', error, '时间:', time);
    return '';
  }
};

// 页面加载时获取消息历史并开始轮询
onMounted(() => {
  if (authStore.isAuthenticated && props.currentConversation) {
    startPolling();
  }
});

// 页面卸载时停止轮询
onUnmounted(() => {
  stopPolling();
});

// 监听对话变化，重启轮询
watch(() => props.currentConversation, (newConversation) => {
  // 停止当前轮询
  stopPolling();
  
  // 如果有新对话，开始新的轮询
  if (newConversation) {
    startPolling();
  }
}, { immediate: true });
</script>

<style scoped>
.chat-dialog-container {
  padding: 0;
  height: 100%;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.chat-dialog-card {
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  border-radius: 0;
  box-shadow: none;
  border: none;
  overflow: hidden;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
  border-bottom: none;
}

:deep(.el-card__body) {
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-dialog-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.chat-dialog-title .el-icon {
  font-size: 20px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.chat-dialog-title .el-icon:hover {
  transform: translateX(-2px);
  opacity: 0.9;
}

.chat-dialog-title h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: white;
}

.conversation-type {
  font-size: 12px;
  color: #667eea;
  background-color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-weight: 500;
}

.loading-container {
  padding: 40px 20px;
  background: #f8f9fa;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-container {
  padding: 60px 0;
  text-align: center;
  background: #f8f9fa;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-tip {
  margin-top: 24px;
  color: #666;
  font-size: 16px;
  font-weight: 500;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  background: #f8f9fa;
}

.message-item {
  display: flex;
  max-width: 85%;
  margin-bottom: 8px;
  animation: messageSlideIn 0.3s ease-out;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.sent {
  justify-content: flex-end;
  align-items: flex-end;
}

.message-item.received {
  justify-content: flex-start;
  align-items: flex-start;
}

.message-content {
  padding: 14px 18px;
  border-radius: 20px;
  position: relative;
  word-break: break-word;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  font-size: 14px;
  line-height: 1.6;
}

.message-item.sent .message-content {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.message-item.received .message-content {
  background: white;
  color: #333;
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e9ecef;
}

.message-item.sent .message-content:hover {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  transform: translateY(-1px);
}

.message-item.received .message-content:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.message-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  font-weight: 600;
  font-size: 14px;
}

.message-item.sent .message-header {
  color: rgba(255, 255, 255, 0.95);
}

.message-item.received .message-header {
  color: #667eea;
}

.message-header .el-icon {
  font-size: 18px;
}

.message-text {
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.6;
}

.message-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.7);
  text-align: right;
  margin-top: 6px;
  padding-right: 4px;
}

.message-item.received .message-time {
  color: #909399;
}

.message-item.sent .message-time {
  color: rgba(255, 255, 255, 0.7);
}

/* 训练计划消息样式 */
.training-plan-message {
  background: linear-gradient(135deg, #ecf5ff 0%, #f0f4ff 100%) !important;
  border: 1px solid #d9ecff !important;
  border-radius: 18px !important;
  padding: 20px !important;
  color: #333 !important;
}

.training-plan-preview {
  margin-bottom: 16px;
  padding: 16px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px solid #ebeef5;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.training-plan-preview p {
  margin: 8px 0;
  font-size: 14px;
  line-height: 1.6;
}

.training-plan-preview strong {
  color: #667eea;
  font-weight: 600;
}

.view-plan-btn {
  margin-bottom: 12px;
  margin-right: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
}

/* 反馈消息样式 */
.feedback-message {
  background: linear-gradient(135deg, #f0f9eb 0%, #e8f5e8 100%) !important;
  border: 1px solid #e1f3d8 !important;
  border-radius: 18px !important;
  padding: 20px !important;
  color: #333 !important;
}

/* 优化反馈消息标题颜色 */
.feedback-message .message-header {
  color: #67c23a !important;
  font-weight: 600;
}

/* 优化反馈消息时间颜色 */
.feedback-message .message-time {
  color: #67c23a !important;
}

.feedback-rating {
  margin-bottom: 16px;
  padding: 10px 0;
  background: rgba(255, 255, 255, 0.8);
  padding: 12px;
  border-radius: 8px;
  margin: 12px 0;
}

.feedback-content {
  padding: 16px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px solid #f0f9eb;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.feedback-content p {
  margin: 10px 0;
  font-size: 14px;
  line-height: 1.6;
}

.feedback-plan-info {
  color: #67c23a;
  font-weight: 600;
  margin-bottom: 12px;
  font-size: 15px;
}

.feedback-comments {
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px dashed #e1f3d8;
}

.message-input-container {
  padding: 20px 24px;
  background: white;
  border-top: 1px solid #e9ecef;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.05);
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  background: white;
  border-radius: 24px;
  padding: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #e9ecef;
}

.input-wrapper .el-input {
  flex: 1;
  margin-bottom: 0;
}

:deep(.el-input__wrapper) {
  border-radius: 16px;
  border: none;
  box-shadow: none;
  background: transparent;
}

:deep(.el-input__wrapper:hover),
:deep(.el-input__wrapper.is-focus) {
  box-shadow: none;
  border: none;
}

:deep(.el-input__inner) {
  resize: none;
  border: none;
  font-size: 14px;
  color: #333;
  padding: 12px 16px;
  background: transparent;
}

:deep(.el-input__inner::placeholder) {
  color: #909399;
}

.send-button {
  align-self: flex-end;
  min-width: 90px;
  height: 36px;
  border-radius: 18px;
  font-weight: 500;
  font-size: 14px;
  padding: 0 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.send-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.send-button:disabled {
  background: #dcdfe6;
  color: #c0c4cc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 滚动条样式 */
.message-list::-webkit-scrollbar {
  width: 8px;
}

.message-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.message-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 弹窗样式优化 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
}

:deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

:deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #e9ecef;
}

/* 骨架屏样式 */
:deep(.el-skeleton__item) {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>
