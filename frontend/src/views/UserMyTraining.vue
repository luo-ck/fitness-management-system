<template>
  <div class="my-training-container">
    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button @click="navigateBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>
    
    <el-card class="training-card" shadow="hover">
      <template #header>
        <div class="training-title">
          <el-icon><Notebook /></el-icon>
          我的训练计划
        </div>
      </template>
      
      <!-- 训练计划列表 -->
      <div v-if="loadingPlans" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="trainingPlans.length === 0" class="empty-container">
        <el-empty description="暂无训练计划" />
      </div>
      
      <div v-else class="training-plans-list">
        <el-card v-for="plan in trainingPlans" :key="plan.planId" class="plan-item" shadow="hover">
          <template #header>
            <div class="plan-header">
              <h3>训练计划 {{ formatDate(plan.planDate) }}</h3>
              <div class="plan-actions">
                <el-button type="primary" size="small" @click="showPlanDetails(plan)">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
                <el-button type="primary" size="small" @click="showFeedbackForm(plan)">
                  <el-icon><EditPen /></el-icon>
                  提交反馈
                </el-button>
              </div>
            </div>
          </template>
          <div class="plan-content">
            <div class="plan-info">
              <p><strong>教练：</strong>{{ plan.coachName || '未知' }}</p>
              <p><strong>计划日期：</strong>{{ formatDate(plan.planDate) }}</p>
              <p><strong>运动项目：</strong>{{ plan.exerciseCount || 0 }} 项</p>
            </div>
            
            <!-- 已提交的反馈 -->
            <div v-if="plan.feedback" class="feedback-item">
              <el-divider content-position="left">已提交的反馈</el-divider>
              <div class="feedback-content">
                <div class="feedback-rating">
                  <el-rate :model-value="plan.feedback.rating" disabled show-score />
                </div>
                <p><strong>感受：</strong>{{ plan.feedback.feeling }}</p>
                <p><strong>评价：</strong>{{ plan.feedback.comments }}</p>
                <p class="feedback-date">提交时间：{{ formatDate(plan.feedback.feedbackDate) }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
    
    <!-- 训练计划详情弹窗 -->
    <el-dialog
      v-model="planDetailDialogVisible"
      title="训练计划详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="loadingPlanDetail" class="loading-container">
        <el-skeleton :rows="8" animated />
      </div>
      <div v-else-if="currentPlanDetail" class="plan-detail-content">
        <h3>训练计划 {{ formatDate(currentPlanDetail.planDate) }}</h3>
        <el-divider />
        <h4>运动项目列表</h4>
        <div v-if="currentPlanDetail.exercises && Array.isArray(currentPlanDetail.exercises) && currentPlanDetail.exercises.length > 0" class="exercises-list">
          <el-table :data="currentPlanDetail.exercises" style="width: 100%">
            <el-table-column prop="orderIndex" label="序号" width="80" />
            <el-table-column label="运动名称" width="120">
              <template #default="scope">
                {{ getExerciseNameById(scope.row.exerciseId) }}
              </template>
            </el-table-column>
            <el-table-column prop="sets" label="组数" width="100" />
            <el-table-column prop="reps" label="次数" width="100" />
            <el-table-column prop="duration" label="时长(分钟)" width="120" />
            <el-table-column label="教学视频" width="150">
              <template #default="scope">
                <el-button 
                  v-if="scope.row.videoId" 
                  type="primary" 
                  size="small" 
                  link
                  @click="viewVideo(scope.row.videoId)"
                >
                  查看视频
                </el-button>
                <span v-else>无</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else class="empty-exercises">
          <el-empty description="暂无运动项" />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePlanDetails">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 反馈表单弹窗 -->
    <el-dialog
      v-model="feedbackDialogVisible"
      title="提交训练反馈"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="80px">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="feedbackForm.rating" :max="5" show-score />
        </el-form-item>
        
        <el-form-item label="感受" prop="feeling">
          <el-select v-model="feedbackForm.feeling" placeholder="请选择训练感受">
            <el-option label="轻松" value="轻松" />
            <el-option label="普通" value="普通" />
            <el-option label="较难" value="较难" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="评价" prop="comments">
          <el-input v-model="feedbackForm.comments" type="textarea" rows="4" placeholder="请对训练计划进行评价" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback" :loading="submittingFeedback">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { ElMessage } from 'element-plus';
import { Notebook, EditPen, View, ArrowLeft } from '@element-plus/icons-vue';
import api from '../services/api';

const router = useRouter();
const authStore = useAuthStore();

// 状态管理
const loadingPlans = ref(false);
const submittingFeedback = ref(false);
const trainingPlans = ref([]);
const feedbackDialogVisible = ref(false);
const feedbackFormRef = ref(null);
const currentPlan = ref(null);

// 训练计划详情相关状态
const planDetailDialogVisible = ref(false);
const loadingPlanDetail = ref(false);
const currentPlanDetail = ref(null);

// 当前用户
const user = computed(() => authStore.currentUser);

// 运动ID到名称的映射表
const exerciseIdToNameMap = {
  1: '跑步',
  2: '游泳',
  3: '举重',
  4: '俯卧撑',
  5: '仰卧起坐',
  6: '深蹲',
  7: '引体向上',
  8: '跳绳',
  9: '平板卧推'
};

// 反馈表单
const feedbackForm = reactive({
  rating: 5,
  feeling: '',
  comments: ''
});

// 反馈表单规则
const feedbackRules = {
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
  feeling: [{ required: true, message: '请描述训练感受', trigger: 'blur' }],
  comments: [{ required: true, message: '请对训练计划进行评价', trigger: 'blur' }]
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN');
};

// 根据运动ID获取运动名称
const getExerciseNameById = (exerciseId) => {
  return exerciseIdToNameMap[exerciseId] || `运动${exerciseId}`;
};

// 获取训练计划详情
const fetchPlanDetails = async (planId) => {
  try {
    loadingPlanDetail.value = true;
    const response = await api.get(`/training-plans/${planId}`);
    currentPlanDetail.value = response.data;
  } catch (error) {
    ElMessage.error('获取计划详情失败');
    console.error('获取计划详情失败:', error);
  } finally {
    loadingPlanDetail.value = false;
  }
};

// 显示训练计划详情
const showPlanDetails = (plan) => {
  planDetailDialogVisible.value = true;
  fetchPlanDetails(plan.planId);
};

// 关闭训练计划详情
const closePlanDetails = () => {
  planDetailDialogVisible.value = false;
  currentPlanDetail.value = null;
};

// 查看教学视频
const viewVideo = async (videoId) => {
  try {
    // 获取视频信息，包含视频URL
    const response = await api.get(`/trainer/videos/${videoId}`);
    const video = response.data;
    if (video && video.videoUrl) {
      // 使用相对路径，让浏览器自动使用当前页面的域名和端口
      // 视频URL格式为：/uploads/filename
      const videoUrl = video.videoUrl;
      // 确保是相对路径，不包含协议和域名
      const relativeUrl = videoUrl.startsWith('http') ? new URL(videoUrl).pathname : videoUrl;
      window.open(relativeUrl, '_blank');
    } else {
      ElMessage.error('视频URL无效');
    }
  } catch (error) {
    console.error('获取视频信息失败:', error);
    ElMessage.error('获取视频信息失败');
  }
};

// 获取训练计划列表
const fetchTrainingPlans = async () => {
  if (!user.value?.userId) {
    ElMessage.error('请先登录');
    router.push('/login');
    return;
  }
  
  loadingPlans.value = true;
  try {
    console.log('开始获取训练计划，用户ID:', user.value.userId);
    const response = await api.get(`/training-plans/student/${user.value.userId}`);
    console.log('获取训练计划成功，响应:', response);
    const plans = response.data;
    
    // 为每个计划获取对应的反馈
    for (const plan of plans) {
      await fetchFeedbackForPlan(plan);
    }
    
    trainingPlans.value = plans;
  } catch (error) {
    ElMessage.error('获取训练计划失败');
    console.error('获取训练计划失败:', error);
    console.error('错误详情:', error.response || error.message || error);
  } finally {
    loadingPlans.value = false;
  }
};

// 获取计划对应的反馈
const fetchFeedbackForPlan = async (plan) => {
  try {
    const response = await api.get(`/feedback/plan/${plan.planId}`);
    if (response.data && response.data.length > 0) {
      plan.feedback = response.data[0];
    }
  } catch (error) {
    console.error(`获取计划 ${plan.planId} 的反馈失败:`, error);
  }
};

// 显示反馈表单
const showFeedbackForm = (plan) => {
  currentPlan.value = plan;
  // 重置表单
  feedbackForm.rating = 5;
  feedbackForm.feeling = '';
  feedbackForm.comments = '';
  // 显示弹窗
  feedbackDialogVisible.value = true;
};

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackFormRef.value || !currentPlan.value) return;
  
  try {
    // 使用Promise-based的表单验证
    await feedbackFormRef.value.validate();
    
    submittingFeedback.value = true;
    
    const feedbackData = {
      planId: currentPlan.value.planId,
      userId: user.value.userId,
      rating: feedbackForm.rating,
      feeling: feedbackForm.feeling,
      comments: feedbackForm.comments
    };
    
    await api.post('/feedback', feedbackData);
    ElMessage.success('反馈提交成功');
    
    // 更新当前计划的反馈信息
    currentPlan.value.feedback = {
      ...feedbackData,
      feedbackId: Date.now() // 临时ID，实际由后端生成
    };
    
    // 关闭弹窗
    feedbackDialogVisible.value = false;
  } catch (error) {
    if (error.name === 'ValidateError') {
      // 表单验证失败，Element Plus会自动显示错误信息
      return;
    }
    
    ElMessage.error('反馈提交失败');
    console.error('提交反馈失败:', error);
    console.error('错误详情:', error.response || error.message || error);
  } finally {
    submittingFeedback.value = false;
  }
};

// 返回至用户中心
const navigateBack = () => {
  router.push('/user');
};

// 页面加载时获取数据
onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login');
    return;
  }
  fetchTrainingPlans();
});
</script>

<style scoped>
.my-training-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.back-button-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.training-card {
  margin-bottom: 20px;
}

.training-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.loading-container {
  padding: 20px 0;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}

.training-plans-list {
  padding: 10px 0;
}

.plan-item {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.plan-item:hover {
  transform: translateY(-3px);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.plan-header h3 {
  margin: 0;
  font-size: 18px;
}

.plan-actions {
  display: flex;
  gap: 10px;
}

.plan-content {
  padding: 10px 0;
}

.plan-info {
  margin-bottom: 15px;
}

.plan-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.feedback-item {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.feedback-content {
  padding: 10px;
  background-color: #fafafa;
  border-radius: 4px;
}

.feedback-rating {
  margin-bottom: 10px;
}

.feedback-content p {
  margin: 8px 0;
  font-size: 14px;
}

.feedback-date {
  color: #999;
  font-size: 12px !important;
  text-align: right;
}
</style>