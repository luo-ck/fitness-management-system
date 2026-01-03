<template>
  <div class="applications-container">
    <el-card class="applications-card" shadow="hover">
      <template #header>
        <div class="applications-title">
          <el-icon><UserFilled /></el-icon>
          教练申请管理
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
          placeholder="搜索用户名、姓名或专业特长" 
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
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
      </div>
      
      <!-- 申请列表 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="10" animated />
      </div>
      
      <div v-else-if="applications.length === 0" class="empty-container">
        <el-empty description="暂无教练申请" />
      </div>
      
      <div v-else class="applications-list">
        <el-table :data="filteredApplications" stripe style="width: 100%">
          <el-table-column prop="id" label="申请ID" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="name" label="真实姓名" width="120" />
          <el-table-column prop="specialty" label="专业特长" width="150" />
          <el-table-column prop="contact" label="联系方式" width="150" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag 
                :type="scope.row.status === 'pending' ? 'warning' : 
                       scope.row.status === 'approved' ? 'success' : 'danger'">
                {{ scope.row.status === 'pending' ? '待审核' : 
                   scope.row.status === 'approved' ? '已通过' : '已拒绝' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applyTime" label="申请时间" width="180">
            <template #default="scope">{{ formatTime(scope.row.applyTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="viewApplication(scope.row)">
                <el-icon><View /></el-icon>
                查看详情
              </el-button>
              
              <el-button type="success" size="small" @click="approveApplication(scope.row)" v-if="scope.row.status === 'pending'">
                <el-icon><CircleCheck /></el-icon>
                审批通过
              </el-button>
              
              <el-button type="danger" size="small" @click="rejectApplication(scope.row)" v-if="scope.row.status === 'pending'">
                <el-icon><CircleClose /></el-icon>
                拒绝申请
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
            :total="filteredApplications.length"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>
    
    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="申请详情"
      width="600px"
    >
      <div v-if="selectedApplication" class="application-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="申请ID">{{ selectedApplication.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ selectedApplication.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ selectedApplication.name }}</el-descriptions-item>
          <el-descriptions-item label="专业特长">{{ selectedApplication.specialty }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ selectedApplication.contact }}</el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag 
              :type="selectedApplication.status === 'pending' ? 'warning' : 
                     selectedApplication.status === 'approved' ? 'success' : 'danger'">
              {{ selectedApplication.status === 'pending' ? '待审核' : 
                 selectedApplication.status === 'approved' ? '已通过' : '已拒绝' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatTime(selectedApplication.applyTime) }}</el-descriptions-item>
          <el-descriptions-item label="审批时间" v-if="selectedApplication.approveTime">{{ formatTime(selectedApplication.approveTime) }}</el-descriptions-item>
          <el-descriptions-item label="审批管理员" v-if="selectedApplication.adminName">{{ selectedApplication.adminName }}</el-descriptions-item>
          <el-descriptions-item label="反馈信息" v-if="selectedApplication.feedback">{{ selectedApplication.feedback }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">{{ selectedApplication.intro }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="success" @click="approveApplication(selectedApplication)" v-if="selectedApplication?.status === 'pending'">
            审批通过
          </el-button>
          <el-button type="danger" @click="rejectApplication(selectedApplication)" v-if="selectedApplication?.status === 'pending'">
            拒绝申请
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 拒绝申请对话框 -->
    <el-dialog
      v-model="rejectDialogVisible"
      title="拒绝申请"
      width="500px"
    >
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef" label-width="100px">
        <el-form-item label="反馈信息" prop="feedback">
          <el-input 
            v-model="rejectForm.feedback" 
            type="textarea" 
            placeholder="请输入拒绝理由" 
            :rows="5"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { UserFilled, Back, Search, View, CircleCheck, CircleClose } from '@element-plus/icons-vue';
import { ElMessage, ElLoading } from 'element-plus';
import api from '../services/api';

const router = useRouter();

const loading = ref(false);
const applications = ref([]);
const searchKeyword = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(10);

// 对话框相关
const detailDialogVisible = ref(false);
const rejectDialogVisible = ref(false);
const selectedApplication = ref(null);
const rejectFormRef = ref();

const rejectForm = ref({
  feedback: ''
});

const rejectRules = {
  feedback: [
    { required: true, message: '请输入拒绝理由', trigger: 'blur' },
    { min: 10, max: 500, message: '拒绝理由长度在 10 到 500 个字符', trigger: 'blur' }
  ]
};

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

// 过滤后的申请列表
const filteredApplications = computed(() => {
  let result = [...applications.value];
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(item => 
      item.username.toLowerCase().includes(keyword) ||
      item.name.toLowerCase().includes(keyword) ||
      item.specialty.toLowerCase().includes(keyword)
    );
  }
  
  // 状态过滤
  if (statusFilter.value) {
    result = result.filter(item => item.status === statusFilter.value);
  }
  
  return result;
});

// 分页后的申请列表
const paginatedApplications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredApplications.value.slice(start, end);
});

