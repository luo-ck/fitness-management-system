<template>
  <div class="rating-form-container">
    <el-card class="rating-form-card" shadow="hover">
      <template #header>
        <div class="rating-form-title">
          <el-icon><StarFilled /></el-icon>
          评价教练
        </div>
      </template>
      
      <el-form :model="ratingForm" :rules="ratingRules" ref="ratingFormRef" label-width="80px">
        <el-form-item label="教练" prop="coachName">
          <el-input v-model="ratingForm.coachName" disabled />
        </el-form-item>
        
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="ratingForm.rating" :max="5" show-score />
        </el-form-item>
        
        <el-form-item label="评价内容" prop="comment">
          <el-input 
            v-model="ratingForm.comment" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入您的评价内容" 
          />
        </el-form-item>
        
        <el-form-item>
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交评价</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { StarFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import api from '../services/api';

// 表单引用
const ratingFormRef = ref(null);
// 加载状态
const submitting = ref(false);

// 定义props
const props = defineProps({
  coachId: {
    type: Number,
    required: true
  },
  coachName: {
    type: String,
    required: true
  },
  userId: {
    type: Number,
    required: true
  },
  userName: {
    type: String,
    required: true
  }
});

// 定义事件
const emit = defineEmits(['close', 'submit-success']);

// 评价表单数据
const ratingForm = reactive({
  coachId: props.coachId,
  coachName: props.coachName,
  userId: props.userId,
  userName: props.userName,
  rating: 0,
  comment: ''
});

// 表单验证规则
const ratingRules = {
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
  comment: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
};

// 提交评价
const handleSubmit = async () => {
  if (!ratingFormRef.value) return;
  
  try {
    await ratingFormRef.value.validate();
    
    submitting.value = true;
    
    // 构建评价数据
    const ratingData = {
      userId: ratingForm.userId,
      userName: ratingForm.userName,
      coachId: ratingForm.coachId,
      rating: ratingForm.rating,
      comment: ratingForm.comment
    };
    
    // 提交评价
    await api.post('/coach-ratings', ratingData);
    ElMessage.success('评价提交成功，感谢您的反馈！');
    
    // 触发成功事件
    emit('submit-success');
    // 关闭表单
    emit('close');
  } catch (error) {
    if (error.name === 'ValidateError') {
      return;
    }
    ElMessage.error('评价提交失败: ' + (error.response?.data?.message || error.message));
  } finally {
    submitting.value = false;
  }
};

// 取消评价
const handleCancel = () => {
  emit('close');
};
</script>

<style scoped>
.rating-form-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.rating-form-card {
  margin-bottom: 20px;
}

.rating-form-title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>