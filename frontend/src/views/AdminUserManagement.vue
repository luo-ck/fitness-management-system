<template>
  <div class="user-management-container">
    <el-card class="user-management-header" shadow="hover">
      <template #header>
        <div class="page-title">
          <el-button type="primary" size="small" @click="handleBack" style="margin-right: 10px;">
            <el-icon><ArrowLeft /></el-icon>
            返回控制台
          </el-button>
          <el-icon><User /></el-icon>
          用户管理
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

    <el-card class="user-table-card" shadow="hover">
      <el-table
        :data="paginatedUsers"
        stripe
        style="width: 100%"
        :loading="loading"
      >
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <span>{{ scope.row.gender === '男' ? '男' : (scope.row.gender === '女' ? '女' : '未知') }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="goal" label="训练目标" width="120" />
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
          :total="filteredUsers.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑用户模态框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑用户"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="1" :max="150" placeholder="请输入年龄" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="训练目标" prop="goal">
          <el-select v-model="form.goal" placeholder="请选择训练目标">
            <el-option label="减脂" value="减脂" />
            <el-option label="增肌" value="增肌" />
            <el-option label="保持" value="保持" />
          </el-select>
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
import { ref, reactive, computed, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { User, Search, RefreshRight, Edit, Delete, ArrowLeft } from '@element-plus/icons-vue';
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus';
import api from '../services/api';

const router = useRouter();

// 页面数据
const users = ref([]);
const filteredUsers = ref([]);
const searchQuery = ref('');
const loading = ref(false);

// 分页数据
const currentPage = ref(1);
const pageSize = ref(10);

// 编辑模态框数据
const dialogVisible = ref(false);
const formRef = ref(null);
const form = reactive({
  userId: null,
  username: '',
  name: '',
  age: null,
  gender: '',
  goal: ''
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  goal: [{ required: true, message: '请选择训练目标', trigger: 'change' }]
};

// 过滤后的用户列表
const paginatedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredUsers.value.slice(start, end);
});

// 监听搜索查询变化
const updateFilteredUsers = () => {
  if (!searchQuery.value) {
    filteredUsers.value = [...users.value];
  } else {
    const query = searchQuery.value.toLowerCase();
    filteredUsers.value = users.value.filter(user => {
      return user.username?.toLowerCase().includes(query) || 
             user.name?.toLowerCase().includes(query);
    });
  }
  // 重置分页
  currentPage.value = 1;
};

// 获取用户列表
const fetchUsers = async () => {
  loading.value = true;
  try {
    const response = await api.get('/users');
    users.value = response.data;
    updateFilteredUsers();
  } catch (error) {
    ElMessage.error('获取用户列表失败');
    console.error('获取用户列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 搜索用户
const handleSearch = () => {
  updateFilteredUsers();
};

// 刷新用户列表
const handleRefresh = () => {
  searchQuery.value = '';
  fetchUsers();
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

// 编辑用户
const handleEdit = (user) => {
  // 复制用户数据到表单
  Object.assign(form, user);
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
        await api.put(`/users/${form.userId}`, form);
        ElMessage.success('用户信息更新成功');
        dialogVisible.value = false;
        fetchUsers(); // 刷新用户列表
      } catch (error) {
        ElMessage.error('更新用户信息失败');
        console.error('更新用户信息失败:', error);
      } finally {
        loading.close();
      }
    }
  });
};

// 删除用户
const handleDelete = (user) => {
  ElMessageBox.confirm(`确定要删除用户 "${user.username}" 吗？`, '删除确认', {
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
      await api.delete(`/users/${user.userId}`);
      ElMessage.success('用户删除成功');
      fetchUsers(); // 刷新用户列表
    } catch (error) {
      ElMessage.error('删除用户失败');
      console.error('删除用户失败:', error);
    } finally {
      loading.close();
    }
  }).catch(() => {
    // 取消删除
  });
};

// 页面加载时获取用户列表
onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.user-management-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.user-management-header {
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

.user-table-card {
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