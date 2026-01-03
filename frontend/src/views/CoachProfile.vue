<template>
  <div class="coach-profile-container">
    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button @click="navigateBack">
        <el-icon><ArrowLeft /></el-icon>
        返回控制台
      </el-button>
    </div>

    <el-card class="profile-header-card" shadow="hover">
      <template #header>
        <div class="profile-title">
          <el-icon><User /></el-icon>
          我的个人中心
        </div>
      </template>
      
      <div class="profile-nav">
        <el-button 
          :type="activeTab === 'profile' ? 'primary' : ''" 
          @click="activeTab = 'profile'"
          size="large"
        >
          <el-icon><User /></el-icon>
          个人信息
        </el-button>
        <el-button 
          :type="activeTab === 'videos' ? 'primary' : ''" 
          @click="activeTab = 'videos'"
          size="large"
        >
          <el-icon><VideoPlay /></el-icon>
          教学视频
        </el-button>
        <el-button 
          :type="activeTab === 'ratings' ? 'primary' : ''" 
          @click="activeTab = 'ratings'"
          size="large"
        >
          <el-icon><StarFilled /></el-icon>
          我的评价
        </el-button>
        <div class="message-center-button-wrapper">
          <el-button 
            type="primary"
            @click="navigateToChat"
            size="large"
            class="message-center-button"
          >
            <el-icon><ChatDotRound /></el-icon>
            消息中心
          </el-button>
          <el-badge v-if="hasUnreadMessages" :value="unreadCount" type="danger" class="message-badge" :hidden="!hasUnreadMessages" />
        </div>
      </div>
    </el-card>

    <!-- 个人信息编辑 -->
    <el-card v-if="activeTab === 'profile'" class="profile-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><User /></el-icon>
          个人信息
        </div>
      </template>
      
      <el-form :model="coachForm" :rules="coachRules" ref="coachFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教练ID" prop="coachId">
              <el-input v-model="coachForm.coachId" disabled placeholder="教练ID" />
            </el-form-item>
            
            <el-form-item label="用户名" prop="username">
              <el-input v-model="coachForm.username" disabled placeholder="用户名" />
            </el-form-item>
            
            <el-form-item label="姓名" prop="name">
              <el-input v-model="coachForm.name" placeholder="请输入姓名" />
            </el-form-item>
            
            <el-form-item label="联系方式" prop="contact">
              <el-input v-model="coachForm.contact" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="专业领域" prop="specialty">
              <el-input v-model="coachForm.specialty" placeholder="请输入专业领域" />
            </el-form-item>
            
            <el-form-item label="简介" prop="intro">
              <el-input
                v-model="coachForm.intro"
                type="textarea"
                :rows="3"
                placeholder="请输入个人简介"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <div class="form-actions">
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="submitProfile" :loading="savingProfile">
            保存修改
          </el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 教学视频管理 -->
    <el-card v-if="activeTab === 'videos'" class="videos-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><VideoPlay /></el-icon>
          我的教学视频
        </div>
      </template>
      
      <div class="videos-header">
        <h3>视频列表</h3>
        <el-button type="primary" @click="showUploadDialog = true">
          <el-icon><Plus /></el-icon>
          上传新视频
        </el-button>
      </div>
      
      <!-- 视频上传对话框 -->
      <el-dialog
        v-model="showUploadDialog"
        title="上传教学视频"
        width="600px"
        :before-close="handleCloseUploadDialog"
      >
        <el-form :model="videoForm" :rules="videoRules" ref="videoFormRef" label-width="100px">
          <el-form-item label="视频标题" prop="title">
            <el-input v-model="videoForm.title" placeholder="请输入视频标题" />
          </el-form-item>
          
          <el-form-item label="视频描述" prop="description">
            <el-input
              v-model="videoForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入视频描述"
            />
          </el-form-item>
          
          <el-form-item label="视频上传" prop="videoUrl">
              <el-radio-group v-model="uploadMethod" @change="onUploadMethodChange">
                <el-radio label="url">视频URL</el-radio>
                <el-radio label="file">本地文件</el-radio>
              </el-radio-group>
              
              <el-input 
                v-if="uploadMethod === 'url'" 
                v-model="videoForm.videoUrl" 
                placeholder="请输入视频的完整URL地址"
              />
              
              <el-upload
                v-else
                v-model:file-list="videoFileList"
                class="upload-demo"
                action=""
                :auto-upload="false"
                :on-change="onFileChange"
                accept="video/*"
              >
                <el-button type="primary" size="small">
                  <el-icon><Plus /></el-icon>
                  选择视频文件
                </el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持MP4、WebM等格式，文件大小不超过100MB
                  </div>
                </template>
              </el-upload>
              <div v-if="videoForm.videoFile" class="selected-file-info">
                <el-icon><VideoPlay /></el-icon>
                {{ videoForm.videoFile.name }}
              </div>
            </el-form-item>
        </el-form>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="showUploadDialog = false">取消</el-button>
            <el-button type="primary" @click="uploadVideo" :loading="uploadingVideo">
              上传视频
            </el-button>
          </div>
        </template>
      </el-dialog>
      
      <div v-if="loadingVideos" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="videos.length === 0" class="empty-container">
        <el-empty description="暂无教学视频" />
        <el-button type="primary" @click="showUploadDialog = true" style="margin-top: 20px;">
          <el-icon><Plus /></el-icon>
          上传首个视频
        </el-button>
      </div>
      
      <div v-else class="videos-list">
        <el-card 
          v-for="video in filteredVideos" 
          :key="video.videoId" 
          class="video-card" 
          shadow="hover"
        >
          <template #header>
            <div class="video-header">
              <span class="video-title">{{ video.title || `视频 ${video.videoId}` }}</span>
              <el-button type="danger" size="small" @click="deleteVideo(video.videoId)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
          
          <div class="video-content">
            <div class="video-preview">
              <el-icon class="video-icon"><VideoPlay /></el-icon>
              <el-button type="primary" size="small" @click="viewVideo(video.videoId)">
                <el-icon><VideoPlay /></el-icon>
                查看视频
              </el-button>
            </div>
            <div class="video-info">
              <p><strong>视频URL：</strong><span class="video-url">{{ video.videoUrl }}</span></p>
              <p><strong>描述：</strong>{{ video.description || '暂无描述' }}</p>
              <p><strong>上传时间：</strong>{{ formatDate(video.uploadDate) }}</p>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>
    
    <!-- 我的评价 -->
    <el-card v-if="activeTab === 'ratings'" class="ratings-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><StarFilled /></el-icon>
          我的评价
        </div>
      </template>
      
      <div v-if="authStore.currentUser">
        <CoachRatingList :coach-id="authStore.currentUser.coachId" />
      </div>
      <div v-else class="empty-container">
        <el-empty description="加载教练信息失败" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../store/authStore';
