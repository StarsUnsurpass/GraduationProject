<template>
  <div class="home-container">
    <div class="hero-section">
      <h1>基于知识图谱的电力设备故障分析系统</h1>
      <p class="subtitle">利用图数据库技术，实现设备关联分析与智能故障诊断</p>
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="$router.push('/fault-analysis')">立即诊断</el-button>
        <el-button size="large" @click="$router.push('/knowledge-graph')">浏览图谱</el-button>
      </div>
    </div>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="box-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><Cpu /></el-icon> 设备类型</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.deviceTypes }}</span>
            <span class="unit">类</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="box-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><Connection /></el-icon> 部件总数</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.components }}</span>
            <span class="unit">个</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="box-card warning-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><Warning /></el-icon> 故障现象</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.faults }}</span>
            <span class="unit">种</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="box-card success-card">
          <template #header>
            <div class="card-header">
              <span><el-icon><CircleCheck /></el-icon> 解决方案</span>
            </div>
          </template>
          <div class="card-content">
            <span class="number">{{ stats.solutions }}</span>
            <span class="unit">条</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="feature-section">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
             <div class="section-title">系统功能简介</div>
          </template>
          <el-timeline>
            <el-timeline-item type="primary" timestamp="Step 1">
              <h4>知识图谱构建</h4>
              <p>录入设备、部件、故障及解决方案的关联关系。</p>
            </el-timeline-item>
            <el-timeline-item type="success" timestamp="Step 2">
              <h4>可视化展示</h4>
              <p>通过力导向图直观展示电力设备知识库网络。</p>
            </el-timeline-item>
            <el-timeline-item type="warning" timestamp="Step 3">
              <h4>智能诊断</h4>
              <p>输入故障现象，自动推理出可能的原因及维修建议。</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="image-card">
           <div class="placeholder-img">
              <el-icon :size="80" color="#409EFF"><DataAnalysis /></el-icon>
              <p>知识驱动 · 智能运维</p>
           </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const stats = ref({
  deviceTypes: 0,
  components: 0,
  faults: 0,
  solutions: 0
});

// Fetch counts on mount
onMounted(async () => {
    try {
        const [d, c, f, s] = await Promise.all([
             axios.get(import.meta.env.VITE_API_BASE_URL + '/api/knowledge-graph/devicetype'),
             axios.get(import.meta.env.VITE_API_BASE_URL + '/api/knowledge-graph/component'),
             axios.get(import.meta.env.VITE_API_BASE_URL + '/api/knowledge-graph/faultphenomenon'),
             axios.get(import.meta.env.VITE_API_BASE_URL + '/api/knowledge-graph/solution'),
        ]);
        stats.value.deviceTypes = d.data.length;
        stats.value.components = c.data.length;
        stats.value.faults = f.data.length;
        stats.value.solutions = s.data.length;
    } catch (e) {
        console.error("Failed to fetch stats", e);
    }
});
</script>

<style scoped>
.home-container {
  padding: 20px 0;
}

.hero-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 20px;
  background: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: background-color 0.3s;
}

.hero-section h1 {
  font-size: 2.5rem;
  color: var(--el-text-color-primary);
  margin-bottom: 10px;
}

.subtitle {
  font-size: 1.2rem;
  color: var(--el-text-color-secondary);
  margin-bottom: 30px;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.stat-cards {
  margin-bottom: 40px;
}

.box-card .card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
}

.card-content {
  text-align: center;
  padding: 10px 0;
}

.number {
  font-size: 28px;
  font-weight: bold;
  color: var(--el-text-color-primary);
}

.unit {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-left: 5px;
}

.warning-card .number { color: var(--el-color-warning); }
.success-card .number { color: var(--el-color-success); }

.section-title {
  font-weight: bold;
}

.placeholder-img {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--el-color-primary);
  min-height: 250px;
}
</style>
