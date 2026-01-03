<template>
  <div class="coach-container">
    <el-card class="coach-header" shadow="hover">
      <template #header>
        <div class="header-title">
          <el-icon><UserFilled /></el-icon>
          已验证教练列表
        </div>
      </template>
      <div class="user-info">
        <span>欢迎您，{{ currentUser?.username }}</span>
        <div class="action-buttons">
          <el-button type="primary" @click="goToUserCenter">
            <el-icon><ArrowLeft /></el-icon>
            返回用户中心
          </el-button>
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </el-card>
    
    <el-card class="coach-list-card" shadow="hover">
      <template #header>
        <div class="list-title">
          <el-icon><StarFilled /></el-icon>
          选择您的专属教练
        </div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="coaches.length === 0" class="empty-container">
        <el-empty description="暂无已验证的教练" />
      </div>
      
      <div v-else class="coach-grid">
        <el-card v-for="coach in coaches" :key="coach.coachId" class="coach-item" shadow="hover">
          <template #header>
            <div class="coach-name">
              <el-avatar :size="60" :src="coach.avatar" :icon="UserFilled" />
              <div class="coach-info">
                <h3>{{ coach.name }}</h3>
                <div class="coach-specialty">{{ coach.specialty }}</div>
                <div class="coach-rating">
                  <el-rate v-model="coach.rating" disabled :max="5" show-score score-template="{{value}}" />
                </div>
              </div>
            </div>
          </template>
          <div class="coach-details">
            <div class="detail-item">
              <el-icon><ChatDotRound /></el-icon>
              <span>专长: {{ coach.specialty }}</span>
            </div>
            <div class="detail-item">
              <el-icon><Phone /></el-icon>
              <span>联系方式: {{ coach.contact }}</span>
            </div>
          </div>
          <div class="coach-description">
            {{ coach.intro || '暂无介绍' }}
          </div>
          <div class="coach-actions">
            <el-button 
              type="success" 
              @click="openRatingList(coach)"
            >
              查看评价
            </el-button>
            <el-button 
              type="primary" 
              @click="selectCoach(coach.coachId)" 
              :disabled="isCoachSelected(coach.coachId)"
            >
              {{ isCoachSelected(coach.coachId) ? '已选择' : '选择教练' }}
            </el-button>
          </div>
        </el-card>
      </div>
      
      <div v-if="error" class="error-message">
        <el-alert type="error" :title="error" show-icon />
      </div>
    </el-card>
    
    <!-- 评价列表弹窗 -->
    <el-dialog
      v-model="ratingDialogVisible"
      :title="selectedCoach?.name + '的评价'"
      width="80%"
      top="5vh"
    >
      <CoachRatingList v-if="selectedCoach" :coach-id="selectedCoach.coachId" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { useCoachStore } from '../store/coachStore';
import { UserFilled, StarFilled, Clock, ChatDotRound, Phone, ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import CoachRatingList from '../components/CoachRatingList.vue';

const router = useRouter();
const authStore = useAuthStore();
const coachStore = useCoachStore();

const loading = ref(false);
const error = ref(null);
const ratingDialogVisible = ref(false);
const selectedCoach = ref(null);

const currentUser = computed(() => authStore.currentUser);
const coaches = computed(() => coachStore.allCoaches);
const userCoaches = computed(() => coachStore.selectedCoaches);

const fetchCoaches = async () => {
  loading.value = true;
  error.value = null;
  try {
    await coachStore.fetchCoaches();
    if (authStore.isAuthenticated && currentUser.value?.userId) {
      await coachStore.fetchUserCoaches(currentUser.value.userId);
    }
  } catch (err) {
    error.value = err.message || '获取教练列表失败';
  } finally {
    loading.value = false;
  }
};

const selectCoach = async (coachId) => {
  if (!authStore.isAuthenticated) {
    error.value = '请先登录';
    return;
  }
  
  loading.value = true;
  error.value = null;
  try {
    await coachStore.selectCoach(currentUser.value.userId, coachId);
    ElMessage.success('选择教练成功');
  } catch (err) {
    error.value = err.message || '选择教练失败';
  } finally {
    loading.value = false;
  }
};

const isCoachSelected = (coachId) => {
  return userCoaches.value.some(coach => coach.coachId === coachId);
};

const openRatingList = (coach) => {
  selectedCoach.value = coach;
  ratingDialogVisible.value = true;
};

const goToUserCenter = () => {
  router.push('/user');
};

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login');
    return;
  }
  fetchCoaches();
});
</script>

<style scoped>
.coach-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.coach-header {
  margin-bottom: 24px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
}

.header-title {
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #333;
}

.header-title .el-icon {
  font-size: 28px;
  color: #667eea;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
  border-bottom: none;
}

:deep(.el-card__body) {
  padding: 24px;
}

.user-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  flex-wrap: wrap;
  gap: 16px;
}

.user-info span {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.coach-list-card {
  margin-bottom: 20px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
}

.list-title {
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
}

.list-title .el-icon {
  font-size: 24px;
}

.loading-container {
  padding: 40px 20px;
  background: #f8f9fa;
  border-radius: 12px;
}

.empty-container {
  padding: 60px 0;
  text-align: center;
  background: #f8f9fa;
  border-radius: 12px;
}

.coach-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 24px;
  padding: 24px 0;
}

.coach-item {
  transition: all 0.3s ease;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  background: #fff;
}

.coach-item:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

:deep(.coach-item .el-card__header) {
  background: #f8f9fa;
  color: #333;
  padding: 20px;
  border-bottom: none;
}

:deep(.coach-item .el-card__body) {
  padding: 20px;
}

.coach-name {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.coach-name .el-avatar {
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.coach-name .el-avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.coach-info h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.coach-specialty {
  color: #666;
  font-size: 14px;
  background: #f0f0f0;
  display: inline-block;
  padding: 4px 12px;
  border-radius: 16px;
  margin-bottom: 8px;
}

.coach-rating {
  color: #f59e0b;
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-rate) {
  margin: 0;
}

:deep(.el-rate__text) {
  margin-left: 8px;
  font-size: 14px;
  font-weight: 500;
}

.coach-details {
  padding: 16px 0;
  border-top: 1px solid #e9ecef;
  border-bottom: 1px solid #e9ecef;
  margin: 16px 0;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #555;
}

.detail-item .el-icon {
  color: #667eea;
  font-size: 16px;
}

.coach-description {
  padding: 16px 0;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  min-height: 80px;
  display: flex;
  align-items: center;
}

.coach-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding-top: 16px;
  margin-top: 16px;
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
}

.coach-actions .el-button {
  flex: 1;
  padding: 10px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: none;
}

.coach-actions .el-button:nth-child(1) {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.coach-actions .el-button:nth-child(1):hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(59, 130, 246, 0.3);
}

.coach-actions .el-button:nth-child(2) {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.coach-actions .el-button:nth-child(2):hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(16, 185, 129, 0.3);
}

:deep(.el-button--disabled) {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  margin-top: 20px;
  background: #fee2e2;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #fecaca;
}

:deep(.el-alert) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(245, 34, 45, 0.1);
  border: none;
}

/* 弹窗样式优化 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  max-height: 90vh;
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
  overflow-y: auto;
  max-height: calc(90vh - 120px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .coach-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .user-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .action-buttons {
    width: 100%;
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
