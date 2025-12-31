<template>
  <div class="knowledge-graph-container">
    <!-- Graph Area -->
    <div id="graph-container" class="graph-canvas"></div>

    <!-- Floating Filter Panel -->
    <el-card class="filter-panel" shadow="always">
       <template #header>
         <div class="panel-header">
           <span><el-icon><Filter /></el-icon> 图谱筛选</span>
         </div>
       </template>
       <div class="filter-content">
         <el-checkbox-group v-model="selectedCategories" @change="updateChart" direction="vertical">
            <div class="filter-item"><el-checkbox label="DeviceType">设备类型 (Device)</el-checkbox></div>
            <div class="filter-item"><el-checkbox label="Component">设备部件 (Component)</el-checkbox></div>
            <div class="filter-item"><el-checkbox label="FaultPhenomenon">故障现象 (Fault)</el-checkbox></div>
            <div class="filter-item"><el-checkbox label="FaultCause">故障原因 (Cause)</el-checkbox></div>
            <div class="filter-item"><el-checkbox label="Solution">解决方案 (Solution)</el-checkbox></div>
        </el-checkbox-group>
       </div>
    </el-card>

    <!-- Legend/Info (Optional) -->
    <div class="graph-info">
      <el-tag effect="dark" type="primary">设备</el-tag>
      <el-tag effect="dark" type="info">部件</el-tag>
      <el-tag effect="dark" type="warning">故障</el-tag>
      <el-tag effect="dark" type="danger">原因</el-tag>
      <el-tag effect="dark" type="success">方案</el-tag>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

const chartInstance = ref(null);
const fullData = ref({ nodes: [], links: [] });
const selectedCategories = ref(['DeviceType', 'Component', 'FaultPhenomenon', 'FaultCause', 'Solution']);

const updateChart = () => {
    if (!fullData.value.nodes.length) return;
    
    const filteredNodesRaw = fullData.value.nodes.filter(n => selectedCategories.value.includes(n.category));
    const filteredNodeIds = new Set(filteredNodesRaw.map(n => n.id));
    
    const filteredLinksRaw = fullData.value.links.filter(l => 
        filteredNodeIds.has(l.source) && filteredNodeIds.has(l.target)
    );

    renderChart(filteredNodesRaw, filteredLinksRaw);
};

const renderChart = (nodeData, linkData) => {
  const chartDom = document.getElementById('graph-container');
  if (!chartDom) return;
  
  if (!chartInstance.value) {
      chartInstance.value = echarts.init(chartDom);
      window.addEventListener('resize', () => {
          chartInstance.value.resize();
      });
  }
  
  const categories = [
    { name: 'DeviceType', itemStyle: { color: '#409EFF' } },
    { name: 'Component', itemStyle: { color: '#909399' } },
    { name: 'FaultPhenomenon', itemStyle: { color: '#E6A23C' } },
    { name: 'FaultCause', itemStyle: { color: '#F56C6C' } },
    { name: 'Solution', itemStyle: { color: '#67C23A' } }
  ];

  const nodes = nodeData.map(node => {
    let catIndex = 0;
    if (node.category === 'DeviceType') catIndex = 0;
    else if (node.category === 'Component') catIndex = 1;
    else if (node.category === 'FaultPhenomenon') catIndex = 2;
    else if (node.category === 'FaultCause') catIndex = 3;
    else if (node.category === 'Solution') catIndex = 4;

    return {
      id: node.id, 
      name: node.name,
      value: node.category,
      category: catIndex,
      symbolSize: catIndex === 0 ? 55 : 40,
      label: { show: true }
    };
  });

  const links = linkData.map(link => ({
    source: link.source,
    target: link.target,
    value: link.name,
    label: { show: false, formatter: link.name } // Hidden label for cleaner look, show on hover
  }));

  const option = {
    title: {
      text: '',
    },
    tooltip: {
        trigger: 'item',
        formatter: (params) => {
           if (params.dataType === 'edge') {
               return `${params.data.source} > ${params.data.value} > ${params.data.target}`;
           }
           return `<b>${params.name}</b><br/>${params.data.value}`;
        }
    },
    legend: [{ show: false }], // Using custom legend
    series: [
      {
        name: 'Graph',
        type: 'graph',
        layout: 'force',
        data: nodes,
        links: links,
        categories: categories,
        roam: true,
        label: {
          show: true,
          position: 'right',
          formatter: '{b}'
        },
        force: {
          repulsion: 800,
          edgeLength: 120,
          gravity: 0.1
        },
        lineStyle: {
            color: '#c0c4cc',
            curveness: 0.3,
            width: 2
        },
        emphasis: {
            focus: 'adjacency',
            lineStyle: {
                width: 4
            }
        },
        edgeSymbol: ['none', 'arrow'],
        edgeSymbolSize: 10
      }
    ]
  };

  chartInstance.value.setOption(option);
};

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8081/api/knowledge-graph/whole-graph');
    if (response.data) {
        fullData.value = response.data;
        updateChart();
    }
  } catch (error) {
    console.error('Failed to fetch graph data:', error);
  }
});
</script>

<style scoped>
.knowledge-graph-container {
  position: relative;
  height: calc(100vh - 140px); /* Adjust based on header/footer */
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.graph-canvas {
  width: 100%;
  height: 100%;
}

.filter-panel {
    position: absolute;
    top: 20px;
    left: 20px;
    width: 220px;
    opacity: 0.95;
    z-index: 5;
}

.panel-header {
    display: flex;
    align-items: center;
    gap: 5px;
    font-weight: bold;
}

.filter-content {
    display: flex;
    flex-direction: column;
}

.filter-item {
    margin-bottom: 8px;
}

.graph-info {
    position: absolute;
    bottom: 20px;
    right: 20px;
    display: flex;
    gap: 10px;
    background: rgba(255,255,255,0.8);
    padding: 10px;
    border-radius: 4px;
}
</style>