<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="hover">
      <template #header>
        <div class="profile-title">
          <el-icon><User /></el-icon>
          个人信息编辑
        </div>
      </template>
      
      <el-form :model="userInfo" :rules="profileRules" ref="profileFormRef" label-width="100px">
        <el-form-item label="姓名" prop="username">
          <el-input v-model="userInfo.username" placeholder="请输入姓名" />
        </el-form-item>
        
        <el-form-item label="年龄" prop="age">
          <el-input v-model="userInfo.age" type="number" placeholder="请输入年龄" min="1" max="120" />
        </el-form-item>
        
        <el-form-item label="性别" prop="gender">
          <el-select v-model="userInfo.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="身高(cm)" prop="height">
          <el-input v-model="userInfo.height" type="number" placeholder="请输入身高" min="50" max="250" />
        </el-form-item>
        
        <el-form-item label="体重(kg)" prop="weight">
          <el-input v-model="userInfo.weight" type="number" placeholder="请输入体重" min="20" max="500" />
        </el-form-item>
        
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="userInfo.contact" placeholder="请输入联系方式" />
        </el-form-item>
        
        <el-form-item label="训练目标" prop="goal">
          <el-select v-model="userInfo.goal" placeholder="请选择训练目标">
            <el-option label="减脂" value="减脂" />
            <el-option label="增肌" value="增肌" />
            <el-option label="保持" value="保持" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回用户中心
          </el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存修改</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, ArrowLeft } from '@element-plus/icons-vue';
import api from '../services/api';

const router = useRouter();
const profileFormRef = ref(null);
const loading = ref(false);

const userInfo = reactive({
  userId: null,
  username: '',
  age: null,
  gender: '',
  height: null,
  weight: null,
  contact: '',
  goal: ''
});

const profileRules = {
  username: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  height: [{ required: true, message: '请输入身高', trigger: 'blur' }],
  weight: [{ required: true, message: '请输入体重', trigger: 'blur' }],
  goal: [{ required: true, message: '请输入训练目标', trigger: 'blur' }]
};

// 获取当前用户信息
const fetchUserInfo = async () => {
  try {
    loading.value = true;
    const currentUser = JSON.parse(localStorage.getItem('user'));
    if (!currentUser || !currentUser.userId) {
      ElMessage.error('请先登录');
      router.push('/login');
      return;
    }
    
    const response = await api.get(`/users/${currentUser.userId}`);
    const userData = response.data;
    
    // 填充表单数据
    userInfo.userId = userData.userId;
    userInfo.username = userData.username;
    userInfo.age = userData.age || null;
    userInfo.gender = userData.gender || '';
    userInfo.height = userData.height || null;
    userInfo.weight = userData.weight || null;
    userInfo.contact = userData.contact || '';
    userInfo.goal = userData.goal || '';
    
  } catch (error) {
    ElMessage.error('获取用户信息失败');
    console.error('获取用户信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!profileFormRef.value) return;
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await api.put(`/users/${userInfo.userId}`, userInfo);
        ElMessage.success('个人信息更新成功');
        // 更新localStorage中的用户信息
      const currentUser = JSON.parse(localStorage.getItem('user'));
      if (currentUser) {
        currentUser.username = userInfo.username;
        localStorage.setItem('user', JSON.stringify(currentUser));
      }
      } catch (error) {
        ElMessage.error('更新失败，请重试');
        console.error('更新个人信息失败:', error);
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.warning('请填写完整信息');
    }
  });
};

// 重置表单
const handleReset = () => {
  fetchUserInfo();
};

// 返回用户中心
const goBack = () => {
  router.push('/user');
};

onMounted(() => {
  fetchUserInfo();
});
</script>

<style scoped>
.profile-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.profile-card {
  margin-bottom: 20px;
}

.profile-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>