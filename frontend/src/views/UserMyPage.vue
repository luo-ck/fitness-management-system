<template>
  <div class="user-my-page-container">
    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button @click="navigateBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>
    
    <el-card class="my-page-card" shadow="hover">
      <template #header>
        <div class="my-page-title">
          <el-icon><UserFilled /></el-icon>
          我的
        </div>
      </template>
      
      <!-- 标签页 -->
      <el-tabs v-model="activeTab" class="my-page-tabs">
        <!-- 我的训练计划标签页 -->
        <el-tab-pane label="我的训练计划" name="training">
          <div class="tab-content">
            <!-- 引入训练计划组件 -->
            <UserMyTraining />
          </div>
        </el-tab-pane>
        
        <!-- 我的消息标签页 -->
        <el-tab-pane label="我的消息" name="messages">
          <div class="tab-content">
            <!-- 引入消息组件 -->
            <UserInbox />
          </div>
        </el-tab-pane>
        
        <!-- 个人信息标签页 -->
        <el-tab-pane label="个人信息" name="profile">
          <div class="tab-content">
            <!-- 引入个人信息组件 -->
            <UserProfile />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { UserFilled, ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 引入子组件
import UserMyTraining from './UserMyTraining.vue';
import UserInbox from './UserInbox.vue';
import UserProfile from './UserProfile.vue';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 活跃标签页，默认为训练计划
const activeTab = ref('training');

// 当前用户
const user = computed(() => authStore.currentUser);

// 返回上一页
const navigateBack = () => {
  router.push('/user');
};

// 页面加载时检查路由参数，设置活跃标签页
onMounted(() => {
  if (!authStore.isAuthenticated) {
    ElMessage.error('请先登录');
    router.push('/login');
    return;
  }
  
  // 从路由参数中获取标签页信息
  if (route.query.tab) {
    activeTab.value = route.query.tab;
  }
});
</script>

<style scoped>
.user-my-page-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.back-button-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.my-page-card {
  margin-bottom: 20px;
}

.my-page-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.my-page-tabs {
  margin-top: 20px;
}

.tab-content {
  padding: 10px 0;
}
</style>