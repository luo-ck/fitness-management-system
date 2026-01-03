<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="login-title">
          <el-icon><User /></el-icon>
          用户登录
        </div>
      </template>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="UserFilled" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" class="login-button">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </el-form-item>
      </el-form>
      <div v-if="error" class="error-message">
        <el-alert type="error" :title="error" show-icon />
      </div>
      
      <el-divider>其他登录方式</el-divider>
      
      <div class="other-login-buttons">
        <el-button type="info" @click="goToCoachLogin" class="login-button">教练登录</el-button>
        <el-button type="warning" @click="goToAdminLogin" class="login-button">管理员登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { User, UserFilled, Lock } from '@element-plus/icons-vue';

const router = useRouter();
const authStore = useAuthStore();
const loginFormRef = ref(null);
const loading = ref(false);
const error = ref(null);

const loginForm = reactive({
  username: '',
  password: ''
});

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      error.value = null;
      try {
        await authStore.login(loginForm.username, loginForm.password);
        router.push('/user');
      } catch (err) {
        error.value = err.message || '登录失败，请检查用户名和密码';
      } finally {
        loading.value = false;
      }
    }
  });
};

const goToRegister = () => {
  router.push('/register');
};

const goToCoachLogin = () => {
  router.push('/coach/login');
};

const goToAdminLogin = () => {
  router.push('/admin/login');
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.login-card:hover {
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.login-title {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.login-title .el-icon {
  font-size: 28px;
  margin-right: 12px;
  color: #667eea;
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #e9ecef;
  padding: 24px;
  text-align: center;
}

:deep(.el-card__body) {
  padding: 32px 24px;
}

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

.login-button {
  width: 100%;
  margin-bottom: 16px;
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

:deep(.el-button--default) {
  border-radius: 8px;
  padding: 12px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

:deep(.el-button--default:hover) {
  background-color: #f8f9fa;
  transform: translateY(-1px);
}

.other-login-buttons {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.other-login-buttons .login-button {
  width: 50%;
  margin-bottom: 0;
  font-size: 14px;
  padding: 10px;
}

:deep(.el-divider) {
  margin: 24px 0;
}

:deep(.el-divider__text) {
  color: #666;
  font-size: 14px;
  font-weight: 500;
  background-color: #fff;
  padding: 0 16px;
}

.error-message {
  margin-top: 20px;
}

:deep(.el-alert) {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(245, 34, 45, 0.1);
}
</style>