import { useMessageStore } from '../store/messageStore';
import api from '../services/api';
import { User, VideoPlay, Plus, Delete, ArrowLeft, StarFilled, ChatDotRound } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import CoachRatingList from '../components/CoachRatingList.vue';

const router = useRouter();
const authStore = useAuthStore();
const messageStore = useMessageStore();
const activeTab = ref('profile');

// 未读消息相关
const unreadCount = computed(() => messageStore.unreadCount);
const hasUnreadMessages = computed(() => messageStore.hasUnreadMessages);

// 个人信息表单
const coachFormRef = ref(null);
const coachForm = reactive({
    coachId: '',
    username: '',
    name: '',
    contact: '',
    specialty: '',
    intro: ''
  });

const coachRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
};

// 视频管理
const videos = ref([]);
const loadingVideos = ref(false);
const savingProfile = ref(false);
const uploadingVideo = ref(false);
const showUploadDialog = ref(false);
const uploadMethod = ref('url'); // 'url' 或 'file'
const videoFileList = ref([]);

// 计算属性：过滤后的视频列表
const filteredVideos = computed(() => {
  // 确保videos.value是数组，并且只返回有效的视频项
  return Array.isArray(videos.value) 
    ? videos.value.filter(video => video && video.videoId) 
    : [];
});

// 视频上传表单
const videoFormRef = ref(null);
const videoForm = reactive({
    title: '',
    description: '',
    videoUrl: '',
    videoFile: null
  });

const videoRules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }]
};

// 获取教练信息
const fetchCoachInfo = async () => {
  try {
    const coach = authStore.currentUser;
    if (coach) {
      // 调用API获取完整的教练信息
      const response = await api.get(`/coaches/${coach.coachId}`);
      const coachData = response.data;
      
      // 填充表单数据
      Object.assign(coachForm, coachData);
    }
  } catch (error) {
    console.error('获取教练信息失败:', error);
    ElMessage.error('获取教练信息失败');
  }
};

