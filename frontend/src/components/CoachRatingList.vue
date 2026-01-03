<template>
  <div class="rating-list-container">
    <!-- 平均评分 -->
    <div v-if="averageRating !== null" class="average-rating">
      <el-rate v-model="displayRating" :max="5" disabled />
      <span class="average-rating-value">{{ averageRating.toFixed(1) }}</span>
      <span class="rating-count">({{ ratings.length }}条评价)</span>
    </div>
    
    <!-- 评价列表 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="ratings.length === 0" class="empty-container">
      <el-empty description="暂无评价" />
    </div>
    
    <div v-else class="rating-list">
      <el-card 
        v-for="rating in ratings" 
        :key="rating.ratingId" 
        class="rating-item" 
        shadow="hover"
      >
        <div class="rating-header">
          <div class="rating-user-info">
            <el-avatar :size="40" :icon="UserFilled" />
            <div class="user-details">
              <div class="user-name">{{ rating.userName }}</div>
              <div class="rating-time">{{ formatTime(rating.createTime) }}</div>
            </div>
          </div>
          <el-rate v-model="rating.rating" :max="5" disabled />
        </div>
        
        <div class="rating-content">
          {{ rating.comment }}
        </div>
      </el-card>
    </div>
    
    <!-- 关闭按钮 -->
    <div class="close-button-container">
      <el-button @click="handleClose">关闭</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { StarFilled, UserFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import api from '../services/api';

// 加载状态
const loading = ref(false);
// 评价列表
const ratings = ref([]);
// 平均评分
const averageRating = ref(null);
// 用于显示的评分（四舍五入到整数）
const displayRating = ref(0);

// 定义props
const props = defineProps({
  coachId: {
    type: Number,
    required: true
  }
});

// 定义事件
const emit = defineEmits(['close']);

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return date.toLocaleString();
};

// 获取评价列表
const fetchRatings = async () => {
  if (!props.coachId) return;
  
  loading.value = true;
  try {
    // 获取评价列表
    const ratingsResponse = await api.get(`/coach-ratings/coach/${props.coachId}`);
    ratings.value = ratingsResponse.data || [];
    
    // 获取平均评分
    const averageResponse = await api.get(`/coach-ratings/coach/${props.coachId}/average`);
    averageRating.value = averageResponse.data || 0;
    displayRating.value = Math.round(averageRating.value);
  } catch (error) {
    ElMessage.error('获取评价失败: ' + (error.response?.data?.message || error.message));
    console.error('获取评价失败:', error);
  } finally {
    loading.value = false;
  }
};

// 关闭评价列表
const handleClose = () => {
  emit('close');
};

// 监听coachId变化，重新获取评价
watch(() => props.coachId, () => {
  fetchRatings();
});

// 组件挂载时获取评价
onMounted(() => {
  fetchRatings();
});
</script>

<style scoped>
.rating-list-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.average-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.average-rating-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.rating-count {
  color: #666;
  font-size: 14px;
}

.loading-container {
  padding: 20px 0;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}

.rating-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 20px 0;
}

.rating-item {
  transition: transform 0.3s;
}

.rating-item:hover {
  transform: translateY(-3px);
}

.rating-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.rating-user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.user-name {
  font-weight: bold;
  color: #303133;
}

.rating-time {
  color: #909399;
  font-size: 12px;
}

.rating-content {
  color: #606266;
  line-height: 1.6;
  margin-top: 10px;
}

.close-button-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>