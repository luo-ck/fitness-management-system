<template>
  <div class="coach-management-container">
    <el-card class="coach-management-header" shadow="hover">
      <template #header>
        <div class="page-title">
          <el-button type="primary" size="small" @click="handleBack" style="margin-right: 10px;">
            <el-icon><ArrowLeft /></el-icon>
            返回控制台
          </el-button>
          <el-icon><User /></el-icon>
          教练管理
        </div>
      </template>
      <div class="header-actions">
        <el-input
          v-model="searchQuery"
          placeholder="搜索用户名或姓名"
          clearable
          style="width: 300px; margin-right: 10px;"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
          </template>
        </el-input>
        <el-button type="primary" @click="handleRefresh">
          <el-icon><RefreshRight /></el-icon>
          刷新
        </el-button>
      </div>
    </el-card>

    <el-card class="coach-table-card" shadow="hover">
      <el-table
        :data="paginatedCoaches"
        stripe
        style="width: 100%"
        :loading="loading"
      >
        <el-table-column prop="coachId" label="教练ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="specialty" label="专长" width="150" />
        <el-table-column prop="experience" label="经验" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '已验证' : '未验证' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">
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
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredCoaches.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑教练模态框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑教练"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="专长" prop="specialty">
          <el-input v-model="form.specialty" placeholder="请输入专长" />
        </el-form-item>
        <el-form-item label="经验" prop="experience">
          <el-input-number v-model="form.experience" :min="1" :max="50" placeholder="请输入经验年数" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">已验证</el-radio>
            <el-radio label="0">未验证</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="简介" prop="bio">
          <el-input v-model="form.bio" type="textarea" :rows="4" placeholder="请输入教练简介" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { User, Search, RefreshRight, Edit, Delete, ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus';
import api from '../services/api';

const router = useRouter();

// 页面数据
const coaches = ref([]);
const filteredCoaches = ref([]);
const searchQuery = ref('');
const loading = ref(false);

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);

// 编辑模态框数据
const dialogVisible = ref(false);
const formRef = ref(null);
const form = reactive({
  coachId: null,
  username: '',
  name: '',
  specialty: '',
  experience: null,
  status: 0,
  bio: ''
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入专长', trigger: 'blur' }],
  experience: [{ required: true, message: '请输入经验年数', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
};

// 分页后的教练列表
const paginatedCoaches = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredCoaches.value.slice(start, end);
});

// 监听搜索查询变化
const updateFilteredCoaches = () => {
  if (!searchQuery.value) {
    filteredCoaches.value = [...coaches.value];
  } else {
    const query = searchQuery.value.toLowerCase();
    filteredCoaches.value = coaches.value.filter(coach => {
      return coach.username?.toLowerCase().includes(query) || 
             coach.name?.toLowerCase().includes(query);
    });
  }
  // 重置分页
  currentPage.value = 1;
};

// 获取教练列表
const fetchCoaches = async () => {
  loading.value = true;
  try {
    const response = await api.get('/coaches');
    coaches.value = response.data;
    updateFilteredCoaches();
  } catch (error) {
    ElMessage.error('获取教练列表失败');
    console.error('获取教练列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 搜索教练
const handleSearch = () => {
  updateFilteredCoaches();
};

// 刷新教练列表
const handleRefresh = () => {
  searchQuery.value = '';
  fetchCoaches();
};

// 返回控制台
const handleBack = () => {
  router.push('/admin/dashboard');
};

// 分页大小变化
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
};

// 当前页变化
const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
};

// 编辑教练
const handleEdit = (coach) => {
  // 复制教练数据到表单
  Object.assign(form, coach);
  dialogVisible.value = true;
};

// 提交编辑
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const loading = ElLoading.service({
        lock: true,
        text: '保存中...',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      
      try {
        await api.put(`/coaches/${form.coachId}`, form);
        ElMessage.success('教练信息更新成功');
        dialogVisible.value = false;
        fetchCoaches(); // 刷新教练列表
      } catch (error) {
        ElMessage.error('更新教练信息失败');
        console.error('更新教练信息失败:', error);
      } finally {
        loading.close();
      }
    }
  });
};

// 删除教练
const handleDelete = (coach) => {
  ElMessageBox.confirm(`确定要删除教练 "${coach.username}" 吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const loading = ElLoading.service({
      lock: true,
      text: '删除中...',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    
    try {
      await api.delete(`/coaches/${coach.coachId}`);
      ElMessage.success('教练删除成功');
      fetchCoaches(); // 刷新教练列表
    } catch (error) {
      ElMessage.error('删除教练失败');
      console.error('删除教练失败:', error);
    } finally {
      loading.close();
    }
  }).catch(() => {
    // 取消删除
  });
};

// 页面加载时获取教练列表
onMounted(() => {
  fetchCoaches();
});
</script>

<style scoped>
.coach-management-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.coach-management-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 10px;
}

.coach-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>