// 获取教练的教学视频
const fetchVideos = async () => {
  try {
    loadingVideos.value = true;
    const coach = authStore.currentUser;
    console.log('=== 获取教练视频列表 ===');
    console.log('当前教练信息:', coach);
    
    if (!coach) {
      console.error('获取视频列表失败: 教练信息为空');
      ElMessage.error('获取视频列表失败: 教练信息为空');
      videos.value = [];
      return;
    }
    
    // 确保使用正确的教练ID字段名
    const coachId = coach.coachId || coach.userId || coach.id;
    console.log('使用的教练ID:', coachId);
    
    const response = await api.get(`/trainer/videos/coach/${coachId}`);
    console.log('=== 获取视频列表成功 ===');
    console.log('响应状态:', response.status);
    console.log('响应数据:', response.data);
    // 确保videos.value始终是一个数组
    videos.value = Array.isArray(response.data) ? response.data : [];
  } catch (error) {
    console.error('=== 获取视频列表失败 ===');
    console.error('错误类型:', typeof error);
    console.error('错误对象:', error);
    if (error.response) {
      console.error('响应状态:', error.response.status);
      console.error('响应数据:', error.response.data);
      ElMessage.error(`获取视频列表失败: ${error.response.status} - ${error.response.data}`);
    } else if (error.request) {
      console.error('请求已发送但未收到响应:', error.request);
      ElMessage.error('获取视频列表失败: 网络连接问题');
    } else {
      console.error('请求配置错误:', error.message);
      ElMessage.error(`获取视频列表失败: ${error.message}`);
    }
    videos.value = [];
  } finally {
    loadingVideos.value = false;
    console.log('=== 获取视频列表流程结束 ===');
  }
};

// 保存教练信息
const submitProfile = async () => {
  if (!coachFormRef.value) return;
  
  const valid = await coachFormRef.value.validate();
  if (!valid) {
    ElMessage.warning('请填写完整的个人信息');
    return;
  }
  
  try {
    savingProfile.value = true;
    
    // 调用API更新教练信息
    const response = await api.put(`/coaches/${coachForm.coachId}`, coachForm);
    
    // 更新本地存储的教练信息
    authStore.setCurrentUser(response.data);
    
    ElMessage.success('个人信息更新成功');
  } catch (error) {
    console.error('更新个人信息失败:', error);
    ElMessage.error('更新个人信息失败');
  } finally {
    savingProfile.value = false;
  }
};

// 上传方法切换事件
const onUploadMethodChange = () => {
  // 切换上传方法时重置表单
  if (uploadMethod.value === 'url') {
    videoForm.videoFile = null;
    videoFileList.value = [];
  } else {
    videoForm.videoUrl = '';
  }
};

// 文件选择事件
const onFileChange = (file) => {
  videoForm.videoFile = file.raw;
  videoFileList.value = [file];
};

// 上传新视频
const uploadVideo = async () => {
  if (!videoFormRef.value) return;
  
  // 自定义表单验证
  if (!videoForm.title) {
    ElMessage.warning('请输入视频标题');
    return;
  }
  
  // 根据上传方式验证不同字段
  if (uploadMethod.value === 'url' && !videoForm.videoUrl) {
    ElMessage.warning('请输入视频URL');
    return;
  } else if (uploadMethod.value === 'file' && !videoForm.videoFile) {
    ElMessage.warning('请选择视频文件');
    return;
  }
  
  try {
    uploadingVideo.value = true;
    
    const coach = authStore.currentUser;
    if (!coach) return;
    
    if (uploadMethod.value === 'url') {
      // URL上传方式
      const videoData = {
        title: videoForm.title,
        description: videoForm.description,
        videoUrl: videoForm.videoUrl,
        coachId: coach.coachId
      };
      
      await api.post('/trainer/videos', videoData);
    } else {
      // 文件上传方式
      const formData = new FormData();
      formData.append('title', String(videoForm.title));
      formData.append('description', String(videoForm.description));
      formData.append('coachId', String(coach.coachId));
      formData.append('videoFile', videoForm.videoFile);
      
      console.log('=== 准备发送文件上传请求 ===');
      console.log('title:', videoForm.title);
      console.log('description:', videoForm.description);
      console.log('coachId:', coach.coachId);
      console.log('videoFile:', videoForm.videoFile);
      console.log('formData has title:', formData.has('title'));
      console.log('formData has description:', formData.has('description'));
      console.log('formData has coachId:', formData.has('coachId'));
      console.log('formData has videoFile:', formData.has('videoFile'));
      
      await api.post('/trainer/videos/upload', formData);
      console.log('=== 文件上传请求发送成功 ===');
    }
    
    ElMessage.success('视频上传成功');
    showUploadDialog.value = false;
    
    // 清空表单
    Object.assign(videoForm, {
      title: '',
      description: '',
      videoUrl: '',
      videoFile: null
    });
    videoFileList.value = [];
    uploadMethod.value = 'url';
    
    // 重新获取视频列表
    fetchVideos();
  } catch (error) {
    console.error('上传视频失败:', error);
    ElMessage.error('上传视频失败');
  } finally {
    uploadingVideo.value = false;
  }
};

