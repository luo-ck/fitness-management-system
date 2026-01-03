<template>
  <div class="plan-edit-container">
    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button @click="navigateBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>
    
    <el-card class="plan-edit-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><EditPen /></el-icon>
          {{ isEditMode ? '编辑训练计划' : '创建训练计划' }}
        </div>
      </template>
      
      <el-form :model="planForm" :rules="planRules" ref="planFormRef" label-width="100px">
        <el-form-item label="训练日期" prop="planDate">
          <el-date-picker
            v-model="planForm.planDate"
            type="date"
            placeholder="选择训练日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        

      </el-form>
      
      <el-divider>运动项目</el-divider>
      
      <div class="exercises-section">
        <div class="section-header">
          <h3>运动项列表</h3>
          <el-button type="primary" @click="addExercise">
            <el-icon><Plus /></el-icon>
            添加运动项
          </el-button>
        </div>
        
        <div v-if="exercises.length === 0" class="empty-exercises">
          <el-empty description="暂无运动项" />
          <el-button type="primary" @click="addExercise" style="margin-top: 20px;">
            <el-icon><Plus /></el-icon>
            添加首个运动项
          </el-button>
        </div>
        
        <div v-else class="exercises-list">
          <el-card v-for="(exercise, index) in exercises" :key="`exercise-${index}`" class="exercise-card" shadow="hover">
            <template #header>
              <div class="exercise-header">
                <span class="exercise-index">运动项 {{ index + 1 }}</span>
                <el-button type="danger" size="small" @click="removeExercise(index)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </template>
            
            <el-form :model="exercise" :rules="exerciseRules" label-width="80px">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="运动名称" prop="name">
                    <el-select v-model="exercise.name" placeholder="选择运动名称">
                      <el-option label="跑步" value="跑步" />
                      <el-option label="游泳" value="游泳" />
                      <el-option label="举重" value="举重" />
                      <el-option label="俯卧撑" value="俯卧撑" />
                      <el-option label="仰卧起坐" value="仰卧起坐" />
                      <el-option label="深蹲" value="深蹲" />
                      <el-option label="引体向上" value="引体向上" />
                      <el-option label="跳绳" value="跳绳" />
                      <el-option label="平板卧推" value="平板卧推" />
                    </el-select>
                  </el-form-item>
                  
                  <el-form-item label="组数" prop="sets">
                    <el-input-number v-model="exercise.sets" :min="1" :max="20" placeholder="组数" />
                  </el-form-item>
                </el-col>
                
                <el-col :span="12">
                  <el-form-item label="次数" prop="reps">
                    <el-input-number v-model="exercise.reps" :min="1" :max="100" placeholder="次数" />
                  </el-form-item>
                  
                  <el-form-item label="时长(分钟)" prop="duration">
                    <el-input-number v-model="exercise.duration" :min="1" :max="120" placeholder="时长" />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <!-- 教练教学视频选择 -->
              <el-row>
                <el-col :span="24">
                  <el-form-item label="教学视频">
                    <el-select v-model="exercise.videoId" placeholder="选择教学视频" style="width: 100%">
                      <el-option label="无" value="" />
                      <el-option
                        v-for="video in trainerVideos"
                        :key="video.videoId"
                        :label="video.title || `视频 ${video.videoId}`"
                        :value="video.videoId"
                      />
                    </el-select>
                    <div class="video-upload-tip">
                      <small>提示：请先上传教学视频，然后在此选择关联的视频</small>
                    </div>
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item label="备注">
                <el-input
                  v-model="exercise.notes"
                  type="textarea"
                  :rows="2"
                  placeholder="请输入备注信息"
                />
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </div>
      
      <div class="form-actions">
        <el-button @click="navigateBack">返回</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">
          {{ isEditMode ? '保存修改' : '创建计划' }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { EditPen, Plus, Delete, ArrowLeft } from '@element-plus/icons-vue';
import api from '../services/api';

const route = useRoute();
const router = useRouter();
const planFormRef = ref(null);
const loading = ref(false);

const planForm = reactive({
  planId: null,
  planDate: new Date().toISOString().split('T')[0]
});

const exercises = ref([]);
const trainerVideos = ref([]); // 教练的教学视频列表

const userId = computed(() => route.params.userId);
const planId = computed(() => route.params.planId);
// 只有当planId是有效的数字时，才认为是编辑模式
const isEditMode = computed(() => {
  const id = planId.value;
  // 确保id不是undefined、null或空字符串，并且是有效的数字
  return id != null && id !== '' && !isNaN(parseInt(id)) && parseInt(id) > 0;
});

const planRules = {
  planDate: [{ required: true, message: '请选择训练日期', trigger: 'change' }]
};

const exerciseRules = {
  name: [{ required: true, message: '请选择运动名称', trigger: 'change' }],
  sets: [{ required: true, message: '请输入组数', trigger: 'blur' }],
  reps: [{ required: true, message: '请输入次数', trigger: 'blur' }]
  // 移除对notes字段的验证，因为后端实体类中没有这个字段
};

// 获取教练的教学视频列表
const fetchTrainerVideos = async () => {
  try {
    // 从authStore获取当前用户，而不是直接从localStorage读取
    const coachUser = JSON.parse(localStorage.getItem('user'));
    // 教练的ID字段是coachId，不是userId
    const coachId = coachUser ? (coachUser.coachId || coachUser.userId) : null;
    if (!coachId) {
      console.error('获取教练ID失败，coachUser:', coachUser);
      return;
    }
    
    console.log('=== 获取教练视频列表 ===');
    console.log('教练ID:', coachId);
    const response = await api.get(`/trainer/videos/coach/${coachId}`);
    console.log('视频列表响应:', response.data);
    trainerVideos.value = response.data;
  } catch (error) {
    console.error('获取教练视频列表失败:', error);
    if (error.response) {
      console.error('响应状态:', error.response.status);
      console.error('响应数据:', error.response.data);
    }
  }
};

// 添加运动项
const addExercise = () => {
  exercises.value.push({
    name: '',
    sets: 3,
    reps: 10,
    duration: null,
    videoId: null // 添加视频ID字段
  });
};

// 删除运动项
const removeExercise = (index) => {
  exercises.value.splice(index, 1);
};

// 获取训练计划详情
const fetchPlanDetails = async () => {
  try {
    loading.value = true;
    
    // 再次验证是否真的是编辑模式，防止在创建新计划时调用
    if (!isEditMode.value) {
      return;
    }
    
    // 验证planId是否为有效的数字
    const parsedPlanId = parseInt(planId.value, 10);
    if (isNaN(parsedPlanId) || parsedPlanId <= 0) {
      ElMessage.warning('无效的计划ID');
      navigateBack();
      return;
    }
    
    const response = await api.get(`/training-plans/${parsedPlanId}`);
    const planData = response.data;
    
    if (!planData) {
      ElMessage.warning('未找到该训练计划');
      navigateBack();
      return;
    }
    
    // 填充计划信息
    planForm.planId = planData.planId;
    planForm.planDate = planData.planDate;
    
    // 填充运动项，将exerciseId转换为运动名称
      if (planData.exercises && planData.exercises.length > 0) {
        // 运动Id到名称的映射表（用于反向转换）
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
        
        exercises.value = planData.exercises.map(exercise => ({
          // 将exerciseId转换为运动名称，用于表单显示
          name: exerciseIdToNameMap[exercise.exerciseId] || `运动${exercise.exerciseId}`,
          sets: exercise.sets,
          reps: exercise.reps,
          duration: exercise.duration,
          videoId: exercise.videoId || null // 保留视频ID
        }));
      } else {
        exercises.value = [];
      }
  } catch (error) {
    console.error('获取计划详情失败:', error);
    if (error.response) {
      const { status, data } = error.response;
      if (status === 404) {
        ElMessage.warning('未找到该训练计划');
        navigateBack();
      } else if (status === 400) {
        ElMessage.warning('无效的请求参数，请检查计划ID');
        navigateBack();
      } else {
        ElMessage.error(`获取计划详情失败，状态码: ${status}`);
        console.error('错误详情:', data);
      }
    } else {
      ElMessage.error('获取计划详情失败，请检查网络连接');
    }
  } finally {
    loading.value = false;
  }
};

// 提交表单
const submitForm = async () => {
  if (!planFormRef.value) return;
  
  // 验证计划基本信息
  const planValid = await planFormRef.value.validate();
  if (!planValid) {
    ElMessage.warning('请填写完整计划信息');
    return;
  }
  
  // 验证运动项
  let exercisesValid = true;
  for (const exercise of exercises.value) {
    if (!exercise.name || !exercise.sets || !exercise.reps) {
      exercisesValid = false;
      break;
    }
  }
  
  if (!exercisesValid) {
    ElMessage.warning('请填写完整的运动项信息');
    return;
  }
  
  if (exercises.value.length === 0) {
    ElMessage.warning('请至少添加一个运动项');
    return;
  }
  
  try {
      loading.value = true;
      
      // 从localStorage获取教练信息
      const coachUser = JSON.parse(localStorage.getItem('user'));
      const coachId = coachUser ? coachUser.coachId : null;
      
      // 准备提交的数据，只包含后端需要的字段
      const planData = {
        ...planForm,
        userId: userId.value,
        coachId: coachId // 添加教练ID
      };
      
      let response;
      let currentPlanId;
      
      // 运动名称到exerciseId的映射表
      const exerciseNameToIdMap = {
        '跑步': 1,
        '游泳': 2,
        '举重': 3,
        '俯卧撑': 4,
        '仰卧起坐': 5,
        '深蹲': 6,
        '引体向上': 7,
        '跳绳': 8,
        '平板卧推': 9
      };
      
      // 准备完整的提交数据，包含运动项
      const fullPlanData = {
        ...planData,
        exercises: exercises.value.map((exercise, index) => {
          // 确保exerciseId存在且有效，防止外键约束错误
          const exerciseId = exerciseNameToIdMap[exercise.name];
          if (!exerciseId) {
            console.error(`运动项 ${exercise.name} 没有对应的exerciseId映射`);
            // 抛出错误，阻止提交
            throw new Error(`运动项 ${exercise.name} 配置错误`);
          }
          
          return {
            exerciseId: exerciseId,
            sets: exercise.sets,
            reps: exercise.reps,
            duration: exercise.duration,
            orderIndex: index,
            videoId: exercise.videoId || null // 添加视频ID
          };
        })
      };
      
      console.log('=== 准备提交训练计划 ===');
      console.log('计划ID:', planId.value);
      console.log('是否编辑模式:', isEditMode.value);
      console.log('提交数据:', fullPlanData);
      
      if (isEditMode.value) {
        // 编辑现有计划
        console.log('发送PUT请求:', `/training-plans/${planId.value}`);
        response = await api.put(`/training-plans/${planId.value}`, fullPlanData);
        console.log('PUT请求成功:', response);
        ElMessage.success('训练计划更新成功');
      } else {
        // 创建新计划
        console.log('发送POST请求:', '/training-plans');
        response = await api.post('/training-plans', fullPlanData);
        console.log('POST请求成功:', response);
        ElMessage.success('训练计划创建成功');
      }
      
      // 导航回计划列表
      console.log('导航回计划列表');
      navigateBack();
    } catch (error) {
      console.error('=== 提交训练计划失败 ===');
      console.error('错误类型:', typeof error);
      console.error('错误对象:', error);
      if (error.response) {
        console.error('响应状态:', error.response.status);
        console.error('响应数据:', error.response.data);
        console.error('响应头:', error.response.headers);
        ElMessage.error(`${isEditMode.value ? '更新' : '创建'}失败，状态码: ${error.response.status}`);
      } else if (error.request) {
        console.error('请求已发送但未收到响应:', error.request);
        ElMessage.error(`${isEditMode.value ? '更新' : '创建'}失败，未收到服务器响应`);
      } else {
        console.error('请求配置错误:', error.message);
        ElMessage.error(`${isEditMode.value ? '更新' : '创建'}失败，请求配置错误: ${error.message}`);
      }
    } finally {
      loading.value = false;
      console.log('=== 提交训练计划流程结束 ===');
    }
};

// 返回计划列表
const navigateBack = () => {
  router.push(`/coach/student/${userId.value}/plans`);
};

onMounted(() => {
  fetchTrainerVideos(); // 获取教练的教学视频列表
  if (isEditMode.value) {
    fetchPlanDetails();
  }
});
</script>

<style scoped>
.plan-edit-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.back-button-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.plan-edit-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.exercises-section {
  margin: 20px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.empty-exercises {
  padding: 40px 0;
  text-align: center;
}

.exercises-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.exercise-card {
  margin-bottom: 10px;
}

.exercise-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exercise-index {
  font-weight: bold;
  font-size: 14px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>