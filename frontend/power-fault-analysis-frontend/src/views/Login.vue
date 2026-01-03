<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
           <el-icon :size="30" class="logo-icon"><Lightning /></el-icon>
           <h2>系统登录</h2>
        </div>
      </template>
      <el-form :model="form" @submit.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large"/>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" size="large" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { User, Lock, Lightning } from '@element-plus/icons-vue';

const router = useRouter();
const form = ref({
  username: '',
  password: ''
});
const loading = ref(false);

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }
  
  loading.value = true;
  try {
    const res = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/users/login`, {
        username: form.value.username,
        password: form.value.password
    });
    
    if (res.data && res.data.token) {
        localStorage.setItem('token', res.data.token);
        localStorage.setItem('user', JSON.stringify(res.data));
        ElMessage.success('登录成功');
        router.push('/');
    }
  } catch (error) {
    const errorMsg = error.response && error.response.data ? error.response.data : '用户名或密码错误';
    ElMessage.error('登录失败: ' + errorMsg);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: var(--el-bg-color-page);
}

.login-card {
  width: 400px;
  border-radius: 8px;
}

.login-header {
  text-align: center;
  color: var(--el-color-primary);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-icon {
    margin-bottom: 10px;
}

.login-btn {
  width: 100%;
}
</style>
