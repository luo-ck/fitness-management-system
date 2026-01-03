<template>
  <div class="user-container">
    <el-card class="user-card" shadow="hover">
      <template #header>
        <div class="user-title">
          <el-icon><UserFilled /></el-icon>
          用户中心
        </div>
      </template>
      <div class="user-info">
        <el-avatar :size="100" :src="user?.avatar" :icon="UserFilled" />
        <div class="user-details">
          <div class="username-row">
            <h2>{{ user?.username }}</h2>
            <el-button type="success" size="small" @click="$router.push('/user/profile')">
              <el-icon><EditPen /></el-icon>
              编辑个人信息
            </el-button>
          </div>
          <div class="detail-item">
            <el-icon><Phone /></el-icon>
            <span>{{ user?.contact }}</span>
          </div>
          <div class="detail-item">
            <el-icon><Calendar /></el-icon>
            <span>注册时间: {{ user?.createdAt }}</span>
          </div>
        </div>
      </div>
      
      <el-divider>我的教练</el-divider>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="3" animated />
      </div>
      
      <div v-else-if="userCoaches.length === 0" class="empty-container">
        <el-empty description="暂无选择的教练" />
        <el-button type="primary" @click="$router.push('/coaches')">去选择教练</el-button>
      </div>
      
      <div v-else class="user-coaches">
        <el-card v-for="coach in userCoaches" :key="coach.coachId" class="coach-item" shadow="hover">
          <div class="coach-info">
            <el-avatar :size="50" :src="coach.avatar" :icon="UserFilled" />
            <div class="coach-details">
              <h3>{{ coach.name }}</h3>
              <div class="coach-specialty">{{ coach.specialty }}</div>
              <div class="coach-rating">
                评分: {{ coach.rating.toFixed(1) }}
                <el-rate v-model="coach.rating" disabled :max="5" show-score score-template="{{value}}" />
              </div>
            </div>
            <div class="coach-actions">
              <el-button type="success" size="small" @click="openRatingForm(coach)">去评价</el-button>
              <el-button type="danger" size="small" @click="cancelCoach(coach.coachId)">取消选择</el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 评价表单弹窗 -->
      <el-dialog
        v-model="ratingDialogVisible"
        title="评价教练"
        width="600px"
      >
        <CoachRatingForm 
          v-if="currentCoach"
          :coach-id="currentCoach.coachId"
          :coach-name="currentCoach.name"
          :user-id="user.userId"
          :user-name="user.username"
          @close="closeRatingForm"
          @submit-success="handleRatingSubmitSuccess"
        />
      </el-dialog>
      
      <div class="user-actions">
        <el-button type="primary" @click="$router.push('/coaches')">查看更多教练</el-button>
        <div class="message-center-button-wrapper">
          <el-button type="info" @click="$router.push('/chat')" class="message-center-button">
            <el-icon><ChatDotRound /></el-icon>
            消息中心
          </el-button>
          <el-badge v-if="hasUnreadMessages" :value="unreadCount" type="danger" class="message-badge" :hidden="!hasUnreadMessages" />
        </div>
        <el-button type="warning" @click="$router.push('/user/coach-application')">
          <el-icon><UserFilled /></el-icon>
          申请成为教练
        </el-button>
        <el-button type="success" @click="feedbackDialogVisible = true">
          <el-icon><ChatDotRound /></el-icon>
          意见反馈
        </el-button>

        <el-button type="danger" @click="handleLogout">退出登录</el-button>
      </div>

      <!-- 意见反馈弹窗 -->
      <el-dialog
        v-model="feedbackDialogVisible"
        title="意见反馈"
        width="600px"
      >
        <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="80px">
          <el-form-item label="标题" prop="title">
            <el-input v-model="feedbackForm.title" placeholder="请输入反馈标题" />
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input 
              v-model="feedbackForm.content" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入反馈内容" 
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="handleDialogClose">关闭</el-button>
            <el-button type="primary" @click="submitFeedback" :loading="submittingFeedback">提交反馈</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  
  <!-- 右下角浮动"我的"按钮 -->
  <div class="floating-button-container">
    <el-button type="primary" round @click="goToMyTraining" class="floating-button">
      <el-icon><UserFilled /></el-icon>
      我的
    </el-button>
  </div>
