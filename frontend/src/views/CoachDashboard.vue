<template>
  <div class="dashboard-container">
    <el-card class="dashboard-header" shadow="hover">
      <template #header>
        <div class="dashboard-title">
          <el-icon><User /></el-icon>
          教练控制台
        </div>
      </template>
      <div class="user-info">
        <span>欢迎您，{{ currentCoach?.name }}</span>
        <el-button @click="navigateToProfile">
          <el-icon><User /></el-icon>
          我的
        </el-button>
        <el-button type="danger" @click="handleLogout">退出登录</el-button>
      </div>
    </el-card>

    <el-card class="students-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><UserFilled /></el-icon>
          我的学员列表
        </div>
      </template>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="students.length === 0" class="empty-container">
        <el-empty description="暂无学员" />
      </div>

      <div v-else class="students-table">
        <el-table :data="students" stripe style="width: 100%">
          <el-table-column prop="userId" label="学员ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="age" label="年龄" width="80" />
          <el-table-column prop="gender" label="性别" width="80" />
          <el-table-column prop="goal" label="训练目标" width="120" />
          <el-table-column prop="contact" label="联系方式" width="150" />
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" size="small" @click="navigateToStudentDetail(scope.row.userId)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button type="info" size="small" @click="contactStudent(scope.row)">
                <el-icon><Phone /></el-icon>
                联系学员
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import api from '../services/api';
import { User, UserFilled, View, Phone } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const router = useRouter();
const authStore = useAuthStore();
const loading = ref(false);
const students = ref([]);

const currentCoach = computed(() => authStore.currentUser);

// 转换下划线格式为驼峰格式
const toCamelCase = (obj) => {
  if (Array.isArray(obj)) {
    return obj.map(item => toCamelCase(item));
  } else if (obj !== null && typeof obj === 'object') {
    // 确保obj是一个可枚举的对象，而不是其他类型的对象
    if (obj.constructor === Object) {
      return Object.keys(obj).reduce((acc, key) => {
        const camelKey = key.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
        acc[camelKey] = toCamelCase(obj[key]);
        return acc;
      }, {});
    }
    // 如果是其他类型的对象（如Date、RegExp等），直接返回
    return obj;
  }
  return obj;
};

const fetchStudents = async () => {
  loading.value = true;
  try {
    // 调用API获取教练的学员列表
    const response = await api.get(`/user-coaches/coach/${currentCoach.value.coachId}`);
    console.log('Raw students data:', response.data);
    
    // 确保始终处理数组类型的数据
    const rawData = response.data;
    // 处理可能的嵌套结构，如{data: [...]}或直接返回数组
    const studentsArray = Array.isArray(rawData) ? rawData : 
                         (rawData && rawData.data && Array.isArray(rawData.data)) ? rawData.data : [];
    
    // 将下划线格式转换为驼峰格式
    students.value = toCamelCase(studentsArray);
    console.log('Camel case students data:', students.value);
  } catch (err) {
    ElMessage.error('获取学员列表失败');
    console.error('获取学员列表失败:', err);
    // 失败时使用模拟数据
    students.value = [
      {
        userId: 1,
        username: 'testuser',
        name: '测试用户',
        age: 25,
        gender: '男',
        goal: '减脂',
        contact: '13800138000'
      }
    ];
  } finally {
    loading.value = false;
  }
};

// 导航到学员详情页面
const navigateToStudentDetail = (userId) => {
  router.push(`/coach/student/${userId}`);
};

// 导航到个人中心页面
const navigateToProfile = () => {
  router.push('/coach/profile');
};

const contactStudent = (student) => {
  // 跳转到聊天界面并选择对应的学员对话
  router.push({
    path: '/coach/chat',
    query: {
      otherId: student.userId,
      otherName: student.name || student.username,
      otherType: 'user'
    }
  });
};

const handleLogout = () => {
  authStore.logout();
  router.push('/coach/login');
};

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/coach/login');
    return;
  }
  fetchStudents();
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

.students-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.loading-container {
  padding: 20px;
}

.empty-container {
  padding: 40px 0;
}

.students-table {
  padding: 20px 0;
}
</style>