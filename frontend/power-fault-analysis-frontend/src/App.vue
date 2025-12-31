<template>
  <el-container class="layout-container">
    <el-header class="app-header">
      <div class="header-inner">
        <div class="logo">
          <el-icon :size="28" class="logo-icon"><Lightning /></el-icon>
          <span>电力设备故障智能分析系统</span>
        </div>
        <el-menu 
          :default-active="activeIndex" 
          class="nav-menu" 
          mode="horizontal" 
          :router="true"
          background-color="#001529"
          text-color="#ffffff"
          active-text-color="#409EFF"
          :ellipsis="false"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>首页
          </el-menu-item>
          <el-menu-item index="/knowledge-graph">
            <el-icon><Share /></el-icon>知识图谱
          </el-menu-item>
          <el-menu-item index="/fault-analysis">
            <el-icon><Search /></el-icon>故障诊断
          </el-menu-item>
          <el-menu-item index="/data-management">
            <el-icon><Setting /></el-icon>数据管理
          </el-menu-item>
        </el-menu>
      </div>
    </el-header>
    
    <el-main class="app-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>

    <el-footer class="app-footer">
      <p>© 2025 电力设备故障分析系统 | Graduation Project</p>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const activeIndex = ref('/');
const route = useRoute();

watch(() => route.path, (newPath) => {
  activeIndex.value = newPath;
}, { immediate: true });
</script>

<style>
/* Global Resets */
body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f0f2f5;
  color: #303133;
}

.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background-color: #001529;
  color: white;
  padding: 0 !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 10;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.logo {
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-left: 20px;
}

.nav-menu {
  border-bottom: none !important;
  height: 60px;
  background-color: transparent !important;
}

.app-main {
  flex: 1;
  padding: 20px !important;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

.app-footer {
  text-align: center;
  color: #909399;
  font-size: 14px;
  padding: 20px 0;
  background-color: #f0f2f5;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>