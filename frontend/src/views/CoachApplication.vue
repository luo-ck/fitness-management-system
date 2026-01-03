<template>
  <div class="application-container">
    <el-card class="application-card" shadow="hover">
      <template #header>
        <div class="application-title">
          <el-icon><UserFilled /></el-icon>
          申请成为教练
        </div>
      </template>
      
      <el-form :model="applicationForm" :rules="rules" ref="applicationFormRef" label-width="100px">
        <el-form-item label="真实姓名" prop="name">
          <el-input v-model="applicationForm.name" placeholder="请输入您的真实姓名" />
        </el-form-item>
        
        <el-form-item label="专业特长" prop="specialty">
          <el-input v-model="applicationForm.specialty" placeholder="请输入您的专业特长，如：健身、瑜伽、跑步等" />
        </el-form-item>
        
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="applicationForm.contact" placeholder="请输入您的联系方式，如：电话、微信等" />
        </el-form-item>
        
        <el-form-item label="个人简介" prop="intro">
          <el-input 
            v-model="applicationForm.intro" 
            type="textarea" 
            placeholder="请简要介绍您的专业背景和教学经验" 
            :rows="5"
          />
        </el-form-item>
        
        <el-form-item label="附件上传">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            :auto-upload="false"
            :multiple="true"
            :limit="5"
            :on-exceed="handleExceed"
            :before-remove="beforeRemove"
            :file-list="fileList"
            accept=".jpg,.jpeg,.png,.gif,.pdf,.doc,.docx,.xls,.xlsx"
            list-type="picture-card"
            :on-preview="handlePreview"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                支持上传图片（jpg/jpeg/png/gif）和文档（pdf/doc/docx/xls/xlsx），最多5个文件，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
          
          <!-- 预览对话框 -->
          <el-dialog v-model="previewDialogVisible">
            <img v-if="previewImage" :src="previewImage" alt="Preview" class="preview-image">
            <div v-else class="preview-file">{{ previewFileName }}</div>
          </el-dialog>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitApplication" :loading="submitting">
            <el-icon><UploadFilled /></el-icon>
            提交申请
          </el-button>
          <el-button @click="resetForm">
            <el-icon><RefreshRight /></el-icon>
            重置
          </el-button>
          <el-button @click="$router.back()">
            <el-icon><Back /></el-icon>
            返回
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { UserFilled, UploadFilled, RefreshRight, Back, Plus } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '../services/api';

const router = useRouter();
const authStore = useAuthStore();
const applicationFormRef = ref();
const submitting = ref(false);

const user = computed(() => authStore.currentUser);

const applicationForm = reactive({
  name: '',
  specialty: '',
  contact: '',
  intro: ''
});

// 文件上传相关状态
const fileList = ref([]);
const previewDialogVisible = ref(false);
const previewImage = ref('');
const previewFileName = ref('');

const rules = {
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  specialty: [
    { required: true, message: '请输入专业特长', trigger: 'blur' },
    { min: 2, max: 50, message: '专业特长长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { min: 5, max: 50, message: '联系方式长度在 5 到 50 个字符', trigger: 'blur' }
  ],
  intro: [
    { required: true, message: '请输入个人简介', trigger: 'blur' },
    { min: 10, max: 500, message: '个人简介长度在 10 到 500 个字符', trigger: 'blur' }
  ]
};

// 文件上传相关方法

// 处理文件超出限制
const handleExceed = (files, uploadFiles) => {
  ElMessage.warning(`当前限制只能上传5个文件，本次选择了${files.length}个文件，将自动忽略多余文件`);
};

// 处理文件删除前确认
const beforeRemove = (uploadFile, uploadFiles) => {
  return ElMessageBox.confirm(`确定要删除文件 ${uploadFile.name} 吗？`).then(
    () => true,
    () => false
  );
};

// 处理文件预览
const handlePreview = (uploadFile) => {
  if (uploadFile.url.startsWith('blob:')) {
    previewImage.value = uploadFile.url;
    previewFileName.value = '';
  } else {
    previewImage.value = '';
    previewFileName.value = uploadFile.name;
  }
  previewDialogVisible.value = true;
};

// 提交申请，包含文件上传
const submitApplication = async () => {
  if (!applicationFormRef.value) return;
  
  await applicationFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true;
      try {
        // 创建FormData对象，用于提交表单数据和文件
        const formData = new FormData();
        
        // 添加表单字段
        formData.append('userId', user.value?.userId);
        formData.append('username', user.value?.username);
        formData.append('name', applicationForm.name);
        formData.append('specialty', applicationForm.specialty);
        formData.append('contact', applicationForm.contact);
        formData.append('intro', applicationForm.intro);
        
        // 添加文件
        fileList.value.forEach(file => {
          formData.append('attachments', file.raw);
        });
        
        // 提交申请
        const response = await api.post('/coach-applications', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
        
        if (response.data) {
          ElMessage.success('申请提交成功，请等待管理员审核');
          router.push('/user');
        } else {
          ElMessage.error('申请提交失败，请稍后重试');
        }
      } catch (err) {
        console.error('申请提交失败:', err);
        ElMessage.error('申请提交失败: ' + (err.response?.data?.message || err.message));
      } finally {
        submitting.value = false;
      }
    }
  });
};

// 重置表单
const resetForm = () => {
  if (applicationFormRef.value) {
    applicationFormRef.value.resetFields();
  }
  fileList.value = [];
};
</script>

<style scoped>
.application-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.application-card {
  margin-bottom: 20px;
}

.application-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-form {
  padding: 20px 0;
}

/* 文件预览样式 */
.preview-image {
  max-width: 100%;
  max-height: 60vh;
  object-fit: contain;
}

.preview-file {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60vh;
  font-size: 20px;
  color: #606266;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>