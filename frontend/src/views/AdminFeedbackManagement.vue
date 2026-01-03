<template>
  <div class="admin-feedback-container">
    <!-- 返回按钮 -->
    <div class="back-button-container">
      <el-button type="primary" @click="$router.push('/admin/dashboard')" class="back-button">
        <el-icon><Back /></el-icon>
        返回控制台
      </el-button>
    </div>

    <el-card class="feedback-header-card" shadow="hover">
      <template #header>
        <div class="feedback-title">
          <el-icon><ChatDotRound /></el-icon>
          反馈管理
        </div>
      </template>
      
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索反馈标题或内容" 
          prefix-icon="Search"
          clearable
          @input="handleSearch"
        >
          <template #append>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </template>
        </el-input>
      </div>
    </el-card>

    <!-- 反馈列表 -->
    <el-card class="feedback-list-card" shadow="hover">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="feedbacks.length === 0" class="empty-container">
        <el-empty description="暂无反馈" />
      </div>
      
      <div v-else class="feedback-list">
        <el-table :data="filteredFeedbacks" stripe style="width: 100%">
          <el-table-column prop="id" label="反馈ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="content" label="内容" min-width="300">
            <template #default="scope">
              {{ truncateText(scope.row.content, 50) }}
            </template>
          </el-table-column>
          <el-table-column prop="senderId" label="用户ID" width="100" />
          <el-table-column prop="sendTime" label="提交时间" width="180">
            <template #default="scope">
              {{ formatTime(scope.row.sendTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewFeedback(scope.row)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              <el-button type="success" size="small" @click="replyFeedback(scope.row)">
                <el-icon><EditPen /></el-icon>
                回复
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="filteredFeedbacks.length"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 反馈详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedFeedback ? '反馈详情' : '回复反馈'"
      width="600px"
    >
      <div v-if="selectedFeedback" class="feedback-detail">
        <div class="detail-item">
          <span class="label">反馈ID: </span>
          <span class="value">{{ selectedFeedback.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">标题: </span>
          <span class="value">{{ selectedFeedback.title }}</span>
        </div>
        <div class="detail-item">
          <span class="label">用户ID: </span>
          <span class="value">{{ selectedFeedback.senderId }}</span>
        </div>
        <div class="detail-item">
          <span class="label">提交时间: </span>
          <span class="value">{{ formatTime(selectedFeedback.sendTime) }}</span>
        </div>
        <div class="detail-item detail-content">
          <span class="label">内容: </span>
          <div class="value">{{ selectedFeedback.content }}</div>
        </div>
      </div>
      
      <!-- 回复表单 -->
      <div v-if="showReplyForm" class="reply-form-container">
        <el-form :model="replyForm" :rules="replyRules" ref="replyFormRef" label-width="80px">
          <el-form-item label="回复内容" prop="content">
            <el-input 
              v-model="replyForm.content" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入回复内容" 
            />
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button 
            v-if="showReplyForm" 
            type="primary" 
            @click="submitReply" 
            :loading="submittingReply"
          >
            发送回复
          </el-button>
          <el-button 
            v-else 
            type="success" 
            @click="showReplyForm = true"
          >
            <el-icon><EditPen /></el-icon>
            回复
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ChatDotRound, Back, Search, View, EditPen } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '../services/api';

const router = useRouter();

// 加载状态
const loading = ref(false);
// 搜索关键词
const searchKeyword = ref('');
// 当前页码
const currentPage = ref(1);
// 每页条数
const pageSize = ref(10);
// 反馈列表
const feedbacks = ref([]);
// 对话框可见性
const dialogVisible = ref(false);
// 选中的反馈
const selectedFeedback = ref(null);
// 显示回复表单
const showReplyForm = ref(false);
// 回复表单
const replyFormRef = ref(null);
const submittingReply = ref(false);
const replyForm = ref({
  content: ''
});

// 回复表单验证规则
const replyRules = {
  content: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return date.toLocaleString();
};

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
};

// 过滤后的反馈列表
const filteredFeedbacks = computed(() => {
  let result = [...feedbacks.value];
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(item => 
      item.title.toLowerCase().includes(keyword) ||
      item.content.toLowerCase().includes(keyword)
    );
  }
  
  return result;
});

// 获取反馈列表
const fetchFeedbacks = async () => {
  loading.value = true;
  try {
    // 获取管理员的消息列表，筛选出类型为system_feedback的消息
    const currentAdmin = JSON.parse(localStorage.getItem('admin'));
    // 管理员对象的ID字段是adminId，不是id
    const adminId = currentAdmin?.adminId || 1; // 默认管理员ID为1
    const response = await api.get(`/messages/receiver/${adminId}/admin`);
    
    console.log('获取到的所有管理员消息:', response.data);
    
    // 筛选出消息类型为system_feedback的消息，这些是用户提交的意见反馈
    feedbacks.value = (response.data || []).filter(message => message.messageType === 'system_feedback');
    
    console.log('筛选后的system_feedback消息:', feedbacks.value);
  } catch (err) {
    console.error('获取反馈列表失败:', err);
    ElMessage.error('获取反馈列表失败: ' + (err.response?.data?.message || err.message));
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
};

// 查看反馈详情
const viewFeedback = (feedback) => {
  selectedFeedback.value = feedback;
  showReplyForm.value = false;
  dialogVisible.value = true;
};

// 回复反馈
const replyFeedback = (feedback) => {
  selectedFeedback.value = feedback;
  showReplyForm.value = true;
  dialogVisible.value = true;
  // 重置回复表单
  replyForm.value.content = '';
};

// 提交回复
const submitReply = async () => {
  if (!replyFormRef.value || !selectedFeedback.value) return;
  
  try {
    await replyFormRef.value.validate();
    
    submittingReply.value = true;
    
    // 发送回复消息给用户
    const messageData = {
      senderId: 1, // 管理员ID，实际应该从登录信息获取
      senderName: '管理员',
      receiverId: selectedFeedback.value.senderId,
      receiverType: 'user',
      messageType: 'feedback_reply',
      title: '反馈回复',
      content: replyForm.value.content,
      relatedId: selectedFeedback.value.id,
      isRead: false,
      sendTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
    };
    
    await api.post('/messages', messageData);
    ElMessage.success('回复发送成功');
    
    // 关闭对话框
    dialogVisible.value = false;
    showReplyForm.value = false;
  } catch (error) {
    if (error.name === 'ValidateError') {
      return;
    }
    ElMessage.error('发送回复失败: ' + (error.response?.data?.message || error.message));
  } finally {
    submittingReply.value = false;
  }
};

// 页面加载时获取反馈列表
onMounted(() => {
  fetchFeedbacks();
});
</script>

<style scoped>
.admin-feedback-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.back-button-container {
  margin-bottom: 20px;
}

.feedback-header-card {
  margin-bottom: 20px;
}

.feedback-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-filter {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-filter .el-input {
  flex: 1;
  min-width: 300px;
}

.feedback-list-card {
  margin-bottom: 20px;
}

.loading-container {
  padding: 20px 0;
}

.empty-container {
  padding: 40px 0;
  text-align: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.feedback-detail {
  padding: 10px 0;
}

.detail-item {
  margin-bottom: 15px;
  font-size: 14px;
}

.detail-item .label {
  font-weight: bold;
  color: #606266;
  margin-right: 10px;
  display: inline-block;
  width: 80px;
  vertical-align: top;
}

.detail-item .value {
  color: #303133;
  display: inline-block;
  vertical-align: top;
  width: calc(100% - 90px);
}

.detail-content .value {
  white-space: pre-wrap;
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
}

.reply-form-container {
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>