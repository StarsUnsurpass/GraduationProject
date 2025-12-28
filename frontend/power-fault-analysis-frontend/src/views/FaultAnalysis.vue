<template>
  <div class="fault-analysis-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>Intelligent Fault Diagnosis</span>
        </div>
      </template>
      <div class="search-box">
        <el-input 
            v-model="searchQuery" 
            placeholder="Enter Fault Phenomenon (e.g., 油温过高)" 
            class="input-with-select"
            @keyup.enter="handleDiagnose"
        >
          <template #append>
            <el-button @click="handleDiagnose">Diagnose</el-button>
          </template>
        </el-input>
      </div>
      
      <div v-if="resultFound" class="result-area">
         <h3>Diagnosis Result for: {{ searchQuery }}</h3>
         <div id="diagnosis-graph" style="width: 100%; height: 500px; border: 1px solid #eee;"></div>
      </div>
      <div v-else-if="searched" class="no-result">
          <el-empty description="No diagnosis found for this phenomenon"></el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import axios from 'axios';
import * as echarts from 'echarts';

const searchQuery = ref('');
const resultFound = ref(false);
const searched = ref(false);
let chartInstance = null;

const handleDiagnose = async () => {
    if (!searchQuery.value) return;
    
    searched.value = true;
    resultFound.value = false;
    
    try {
        const response = await axios.get(`http://localhost:8081/api/fault-analysis/diagnose`, {
            params: { phenomenon: searchQuery.value }
        });
        
        if (response.data && response.data.nodes && response.data.nodes.length > 0) {
            resultFound.value = true;
            await nextTick(); // Wait for DOM
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
    if (chartInstance) chartInstance.dispose();
    chartInstance = echarts.init(chartDom);
    
    // Categories matching the colors
    const categories = [
        { name: 'FaultPhenomenon', itemStyle: { color: '#E6A23C' } }, // Warning color
        { name: 'FaultCause', itemStyle: { color: '#F56C6C' } },      // Danger color
        { name: 'Solution', itemStyle: { color: '#67C23A' } }         // Success color
    ];

    const nodes = data.nodes.map(node => {
        // Find index or default to 0
        let idx = categories.findIndex(c => c.name === node.category);
        if (idx === -1) idx = 0; 
        
        return {
            id: node.id,
            name: node.name,
            category: idx,
            symbolSize: node.name === searchQuery.value ? 50 : 30,
            label: { show: true }
        };
    });

    const links = data.links.map(link => ({
        source: link.source,
        target: link.target,
        value: link.name,
        label: { show: true, formatter: link.name }
    }));
    
    const option = {
        title: { 
            text: 'Fault Causality Chain',
            left: 'center'
        },
        tooltip: {
            trigger: 'item',
            formatter: '{b}'
        },
        legend: {
            data: categories.map(c => c.name),
            bottom: 0
        },
        series: [{
            type: 'graph',
            layout: 'force',
            data: nodes,
            links: links,
            categories: categories,
            roam: true,
            label: { 
                position: 'right', 
                show: true 
            },
            force: { 
                repulsion: 200, 
                edgeLength: 100 
            },
            lineStyle: {
                color: 'source',
                curveness: 0.2
            },
            edgeSymbol: ['none', 'arrow']
        }]
    };
    
    chartInstance.setOption(option);
};
</script>

<style scoped>
.fault-analysis-container {
    padding: 20px;
}
.search-box {
    max-width: 600px;
    margin: 20px auto;
}
.result-area {
    margin-top: 30px;
}
</style>
