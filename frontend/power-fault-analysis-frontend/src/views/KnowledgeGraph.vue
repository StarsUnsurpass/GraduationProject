<template>
  <div class="knowledge-graph-container">
    <div id="graph-container" style="width: 100%; height: 80vh; border: 1px solid #ccc;"></div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

const chartInstance = ref(null);

const initChart = (data) => {
  const chartDom = document.getElementById('graph-container');
  if (!chartDom) return;
  
  const myChart = echarts.init(chartDom);
  chartInstance.value = myChart;

  const categories = [
    { name: 'DeviceType' },
    { name: 'Component' },
    { name: 'FaultPhenomenon' },
    { name: 'FaultCause' },
    { name: 'Solution' }
  ];

  // Map nodes to expected format
  const nodes = data.nodes.map(node => {
    let catIndex = 0;
    if (node.category === 'DeviceType') catIndex = 0;
    else if (node.category === 'Component') catIndex = 1;
    else if (node.category === 'FaultPhenomenon') catIndex = 2;
    else if (node.category === 'FaultCause') catIndex = 3;
    else if (node.category === 'Solution') catIndex = 4;

    return {
      id: node.id, // ECharts graph uses id or name
      name: node.name,
      value: node.category,
      category: catIndex,
      symbolSize: 40,
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
      text: 'Power Equipment Knowledge Graph',
      subtext: 'Force Directed Layout',
      top: 'bottom',
      left: 'right'
    },
    tooltip: {
        trigger: 'item',
        formatter: '{b}: {c}'
    },
    legend: [
      {
        data: categories.map(a => a.name)
      }
    ],
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
          repulsion: 500,
          edgeLength: 150
        },
        lineStyle: {
            color: 'source',
            curveness: 0.3
        },
        edgeSymbol: ['none', 'arrow'],
        edgeSymbolSize: 10
      }
    ]
  };

  myChart.setOption(option);
  
  // Handle resize
  window.addEventListener('resize', () => {
      myChart.resize();
  });
};

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8081/api/knowledge-graph/whole-graph');
    if (response.data) {
        initChart(response.data);
    }
  } catch (error) {
    console.error('Failed to fetch graph data:', error);
    // Use dummy data if fetch fails for testing visual
    // initChart(dummyData);
  }
});
</script>

<style scoped>
.knowledge-graph-container {
  padding: 20px;
  background-color: #f5f7fa;
}
</style>