</div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { useCoachStore } from '../store/coachStore';
import { useMessageStore } from '../store/messageStore';
import { UserFilled, Phone, Calendar, EditPen, ChatDotRound, StarFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import api from '../services/api';
import CoachRatingForm from '../components/CoachRatingForm.vue';

const router = useRouter();
const authStore = useAuthStore();
const coachStore = useCoachStore();
const messageStore = useMessageStore();

const loading = ref(false);
const user = computed(() => authStore.currentUser);
const userCoaches = computed(() => coachStore.selectedCoaches);

// 未读消息相关
const unreadCount = computed(() => messageStore.unreadCount);
const hasUnreadMessages = computed(() => messageStore.hasUnreadMessages);

const fetchUserCoaches = async () => {
  if (!user.value?.userId) return;
  
  loading.value = true;
  try {
    await coachStore.fetchUserCoaches(user.value.userId);
  } catch (err) {
    console.error('获取用户教练失败:', err);
  } finally {
    loading.value = false;
  }
};

const cancelCoach = async (coachId) => {
  if (!user.value?.userId) return;
  
  try {
    await coachStore.cancelCoach(user.value.userId, coachId);
    ElMessage.success('取消选择教练成功');
  } catch (err) {
    ElMessage.error('取消选择教练失败: ' + (err.message || ''));
  }
};

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};

// 跳转到我的页面
const goToMyTraining = () => {
  router.push('/user/my');
};

// 意见反馈相关
const feedbackFormRef = ref(null);
const submittingFeedback = ref(false);
const feedbackDialogVisible = ref(false);
const feedbackForm = reactive({
  title: '',
  content: ''
});

const feedbackRules = {
  title: [{ required: true, message: '请输入反馈标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
};

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackFormRef.value) return;
  
  try {
    await feedbackFormRef.value.validate();
    
    submittingFeedback.value = true;
    
    // 使用消息API发送意见反馈给管理员
    const messageData = {
      senderId: user.value.userId,
      senderName: user.value.username || '用户',
      receiverId: 1, // 管理员ID，实际应该从配置或登录信息获取
      receiverType: 'admin', // 接收者类型为管理员
      messageType: 'system_feedback', // 消息类型为系统反馈
      title: feedbackForm.title,
      content: feedbackForm.content,
      isRead: false,
      sendTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    };
    
    await api.post('/messages', messageData);
    ElMessage.success('反馈提交成功，感谢您的意见！');
    
    // 关闭弹窗并重置表单
    handleDialogClose();
  } catch (error) {
    if (error.name === 'ValidateError') {
      return;
    }
    ElMessage.error('提交反馈失败: ' + (error.response?.data?.message || error.message));
    console.error('提交反馈失败详情:', error);
  } finally {
    submittingFeedback.value = false;
  }
};

// 关闭弹窗
const handleDialogClose = () => {
  feedbackDialogVisible.value = false;
  // 重置表单
  if (feedbackFormRef.value) {
    feedbackFormRef.value.resetFields();
  }
  feedbackForm.title = '';
  feedbackForm.content = '';
};

// 评价相关
const ratingDialogVisible = ref(false);
const currentCoach = ref(null);

// 打开评价表单
const openRatingForm = (coach) => {
  currentCoach.value = coach;
  ratingDialogVisible.value = true;
};

// 关闭评价表单
const closeRatingForm = () => {
  ratingDialogVisible.value = false;
  currentCoach.value = null;
};