// 获取申请列表
const fetchApplications = async () => {
  if (!currentAdmin.value) return;
  
  loading.value = true;
  try {
    const response = await api.get('/coach-applications');
    applications.value = response.data || [];
  } catch (err) {
    console.error('获取申请列表失败:', err);
    ElMessage.error('获取申请列表失败: ' + (err.response?.data?.message || err.message));
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

// 查看申请详情
const viewApplication = (application) => {
  selectedApplication.value = application;
  detailDialogVisible.value = true;
};

// 审批通过申请
const approveApplication = async (application) => {
  if (!currentAdmin.value) return;
  
  let loadingInstance = null;
  
  try {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '审批中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    
    const approveData = {
      adminId: currentAdmin.value.id,
      adminName: currentAdmin.value.name
    };
    
    const response = await api.put(`/coach-applications/approve/${application.id}`, approveData);
    if (response.data) {
      ElMessage.success('审批通过成功');
      fetchApplications();
      detailDialogVisible.value = false;
    }
  } catch (err) {
    console.error('审批通过失败:', err);
    ElMessage.error('审批通过失败: ' + (err.response?.data?.message || err.message));
  } finally {
    if (loadingInstance) {
      loadingInstance.close();
    }
  }
};

// 拒绝申请
const rejectApplication = (application) => {
  selectedApplication.value = application;
  rejectForm.value.feedback = '';
  rejectDialogVisible.value = true;
};

// 确认拒绝
const confirmReject = async () => {
  if (!rejectFormRef.value || !selectedApplication.value || !currentAdmin.value) return;
  
  await rejectFormRef.value.validate(async (valid) => {
    if (valid) {
      let loadingInstance = null;
      try {
        loadingInstance = ElLoading.service({
          lock: true,
          text: '拒绝中...',
          background: 'rgba(0, 0, 0, 0.7)'
        });
        
        const rejectData = {
          adminId: currentAdmin.value.id,
          adminName: currentAdmin.value.name,
          feedback: rejectForm.value.feedback
        };
        
        const response = await api.put(`/coach-applications/reject/${selectedApplication.value.id}`, rejectData);
        if (response.data) {
          ElMessage.success('拒绝申请成功');
          fetchApplications();
          rejectDialogVisible.value = false;
          detailDialogVisible.value = false;
        }
      } catch (err) {
        console.error('拒绝申请失败:', err);
        ElMessage.error('拒绝申请失败: ' + (err.response?.data?.message || err.message));
      } finally {
        if (loadingInstance) {
          loadingInstance.close();
        }
      }
    }
  });
};

// 页面加载时获取申请列表
onMounted(() => {
  if (!currentAdmin.value) {
    router.push('/admin/login');
    return;
  }
  fetchApplications();
});
</script>

<style scoped>
.applications-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.applications-card {
  margin-bottom: 20px;
}

.applications-title {
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

.application-detail {
  padding: 10px 0;
}
</style>