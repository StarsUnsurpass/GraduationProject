<template>
  <div class="fault-analysis-container">
    <div class="search-section" :class="{ 'has-results': searched }">
            <h1 v-if="!searched" class="page-title">故障智能诊断</h1>
            <p v-if="!searched" class="page-subtitle">输入故障现象，系统将自动分析原因并推荐解决方案</p>
            
            <div class="search-box-wrapper">
                <el-input 
                    v-model="searchQuery" 
                    placeholder="请输入故障现象 (例如: 油温过高)" 
                    class="main-search-input"
                    size="large"
                    @keyup.enter="handleDiagnose"
                >
                  <template #prefix>
                    <el-icon class="search-icon"><Search /></el-icon>
                  </template>
                  <template #append>
                    <el-button type="primary" @click="handleDiagnose" class="search-btn">开始诊断</el-button>
                  </template>
                </el-input>
            </div>
            
             <!-- Quick Tags (Demo) -->
             <div v-if="!searched" class="quick-tags">
                 <span>推荐搜索: </span>
                 <el-tag class="tag-item" @click="quickSearch('油温过高')">油温过高</el-tag>
                 <el-tag class="tag-item" @click="quickSearch('异响')">异响</el-tag>
                 <el-tag class="tag-item" @click="quickSearch('电压不稳')">电压不稳</el-tag>
             </div>
        </div>
    
    <div v-if="resultFound" class="result-area">
         <div class="result-header">
             <h3><el-icon><DataAnalysis /></el-icon> 诊断结果分析: <span class="highlight">{{ searchQuery }}</span></h3>
             <el-button link type="primary" @click="resetSearch">重新搜索</el-button>
         </div>
         <el-card shadow="never" class="graph-card">
            <div id="diagnosis-graph" style="width: 100%; height: 600px;"></div>
         </el-card>

         <!-- Solution List -->
         <div v-if="solutions.length > 0" class="solution-section">
             <h3><el-icon><SuccessFilled /></el-icon> 维护建议 / 解决方案</h3>
             <el-timeline>
                <el-timeline-item
                  v-for="(sol, index) in solutions"
                  :key="index"
                  type="success"
                  size="large"
                  :timestamp="'建议 ' + (index + 1)"
                  placement="top"
                >
                  <el-card class="solution-card">
                    <h4>{{ sol.name }}</h4>
                    <p>{{ sol.description || '暂无详细描述' }}</p>
                    <p v-if="sol.attributes" class="attr-text">参数参考: {{ sol.attributes }}</p>
                  </el-card>
                </el-timeline-item>
             </el-timeline>
         </div>
    </div>

    <div v-else-if="searched && !resultFound" class="no-result">
         <el-empty description="暂未找到相关诊断结果" />
         <el-button @click="resetSearch">返回</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue';
import axios from 'axios';
import * as echarts from 'echarts';
import { Search, DataAnalysis, SuccessFilled } from '@element-plus/icons-vue';

const searchQuery = ref('');
const resultFound = ref(false);
const searched = ref(false);
const solutions = ref([]);
let chartInstance = null;

// Detect dark mode
const isDarkMode = () => document.documentElement.classList.contains('dark');

const quickSearch = (val) => {
    searchQuery.value = val;
    handleDiagnose();
}

const resetSearch = () => {
    searched.value = false;
    resultFound.value = false;
    searchQuery.value = '';
    solutions.value = [];
}

const lastDiagnosisData = ref(null);