// 评价提交成功处理
const handleRatingSubmitSuccess = () => {
  // 可以刷新教练列表或更新评分
  ElMessage.success('评价提交成功！');
  // 关闭弹窗
  closeRatingForm();
};

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login');
    return;
  }
  fetchUserCoaches();
  
  // 获取对话列表，初始化未读消息计数
  try {
    const userId = user.value?.userId || user.value?.coachId;
    if (userId) {
      const response = await api.get(`/messages/conversations/${userId}`);
      const conversations = response.data || [];
      console.log('获取到的对话列表:', conversations);
      
      // 更新全局未读消息计数
      messageStore.resetAllUnreadCounts();
      conversations.forEach(conversation => {
        if (conversation.unreadCount > 0) {
          messageStore.setConversationUnreadCount(conversation.otherId, conversation.unreadCount);
        }
      });
      console.log('初始化后的全局未读消息计数:', messageStore.unreadCount);
    }
  } catch (err) {
    console.error('获取对话列表失败:', err);
  }
});
</script>

<style scoped>
.user-container {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.user-card {
  margin-bottom: 20px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
  background: #fff;
}

.user-title {
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #333;
}

.user-title .el-icon {
  font-size: 28px;
  color: #667eea;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px;
  border-bottom: none;
}

:deep(.el-card__body) {
  padding: 32px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 32px;
  padding: 24px 0;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
}

.user-info .el-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.user-info .el-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.user-details {
  flex: 1;
}

.username-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.username-row h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

:deep(.el-button--success:hover) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(16, 185, 129, 0.3);
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

:deep(.el-button--danger:hover) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(239, 68, 68, 0.3);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
}

:deep(.el-button--info) {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

:deep(.el-button--info:hover) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.3);
}

:deep(.el-button--warning) {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

:deep(.el-button--warning:hover) {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(245, 158, 11, 0.3);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 16px;
  color: #555;
}

.detail-item .el-icon {
  color: #667eea;
  font-size: 18px;
}

.loading-container {
  padding: 32px 0;
}

.empty-container {
  padding: 48px 0;
  text-align: center;
  background: #f8f9fa;
  border-radius: 12px;
}

.user-coaches {
  padding: 24px 0;
}

.coach-item {
  margin-bottom: 20px;
  transition: all 0.3s ease;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.coach-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.coach-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}

.coach-info .el-avatar {
  border: 3px solid #f0f0f0;
  transition: all 0.3s ease;
}

.coach-info .el-avatar:hover {
  transform: scale(1.1);
  border-color: #667eea;
}

.coach-details {
  flex: 1;
  min-width: 200px;
}

.coach-details h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.coach-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 120px;
}

.coach-specialty {
  color: #666;
  font-size: 14px;
  margin-bottom: 12px;
  background: #f0f0f0;
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
}

.coach-rating {
  color: #f59e0b;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 12px;
}

:deep(.el-rate) {
  margin: 0;
}

:deep(.el-rate__text) {
  margin-left: 8px;
  font-size: 14px;
  font-weight: 500;
}

.user-actions {
  display: flex;
  gap: 12px;
  padding-top: 24px;
  flex-wrap: wrap;
  justify-content: center;
}

.user-actions .el-button {
  padding: 12px 20px;
  font-size: 14px;
  flex: 1;
  min-width: 120px;
}

/* 浮动按钮样式 */
.floating-button-container {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 1000;
}

.floating-button {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.18);
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  font-weight: 500;
}

.floating-button:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
  color: white;
}

/* 消息中心按钮样式 */
.message-center-button-wrapper {
  position: relative;
  display: inline-block;
  flex: 1;
  min-width: 140px;
}

.message-center-button {
  /* 确保按钮有足够的内边距，避免badge遮挡文字 */
  width: 100%;
  padding-right: 24px;
}

/* 调整badge位置 */
.message-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  transform: translate(50%, -50%);
  z-index: 10;
}

:deep(.el-divider) {
  margin: 24px 0;
  border-color: #e9ecef;
}

:deep(.el-divider__text) {
  color: #666;
  font-size: 16px;
  font-weight: 500;
  background-color: #fff;
  padding: 0 20px;
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

/* 表单样式优化 */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid #e1e4e8;
}

:deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

:deep(.el-input__inner) {
  font-size: 14px;
  color: #333;
  padding: 12px 16px;
}
</style>
