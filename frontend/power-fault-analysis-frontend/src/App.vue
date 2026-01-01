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
        <div class="header-right">
          <el-tooltip :content="isDark ? '切换白天模式' : '切换夜间模式'" placement="bottom">
            <el-switch
              v-model="isDark"
              inline-prompt
              :active-icon="Moon"
              :inactive-icon="Sunny"
              @change="toggleDark"
              style="--el-switch-on-color: #2c3e50; --el-switch-off-color: #f39c12"
            />
          </el-tooltip>
        </div>
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
import { ref, watch, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { Sunny, Moon } from '@element-plus/icons-vue';

const activeIndex = ref('/');
const route = useRoute();
const isDark = ref(false);

const toggleDark = (val) => {
  const html = document.documentElement;
  if (val) {
    html.classList.add('dark');
    localStorage.setItem('theme', 'dark');
  } else {
    html.classList.remove('dark');
    localStorage.setItem('theme', 'light');
  }
};

onMounted(() => {
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme === 'dark' || (!savedTheme && window.matchMedia('(prefers-color-scheme: dark)').matches)) {
    isDark.value = true;
    toggleDark(true);
  }
});

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
  background-color: var(--el-bg-color-page);
  color: var(--el-text-color-primary);
  transition: background-color 0.3s, color 0.3s;
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

/* Ensure header stays dark even in dark mode for consistency if desired, 
   or let it change. Usually professional apps keep the side/top bar consistent. 
   Here we keep it dark as originally designed. */

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.header-right {
  display: flex;
  align-items: center;
  padding-right: 20px;
}

.logo {
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
  padding-left: 20px;
  color: white;
}

.nav-menu {
  border-bottom: none !important;
  height: 60px;
  background-color: transparent !important;
  flex: 1;
  justify-content: center;
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
  color: var(--el-text-color-secondary);
  font-size: 14px;
  padding: 20px 0;
  background-color: var(--el-bg-color-page);
  border-top: 1px solid var(--el-border-color-lighter);
}

/* Dark mode overrides for ECharts containers if needed */
.dark .echarts-container {
  background-color: #1a1a1a !important;
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