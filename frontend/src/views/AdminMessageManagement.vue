<template>
  <div class="messages-container">
    <el-card class="messages-card" shadow="hover">
      <template #header>
        <div class="messages-title">
          <el-icon><Message /></el-icon>
          消息管理
        </div>
      </template>
      
      <!-- 返回按钮 -->
      <el-button type="primary" @click="$router.push('/admin/dashboard')" class="back-button">
        <el-icon><Back /></el-icon>
        返回控制台
      </el-button>
      
      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索标题、内容或发送者" 
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
        
        <el-select v-model="statusFilter" placeholder="按状态筛选" clearable>
          <el-option label="全部" value="" />
          <el-option label="未读" value="false" />
          <el-option label="已读" value="true" />
        </el-select>
      </div>
      
      <!-- 消息列表 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="messages.length === 0" class="empty-container">
        <el-empty description="暂无消息" />
      </div>
      
      <div v-else class="messages-list">
        <el-table :data="filteredMessages" stripe style="width: 100%">
          <el-table-column prop="id" label="消息ID" width="80" />
          <el-table-column prop="title" label="标题" width="200" />
          <el-table-column prop="senderName" label="发送者" width="120" />
          <el-table-column prop="receiverId" label="接收者ID" width="100" />
          <el-table-column prop="receiverType" label="接收者类型" width="100">
            <template #default="scope">
              <el-tag 
                :type="scope.row.receiverType === 'user' ? 'primary' : 
                       scope.row.receiverType === 'coach' ? 'success' : 'warning'">
                {{ scope.row.receiverType === 'user' ? '用户' : 
                   scope.row.receiverType === 'coach' ? '教练' : '管理员' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="isRead" label="状态" width="80">
            <template #default="scope">
              <el-tag 
                :type="scope.row.isRead ? 'success' : 'warning'">
                {{ scope.row.isRead ? '已读' : '未读' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sendTime" label="发送时间" width="180">
            <template #default="scope">{{ formatTime(scope.row.sendTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewMessage(scope.row)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              
              <el-button type="success" size="small" @click="markAsRead(scope.row)" v-if="!scope.row.isRead">
                <el-icon><CircleCheck /></el-icon>
                标记已读
              </el-button>
              
              <el-button type="danger" size="small" @click="deleteMessage(scope.row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[5, 10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="filteredMessages.length"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
    
    <!-- 消息详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="selectedMessage?.title"
      width="500px"
    >
      <div class="message-detail">
        <div class="detail-item">
          <span class="label">发送者: </span>
          <span class="value">{{ selectedMessage?.senderName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">接收者ID: </span>
          <span class="value">{{ selectedMessage?.receiverId }}</span>
        </div>
        <div class="detail-item">
          <span class="label">接收者类型: </span>
          <span class="value">
            <el-tag 
              :type="selectedMessage?.receiverType === 'user' ? 'primary' : 
                     selectedMessage?.receiverType === 'coach' ? 'success' : 'warning'">
              {{ selectedMessage?.receiverType === 'user' ? '用户' : 
                 selectedMessage?.receiverType === 'coach' ? '教练' : '管理员' }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item">
          <span class="label">发送时间: </span>
          <span class="value">{{ formatTime(selectedMessage?.sendTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态: </span>
          <span class="value">
            <el-tag 
              :type="selectedMessage?.isRead ? 'success' : 'warning'">
              {{ selectedMessage?.isRead ? '已读' : '未读' }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item" v-if="selectedMessage?.readTime">
          <span class="label">阅读时间: </span>
          <span class="value">{{ formatTime(selectedMessage?.readTime) }}</span>
        </div>
        <el-divider />
        <div class="detail-content">{{ selectedMessage?.content }}</div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="success" @click="markAsRead(selectedMessage); dialogVisible = false" v-if="!selectedMessage?.isRead">
            标记已读
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Message, Back, Search, View, CircleCheck, Delete } from '@element-plus/icons-vue';
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus';
import api from '../services/api';

const router = useRouter();

const loading = ref(false);
const messages = ref([]);
const searchKeyword = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

// 对话框相关
const dialogVisible = ref(false);
const selectedMessage = ref(null);

// 当前管理员信息
const currentAdmin = computed(() => {
  const adminStr = localStorage.getItem('admin');
  return adminStr ? JSON.parse(adminStr) : null;
});

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  return date.toLocaleString();
};

// 过滤后的消息列表
const filteredMessages = computed(() => {
  let result = [...messages.value];
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(item => 
      item.title.toLowerCase().includes(keyword) ||
      item.content.toLowerCase().includes(keyword) ||
      item.senderName.toLowerCase().includes(keyword)
    );
  }
  
  // 状态过滤
  if (statusFilter.value !== '') {
    const isRead = statusFilter.value === 'true';
    result = result.filter(item => item.isRead === isRead);
  }
  
  return result;
});

// 获取消息列表
const fetchMessages = async () => {
  if (!currentAdmin.value) return;
  
  loading.value = true;
  try {
    // 这里应该调用获取所有消息的API，目前先用获取管理员自己的消息代替
    // 后续可以扩展为获取所有消息的API
    const response = await api.get(`/messages/receiver/${currentAdmin.value.id}/admin`);
    messages.value = response.data || [];
  } catch (err) {
    console.error('获取消息失败:', err);
    ElMessage.error('获取消息失败: ' + (err.response?.data?.message || err.message));
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
  currentPage.value = 1;
};

// 处理当前页变化
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

// 查看消息详情
const viewMessage = (message) => {
  selectedMessage.value = message;
  dialogVisible.value = true;
  // 如果消息未读，自动标记为已读
  if (!message.isRead) {
    markAsRead(message);
  }
};

// 标记消息为已读
const markAsRead = async (message) => {
  if (!message || message.isRead) return;
  
  let loadingInstance = null;
  try {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '处理中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    
    const response = await api.put(`/messages/${message.id}/read`);
    if (response.data) {
      message.isRead = true;
      ElMessage.success('消息已标记为已读');
    }
  } catch (err) {
    console.error('标记消息已读失败:', err);
    ElMessage.error('标记消息已读失败: ' + (err.response?.data?.message || err.message));
  } finally {
    if (loadingInstance) {
      loadingInstance.close();
    }
  }
};

// 删除消息
const deleteMessage = async (message) => {
  let loadingInstance = null;
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    loadingInstance = ElLoading.service({
      lock: true,
      text: '删除中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    
    const response = await api.delete(`/messages/${message.id}`);
    if (response.data) {
      messages.value = messages.value.filter(item => item.id !== message.id);
      ElMessage.success('消息删除成功');
    }
  } catch (err) {
    if (err !== 'cancel') {
      console.error('删除消息失败:', err);
      ElMessage.error('删除消息失败: ' + (err.response?.data?.message || err.message));
    }
  } finally {
    if (loadingInstance) {
      loadingInstance.close();
    }
  }
};

// 页面加载时获取消息
onMounted(() => {
  if (!currentAdmin.value) {
    router.push('/admin/login');
    return;
  }
  fetchMessages();
});
</script>

<style scoped>
.messages-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.messages-card {
  margin-bottom: 20px;
}

.messages-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.back-button {
  margin-bottom: 20px;
}

.search-filter {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-filter .el-input {
  flex: 1;
  min-width: 200px;
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

/* 消息详情样式 */
.message-detail {
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
}

.detail-item .value {
  color: #303133;
}

.detail-content {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.8;
  color: #303133;
}
</style>