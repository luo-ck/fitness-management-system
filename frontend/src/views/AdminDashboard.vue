<template>
  <div class="dashboard-container">
    <el-card class="dashboard-header" shadow="hover">
      <template #header>
        <div class="dashboard-title">
          <el-icon><Management /></el-icon>
          管理员控制台
        </div>
      </template>
      <div class="user-info">
        <span>欢迎您，{{ currentAdmin?.name }}</span>
        <el-button type="danger" @click="handleLogout">退出登录</el-button>
      </div>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value">{{ userCount }}</div>
            <div class="stat-label">用户总数</div>
          </div>
          <el-icon class="stat-icon">
            <UserFilled />
          </el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value">{{ coachCount }}</div>
            <div class="stat-label">教练总数</div>
          </div>
          <el-icon class="stat-icon">
            <UserFilled />
          </el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="features-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><Menu /></el-icon>
          管理功能
        </div>
      </template>
      <div class="features-grid">
        <el-button type="primary" class="feature-button" @click="navigateToUsers">
          <el-icon><User /></el-icon>
          用户管理
        </el-button>
        <el-button type="primary" class="feature-button" @click="navigateToCoaches">
          <el-icon><User /></el-icon>
          教练管理
        </el-button>
        <el-button type="primary" class="feature-button" @click="navigateToCoachApplications">
          <el-icon><UserFilled /></el-icon>
          教练申请管理
        </el-button>
        <el-button type="primary" class="feature-button" @click="navigateToMessages">
          <el-icon><Message /></el-icon>
          消息管理
        </el-button>
        <el-button type="primary" class="feature-button" @click="navigateToFeedbacks">
          <el-icon><ChatDotRound /></el-icon>
          反馈管理
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Management, User, UserFilled, Document, Menu, ChatDotRound, Message } from '@element-plus/icons-vue';
import { ElMessage, ElLoading } from 'element-plus';
import api from '../services/api';

const router = useRouter();

// 数据统计
const userCount = ref(0);
const coachCount = ref(0);

const currentAdmin = computed(() => {
  const adminStr = localStorage.getItem('admin');
  return adminStr ? JSON.parse(adminStr) : null;
});

const handleLogout = () => {
  localStorage.removeItem('admin');
  localStorage.removeItem('token');
  router.push('/admin/login');
  ElMessage.success('已成功退出登录');
};

const navigateToUsers = () => {
  router.push('/admin/users');
};

const navigateToCoaches = () => {
  router.push('/admin/coaches');
};

const navigateToFeedbacks = () => {
  router.push('/admin/feedback');
};

const navigateToCoachApplications = () => {
  router.push('/admin/coach-applications');
};

const navigateToMessages = () => {
  router.push('/admin/messages');
};

// 获取系统统计数据
const fetchStats = async () => {
  let loadingInstance = ElLoading.service({
    lock: true,
    text: '加载中...',
    background: 'rgba(0, 0, 0, 0.7)'
  });

  try {
    // 获取用户总数
    const usersResponse = await api.get('/users');
    userCount.value = usersResponse.data.length;

    // 获取教练总数
    const coachesResponse = await api.get('/coaches');
    coachCount.value = coachesResponse.data.length;
  } catch (error) {
    ElMessage.error('获取统计数据失败');
    console.error('获取统计数据失败:', error);
  } finally {
    if (loadingInstance) {
      loadingInstance.close();
    }
  }
};

onMounted(() => {
  if (!currentAdmin.value) {
    router.push('/admin/login');
    return;
  }
  fetchStats();
});
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 20px;
}

.dashboard-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  position: relative;
  overflow: hidden;
}

.stat-content {
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
}

.stat-label {
  font-size: 16px;
  color: #606266;
  margin-top: 10px;
}

.stat-icon {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 48px;
  color: rgba(64, 158, 255, 0.2);
}

.features-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  padding: 20px 0;
}

.feature-button {
  height: 80px;
  font-size: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
}
</style>