// 删除视频
const deleteVideo = async (videoId) => {
  try {
    await api.delete(`/trainer/videos/${videoId}`);
    ElMessage.success('视频删除成功');
    
    // 从列表中移除该视频
    videos.value = videos.value.filter(video => video.videoId !== videoId);
  } catch (error) {
    console.error('删除视频失败:', error);
    ElMessage.error('删除视频失败');
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

// 处理上传对话框关闭
const handleCloseUploadDialog = () => {
  // 清空表单
  Object.assign(videoForm, {
    title: '',
    description: '',
    videoUrl: ''
  });
  showUploadDialog.value = false;
};

// 重置表单
const resetForm = () => {
  fetchCoachInfo();
};

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 返回教练控制台
const navigateBack = () => {
  router.push('/coach/dashboard');
};

// 跳转到消息中心
const navigateToChat = () => {
  router.push('/coach/chat');
};

// 监听标签页切换，加载对应数据
watch(activeTab, (newTab) => {
  if (newTab === 'videos') {
    fetchVideos();
  }
});

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/coach/login');
    return;
  }
  
  fetchCoachInfo();
  if (activeTab.value === 'videos') {
    fetchVideos();
  }
  
  // 获取对话列表，初始化未读消息计数
  try {
    const userId = authStore.currentUser?.userId || authStore.currentUser?.coachId;
    if (userId) {
      const response = await api.get(`/messages/conversations/${userId}`);
      const conversations = response.data || [];
      console.log('获取到的对话列表:', conversations);
      
      // 更新全局未读消息计数
      messageStore.resetAllUnreadCounts();
      conversations.forEach(conversation => {
        if (conversation.unreadCount > 0) {
          messageStore.setConversationUnreadCount(conversation.otherId, conversation.unreadCount);
        }
      });
      console.log('初始化后的全局未读消息计数:', messageStore.unreadCount);
    }
  } catch (err) {
    console.error('获取对话列表失败:', err);
  }
});
</script>

<style scoped>
.coach-profile-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-button-container {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-start;
}

.profile-header-card {
  margin-bottom: 20px;
}

.profile-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.profile-nav {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.profile-card,
.videos-card {
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.videos-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.videos-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.form-tip {
  margin-top: 10px;
  color: #909399;
}

.loading-container {
  padding: 20px;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}

.videos-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.video-card {
  height: 100%;
}

.video-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.video-title {
  font-weight: bold;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.video-content {
  padding: 10px 0;
}

.video-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background-color: #f0f2f5;
  border-radius: 4px;
  margin-bottom: 15px;
}

.video-icon {
  font-size: 60px;
  color: #909399;
  margin-bottom: 20px;
}

.video-info p {
  margin: 10px 0;
  font-size: 14px;
  line-height: 1.5;
}

.video-url {
  color: #409eff;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  max-width: 100%;
}

.video-url:hover {
  text-decoration: underline;
}

/* 消息中心按钮样式 */
.message-center-button-wrapper {
  position: relative;
  display: inline-block;
}

.message-center-button {
  /* 确保按钮有足够的内边距，避免badge遮挡文字 */
  padding-right: 20px;
}

/* 调整badge位置 */
.message-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  transform: translate(50%, -50%);
  z-index: 10;
}
</style>
