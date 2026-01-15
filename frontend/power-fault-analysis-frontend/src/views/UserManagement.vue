<template>
  <div class="user-management-container">
    <div class="page-header">
      <h2><el-icon><User /></el-icon> 用户管理</h2>
      <el-button type="primary" @click="openAddDialog">添加用户</el-button>
    </div>

    <el-table :data="users" style="width: 100%" v-loading="loading">
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色">
          <template #default="scope">
              <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'success'">{{ scope.row.role }}</el-tag>
          </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" :icon="Edit" circle @click="openEditDialog(scope.row)" style="margin-right: 5px;"/>
          <el-popconfirm title="确定删除该用户吗？" @confirm="deleteUser(scope.row.username)">
            <template #reference>
              <el-button type="danger" size="small" :icon="Delete" circle :disabled="scope.row.username === 'admin'" />
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- Add/Edit User Dialog -->
    <el-dialog v-model="showAddDialog" :title="isEditMode ? '编辑用户' : '添加新用户'" width="30%" @close="resetForm">
      <el-form :model="newUserForm">
        <el-form-item label="用户名">
          <el-input v-model="newUserForm.username" :disabled="isEditMode" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="newUserForm.password" type="password" show-password :placeholder="isEditMode ? '留空则不修改密码' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="newUserForm.role" placeholder="请选择角色">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="handleUserSubmit">{{ isEditMode ? '更新' : '创建' }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { User, Edit, Delete } from '@element-plus/icons-vue';

const users = ref([]);
const loading = ref(false);
const showAddDialog = ref(false);
const isEditMode = ref(false);
const newUserForm = ref({ username: '', password: '', role: 'USER' });

const fetchUsers = async () => {
  loading.value = true;
  try {
    const res = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/users`);
    users.value = res.data;
  } catch (error) {
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
};

const openAddDialog = () => {
    isEditMode.value = false;
    newUserForm.value = { username: '', password: '', role: 'USER' };
    showAddDialog.value = true;
}

const openEditDialog = (row) => {
    isEditMode.value = true;
    newUserForm.value = { 
        username: row.username, 
        password: '', // Password not retrieved for security
        role: row.role 
    };
    showAddDialog.value = true;
}

const resetForm = () => {
    newUserForm.value = { username: '', password: '', role: 'USER' };
    isEditMode.value = false;
}

const handleUserSubmit = async () => {
    if(!newUserForm.value.username) {
        ElMessage.warning("用户名不能为空");
        return;
    }
    if (!isEditMode.value && !newUserForm.value.password) {
        ElMessage.warning("创建用户时密码不能为空");
        return;
    }

    try {
        if (isEditMode.value) {
            await axios.put(`${import.meta.env.VITE_API_BASE_URL}/api/users/${newUserForm.value.username}`, newUserForm.value);
            ElMessage.success("用户更新成功");
        } else {
            await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/users`, newUserForm.value);
            ElMessage.success("用户添加成功");
        }
        showAddDialog.value = false;
        fetchUsers();
    } catch (e) {
        ElMessage.error(isEditMode.value ? "更新失败" : "添加失败，可能用户名已存在");
    }
}

const deleteUser = async (username) => {
    try {
        await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/api/users/${username}`);
        ElMessage.success("删除成功");
        fetchUsers();
    } catch (e) {
        ElMessage.error("删除失败");
    }
}

onMounted(() => {
    fetchUsers();
});
</script>

<style scoped>
.user-management-container {
    padding: 20px;
}
.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
</style>
