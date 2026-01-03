<template>
  <div class="training-plans-container">
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
        <h3>训练计划 {{ formatDateForDialog(currentPlanDetail.planDate) }}</h3>
        <el-divider />
        <h4>运动项目列表</h4>
        <div v-if="currentPlanDetail.exercises && currentPlanDetail.exercises.length > 0" class="exercises-list">
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
    
    <el-card class="plans-header-card" shadow="hover">
      <template #header>
        <div class="plans-title">
          <el-icon><Notebook /></el-icon>
          训练计划管理
        </div>
      </template>
      
      <div class="plans-info">
        <h3>学员：{{ studentName || '加载中...' }}</h3>
        <el-button type="primary" @click="navigateToPlanEdit">
          <el-icon><Plus /></el-icon>
          创建新计划
        </el-button>
      </div>
    </el-card>
    
    <el-card class="plans-list-card" shadow="hover">
      <template #header>
        <div class="card-subtitle">训练计划列表</div>
      </template>
      
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="trainingPlans.length === 0" class="empty-container">
        <el-empty description="暂无训练计划" />
        <el-button type="primary" @click="navigateToPlanEdit" style="margin-top: 20px;">
          <el-icon><Plus /></el-icon>
          创建首个训练计划
        </el-button>
      </div>
      
      <div v-else class="plans-table">
        <el-table :data="trainingPlans" stripe style="width: 100%">
          <el-table-column prop="planId" label="计划ID" width="80" />
          <el-table-column prop="planDate" label="训练日期" width="150" :formatter="formatDate" />
          <el-table-column prop="exerciseCount" label="运动项目" width="120" />
          <!-- 移除状态列，因为后端实体没有status字段 -->
          <el-table-column label="操作" width="250">
            <template #default="scope">
              <el-button type="primary" size="small" @click="navigateToPlanEdit(scope.row.planId)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="success" size="small" @click="viewPlanDetails(scope.row.planId)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button type="danger" size="small" @click="deletePlan(scope.row.planId)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Notebook, Plus, Edit, View, Delete, ArrowLeft } from '@element-plus/icons-vue';
import api from '../services/api';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const studentName = ref('');
const trainingPlans = ref([]);
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
const formatDate = (row, column, cellValue) => {
  if (!cellValue) return '';
  const date = new Date(cellValue);
  return date.toLocaleDateString('zh-CN');
};

// 格式化日期（用于弹窗）
const formatDateForDialog = (dateStr) => {
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
    console.log('获取到的计划详情响应:', response); // 添加调试信息
    currentPlanDetail.value = response.data;
  } catch (error) {
    console.error('获取计划详情失败:', error);
    ElMessage.error('获取计划详情失败');
  } finally {
    loadingPlanDetail.value = false;
  }
};

// 获取学员名称
const fetchStudentName = async () => {
  try {
    const response = await api.get(`/users/${userId.value}`);
    const userData = response.data;
    studentName.value = userData.name || userData.username;
  } catch (error) {
    console.error('获取学员名称失败:', error);
    studentName.value = '未知学员';
  }
};

// 获取训练计划列表
const fetchTrainingPlans = async () => {
  try {
    loading.value = true;
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
    loading.value = false;
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

// 删除训练计划
const deletePlan = async (planId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个训练计划吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 这里假设后端提供了删除训练计划的API
    await api.delete(`/training-plans/${planId}`);
    ElMessage.success('训练计划删除成功');
    fetchTrainingPlans();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除训练计划失败:', error);
      ElMessage.error('删除失败，请重试');
    }
  }
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
  router.push(`/coach/student/${userId.value}`);
};

onMounted(() => {
  fetchStudentName();
  fetchTrainingPlans();
});

// 当组件被激活时重新获取数据，确保从编辑页面返回时能看到最新数据
onActivated(() => {
  fetchTrainingPlans();
});
</script>

<style scoped>
.training-plans-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-button-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.plans-header-card {
  margin-bottom: 20px;
}

.plans-title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.plans-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.plans-info h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.plans-list-card {
  margin-bottom: 20px;
}

.card-subtitle {
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

.plans-table {
  padding: 10px 0;
}
</style>