const handleDiagnose = async () => {
    if (!searchQuery.value) return;
    
    searched.value = true;
    resultFound.value = false;
    solutions.value = [];
    
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/fault-analysis/diagnose`, {
            params: { phenomenon: searchQuery.value }
        });
        
        if (response.data && response.data.nodes && response.data.nodes.length > 0) {
            resultFound.value = true;
            lastDiagnosisData.value = response.data;
            solutions.value = response.data.nodes.filter(n => n.category === 'Solution');
            await nextTick();
            initChart(response.data);
        } else {
            resultFound.value = false;
        }
    } catch (error) {
        console.error("Diagnosis failed:", error);
        resultFound.value = false;
    }
};

const initChart = (data) => {
    const chartDom = document.getElementById('diagnosis-graph');
    if (!chartDom) return;
    
    const theme = isDarkMode() ? 'dark' : null;
    
    if (chartInstance) {
        if (chartInstance.getTheme() !== theme) {
            chartInstance.dispose();
            chartInstance = echarts.init(chartDom, theme);
        }
    } else {
        chartInstance = echarts.init(chartDom, theme);
        window.addEventListener('resize', () => chartInstance && chartInstance.resize());
    }
    
    const categories = [
        { name: 'FaultPhenomenon', itemStyle: { color: '#E6A23C' }, label: '故障现象' }, 
        { name: 'FaultCause', itemStyle: { color: '#F56C6C' }, label: '故障原因' },     
        { name: 'Solution', itemStyle: { color: '#67C23A' }, label: '解决方案' }        
    ];

    const nodes = data.nodes.map(node => {
        let idx = categories.findIndex(c => c.name === node.category);
        if (idx === -1) idx = 0; 
        
        return {
            id: node.id,
            name: node.name,
            category: idx,
            symbolSize: node.name === searchQuery.value ? 60 : 45,
            label: { show: true }
        };
    });

    const links = data.links.map(link => ({
        source: link.source,
        target: link.target,
        value: link.name,
        label: { show: true, formatter: translateRelation(link.name) }
    }));
    
    const option = {
        backgroundColor: 'transparent',
        title: { 
            text: '故障因果链',
            left: 'center',
            top: 10,
            textStyle: {
                color: isDarkMode() ? '#fff' : '#333'
            }
        },
        tooltip: { trigger: 'item' },
        legend: {
            data: categories.map(c => c.label),
            bottom: 10,
            textStyle: {
                color: isDarkMode() ? '#ccc' : '#333'
            }
        },
        series: [{
            type: 'graph',
            layout: 'force',
            data: nodes,
            links: links,
            categories: categories.map(c => ({ name: c.label, itemStyle: c.itemStyle })),
            roam: true,
            label: { 
                position: 'right', 
                show: true,
                fontWeight: 'bold'
            },
            force: { 
                repulsion: 400, 
                edgeLength: 150 
            },
            lineStyle: {
                color: 'source',
                curveness: 0.2,
                width: 2
            },
            edgeSymbol: ['none', 'arrow']
        }]
    };
    
    chartInstance.setOption(option);
};

const translateRelation = (name) => {
    const map = {
        'CAUSED_BY': '导致原因',
        'SOLVED_BY': '解决方案',
        'HAS_POSSIBLE_FAULT': '可能故障'
    };
    return map[name] || name;
}

// Watch for theme changes
onMounted(() => {
  const observer = new MutationObserver(() => {
    if (resultFound.value && lastDiagnosisData.value) {
       initChart(lastDiagnosisData.value); // Refresh chart with new theme using cached data
    }
  });
  observer.observe(document.documentElement, { attributes: true, attributeFilter: ['class'] });
});
</script>

<style scoped>
.fault-analysis-container {
    padding: 20px;
    min-height: 80vh;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.search-section {
    width: 100%;
    max-width: 700px;
    text-align: center;
    margin-top: 10vh;
    transition: all 0.5s ease;
}

.search-section.has-results {
    margin-top: 0;
    margin-bottom: 30px;
}

.page-title {
    font-size: 2.5rem;
    color: var(--el-text-color-primary);
    margin-bottom: 10px;
}

.page-subtitle {
    color: var(--el-text-color-secondary);
    margin-bottom: 40px;
    font-size: 1.1rem;
}

.search-box-wrapper {
    margin-bottom: 20px;
}

.main-search-input :deep(.el-input__wrapper) {
    padding-left: 15px;
    border-radius: 30px 0 0 30px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.main-search-input :deep(.el-input-group__append) {
    border-radius: 0 30px 30px 0;
    background-color: var(--el-color-primary);
    color: white;
    border: none;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.search-btn {
    font-weight: bold;
}

.quick-tags {
    color: var(--el-text-color-regular);
    font-size: 14px;
}

.tag-item {
    margin: 0 5px;
    cursor: pointer;
    transition: all 0.2s;
}

.tag-item:hover {
    transform: translateY(-2px);
}

.result-area {
    width: 100%;
    max-width: 1000px;
    animation: fadeIn 0.8s ease;
}

.result-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.highlight {
    color: var(--el-color-primary);
}

.graph-card {
    border-radius: 8px;
    background-color: var(--el-bg-color);
}

.no-result {
    margin-top: 50px;
    text-align: center;
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(20px); }
    to { opacity: 1; transform: translateY(0); }
}
</style>