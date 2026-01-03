<template>
  <div class="student-detail-container">
    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button @click="navigateBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>
    
    <!-- 训练计划详情弹窗 -->
    <el-dialog v-model="planDetailDialogVisible" title="训练计划详情" width="800px">
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
          <el-button @click="planDetailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
    
    <el-card class="student-info-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><User /></el-icon>
          学员详情
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="学员ID">{{ studentInfo.userId }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ studentInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ studentInfo.name }}</el-descriptions-item>
            <el-descriptions-item label="年龄">{{ studentInfo.age || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
        <el-col :span="12">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="性别">{{ studentInfo.gender || '-' }}</el-descriptions-item>
            <el-descriptions-item label="身高">{{ studentInfo.height ? studentInfo.height + ' cm' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="体重">{{ studentInfo.weight ? studentInfo.weight + ' kg' : '-' }}</el-descriptions-item>
            <el-descriptions-item label="训练目标">{{ studentInfo.goal || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
      
      <div class="contact-info">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="联系方式">{{ studentInfo.contact || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
    
    <el-tabs v-model="activeTab" class="student-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="训练计划" name="plans">
        <div class="tab-content">
          <div class="tab-header">
            <h3>训练计划列表</h3>
            <el-button type="primary" @click="navigateToPlanEdit">
              <el-icon><Plus /></el-icon>
              创建训练计划
            </el-button>
          </div>
          
          <div v-if="loadingPlans" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          
          <div v-else-if="trainingPlans.length === 0" class="empty-container">
            <el-empty description="暂无训练计划" />
          </div>
          
          <div v-else class="plans-list">
            <el-card v-for="plan in trainingPlans" :key="plan.planId" class="plan-card" shadow="hover">
              <template #header>
                <div class="plan-header">
                  <span class="plan-title">训练计划 {{ formatDate(plan.planDate) }}</span>
                  <div class="plan-actions">
                    <el-button type="primary" size="small" @click="navigateToPlanEdit(plan.planId)">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                    <el-button type="success" size="small" @click="viewPlanDetails(plan.planId)">
                      <el-icon><View /></el-icon>
                      查看详情
                    </el-button>
                  </div>
                </div>
              </template>
              <div class="plan-content">
                <p>训练日期：{{ formatDate(plan.planDate) }}</p>
                <p>运动项目：{{ plan.exerciseCount || 0 }} 项</p>
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="训练反馈" name="feedback">
        <div class="tab-content">
          <div class="tab-header">
            <h3>训练反馈列表</h3>
          </div>
          
          <div v-if="loadingFeedback" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>
          
          <div v-else-if="feedbackList.length === 0" class="empty-container">
            <el-empty description="暂无训练反馈" />
          </div>
          
          <div v-else class="feedback-list">
            <el-card v-for="feedback in feedbackList" :key="feedback.feedbackId" class="feedback-card" shadow="hover">
              <template #header>
                <div class="feedback-header">
                  <span class="feedback-title">反馈：{{ formatDate(feedback.feedbackDate) }}</span>
                  <el-rate :model-value="feedback.rating" disabled show-score />
                </div>
              </template>
              <div class="feedback-content">
                <p><strong>感受：</strong>{{ feedback.feeling || '-' }}</p>
                <p><strong>评价：</strong>{{ feedback.comments || '-' }}</p>
                <p><strong>计划：</strong>训练计划 {{ formatDate(feedback.planDate) }}</p>
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Plus, Edit, View, ArrowLeft } from '@element-plus/icons-vue';
import api from '../services/api';

const route = useRoute();
const router = useRouter();
const activeTab = ref('plans');
const loading = ref(false);
const loadingPlans = ref(false);
const loadingFeedback = ref(false);

const studentInfo = reactive({
  userId: null,
  username: '',
  name: '',
  age: null,
  gender: '',
  height: null,
  weight: null,
  contact: '',
  goal: ''
});

const trainingPlans = ref([]);
const feedbackList = ref([]);
const planDetailDialogVisible = ref(false);
const loadingPlanDetail = ref(false);
const currentPlanDetail = ref(null);

const userId = computed(() => route.params.userId);

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
    loadingPlanDetail.value = true;// 获取训练计划详情
    const response = await api.get(`/training-plans/${planId}`);
    console.log('获取到的计划详情响应:', response); // 添加调试信息
    console.log('获取到的计划详情数据:', response.data); // 添加更详细的调试信息
    console.log('是否包含exercises字段:', 'exercises' in response.data); // 检查是否包含exercises字段
    if ('exercises' in response.data) {
      console.log('exercises数据:', response.data.exercises); // 打印exercises数据
      console.log('exercises长度:', response.data.exercises.length); // 打印exercises长度
    }
    currentPlanDetail.value = response.data;
  } catch (error) {
    console.error('获取计划详情失败:', error);
    ElMessage.error('获取计划详情失败');
  } finally {
    loadingPlanDetail.value = false;
  }
};

// 获取学员信息
const fetchStudentInfo = async () => {
  try {
    loading.value = true;
    const response = await api.get(`/users/${userId.value}`);
    const userData = response.data;
    
    // 填充学员信息
    Object.assign(studentInfo, userData);
  } catch (error) {
    ElMessage.error('获取学员信息失败');
    console.error('获取学员信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 获取训练计划列表
const fetchTrainingPlans = async () => {
  try {
    loadingPlans.value = true;
    // 这里假设后端提供了根据学员ID获取训练计划的API
    const response = await api.get(`/training-plans/student/${userId.value}`);
    const plansData = response.data;
    console.log('获取到的训练计划列表:', plansData); // 添加调试信息
    trainingPlans.value = plansData;
  } catch (error) {
    console.error('获取训练计划失败:', error);
    // 暂时使用模拟数据
    trainingPlans.value = [
      {
        planId: 1,
        planDate: '2025-12-22T00:00:00',
        exerciseCount: 3
      },
      {
        planId: 2,
        planDate: '2025-12-21T00:00:00',
        exerciseCount: 4
      }
    ];
  } finally {
    loadingPlans.value = false;
  }
};

// 获取训练反馈列表
const fetchFeedbackList = async () => {
  try {
    loadingFeedback.value = true;
    console.log('=== 开始获取训练反馈 ===');
    console.log('学员ID:', userId.value);
    console.log('API URL:', `/feedback/student/${userId.value}`);
    
    // 这里假设后端提供了根据学员ID获取训练反馈的API
    const response = await api.get(`/feedback/student/${userId.value}`);
    
    console.log('=== 获取训练反馈成功 ===');
    console.log('响应状态:', response.status);
    console.log('响应数据:', response.data);
    
    const feedbackData = response.data;
    
    // 确保feedbackData是数组，如果不是则转换为数组
    const formattedData = Array.isArray(feedbackData) ? feedbackData : [feedbackData];
    
    // 处理日期字段，确保格式正确
    const processedData = formattedData.map(feedback => {
      // 为每个反馈添加默认值，防止缺少字段
      return {
        feedbackId: feedback.feedbackId || 0,
        feedbackDate: feedback.feedbackDate || new Date().toISOString(),
        planDate: feedback.planDate || new Date().toISOString(),
        feeling: feedback.feeling || '未填写',
        rating: feedback.rating || 0,
        comments: feedback.comments || '未填写'
      };
    });
    
    feedbackList.value = processedData;
    console.log('=== 训练反馈数据处理完成 ===');
    console.log('处理后的数据:', processedData);
  } catch (error) {
    console.error('=== 获取训练反馈失败 ===');
    console.error('错误类型:', typeof error);
    console.error('错误对象:', error);
    
    if (error.response) {
      // API返回了错误响应
      console.error('响应状态:', error.response.status);
      console.error('响应数据:', error.response.data);
      console.error('响应头:', error.response.headers);
      ElMessage.error(`获取训练反馈失败，状态码: ${error.response.status}，原因: ${JSON.stringify(error.response.data)}`);
    } else if (error.request) {
      // 请求已发送但没有收到响应
      console.error('请求已发送但未收到响应:', error.request);
      ElMessage.error('获取训练反馈失败，未收到服务器响应，请检查网络连接');
    } else {
      // 请求配置错误
      console.error('请求配置错误:', error.message);
      ElMessage.error(`获取训练反馈失败: ${error.message}`);
    }
    
    console.error('错误配置:', error.config);
    
    // 暂时使用模拟数据
    feedbackList.value = [
      {
        feedbackId: 1,
        feedbackDate: '2025-12-21T00:00:00',
        planDate: '2025-12-21T00:00:00',
        feeling: '感觉很好',
        rating: 5,
        comments: '训练计划很合理，效果不错'
      }
    ];
  } finally {
    loadingFeedback.value = false;
    console.log('=== 获取训练反馈流程结束 ===');
  }
};

// 导航到训练计划编辑页面
const navigateToPlanEdit = (planId = null) => {
  if (planId) {
    // 编辑现有计划
    router.push(`/coach/student/${userId.value}/plan/${planId}`);
  } else {
    // 创建新计划，不包含planId参数
    router.push(`/coach/student/${userId.value}/plan`);
  }
};

// 查看训练计划详情
const viewPlanDetails = (planId) => {
  planDetailDialogVisible.value = true;
  fetchPlanDetails(planId);
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

// 返回上一页
const navigateBack = () => {
  router.push('/coach/dashboard');
};

// 监听标签页切换，加载对应数据
const handleTabChange = (tabName) => {
  if (tabName === 'plans') {
    fetchTrainingPlans();
  } else if (tabName === 'feedback') {
    fetchFeedbackList();
  }
};

onMounted(() => {
  fetchStudentInfo();
  fetchTrainingPlans();
  // 如果初始激活的是反馈标签页，加载反馈数据
  if (activeTab.value === 'feedback') {
    fetchFeedbackList();
  }
});
</script>

<style scoped>
.student-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-button-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.student-info-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.contact-info {
  margin-top: 20px;
}

.student-tabs {
  margin-top: 20px;
}

.tab-content {
  padding: 20px 0;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.tab-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.loading-container {
  padding: 20px;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}

.plans-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.plan-card {
  height: 100%;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.plan-title {
  font-weight: bold;
  font-size: 14px;
}

.plan-actions {
  display: flex;
  gap: 10px;
}

.plan-content {
  padding: 10px 0;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.feedback-title {
  font-weight: bold;
  font-size: 14px;
}

.feedback-content {
  padding: 10px 0;
}
</style>