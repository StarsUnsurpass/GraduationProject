<template>
  <div class="data-management-container">
    <div class="page-header">
      <h2><el-icon><Edit /></el-icon> 知识库数据管理</h2>
      <p>录入新的电力设备实体或构建知识关联</p>
    </div>
    
    <el-card class="main-card">
      <el-tabs v-model="activeTab" class="custom-tabs">
        
        <!-- Tab 1: Create Entities -->
        <el-tab-pane name="entities">
          <template #label>
            <span class="custom-tab-label">
              <el-icon><CirclePlus /></el-icon>
              <span>创建实体 (Node)</span>
            </span>
          </template>
          
          <div class="form-container">
            <el-alert title="提示: 请先选择节点类型，输入名称后点击创建。" type="info" show-icon :closable="false" style="margin-bottom:20px;" />
            
            <el-form :model="entityForm" label-position="top">
              <el-row :gutter="20">
                <el-col :span="12">
                   <el-form-item label="节点类型">
                    <el-select v-model="entityType" placeholder="请选择类型" style="width: 100%">
                      <el-option label="设备类型 (DeviceType)" value="devicetype" />
                      <el-option label="设备部件 (Component)" value="component" />
                      <el-option label="故障现象 (FaultPhenomenon)" value="faultphenomenon" />
                      <el-option label="故障原因 (FaultCause)" value="faultcause" />
                      <el-option label="解决方案 (Solution)" value="solution" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                   <el-form-item label="实体名称">
                    <el-input v-model="entityName" placeholder="例如: 变压器, 油温过高..." />
                  </el-form-item>
                </el-col>
              </el-row>
              
              <el-form-item>
                <el-button type="primary" size="large" @click="createEntity" :icon="Plus">立即创建</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- Tab 2: Create Relationships -->
        <el-tab-pane name="relationships">
          <template #label>
            <span class="custom-tab-label">
              <el-icon><Link /></el-icon>
              <span>建立关联 (Link)</span>
            </span>
          </template>

          <div class="form-container">
             <el-alert title="提示: 定义两个已有节点之间的逻辑关系。" type="success" show-icon :closable="false" style="margin-bottom:20px;" />

            <el-form label-position="top">
              <el-form-item label="选择关系类型">
                <el-radio-group v-model="relationType" @change="refreshLists">
                  <el-radio-button label="has_component">设备 包含 部件</el-radio-button>
                  <el-radio-button label="has_fault">部件 产生 故障</el-radio-button>
                  <el-radio-button label="caused_by">故障 由...引起</el-radio-button>
                  <el-radio-button label="solved_by">原因 通过...解决</el-radio-button>
                </el-radio-group>
              </el-form-item>

              <div class="relationship-visualizer" v-if="relationType">
                  <div class="node-box source">
                      <span>起点</span>
                      <!-- Dynamic Source Selection -->
                      <el-select v-if="relationType === 'has_component'" v-model="sourceId" placeholder="选择设备" filterable>
                         <el-option v-for="item in deviceList" :key="item.name" :label="item.name" :value="item.name" />
                      </el-select>
                      <el-select v-else-if="relationType === 'has_fault'" v-model="sourceId" placeholder="选择部件" filterable>
                         <el-option v-for="item in componentList" :key="item.name" :label="item.name" :value="item.name" />
                      </el-select>
                      <el-select v-else-if="relationType === 'caused_by'" v-model="sourceId" placeholder="选择故障" filterable>
                         <el-option v-for="item in faultList" :key="item.name" :label="item.name" :value="item.name" />
                      </el-select>
                      <el-select v-else-if="relationType === 'solved_by'" v-model="sourceId" placeholder="选择原因" filterable>
                         <el-option v-for="item in causeList" :key="item.name" :label="item.name" :value="item.name" />
                      </el-select>
                  </div>
                  
                  <div class="arrow">
                      <el-icon :size="30"><Right /></el-icon>
                      <span class="rel-name">{{ getRelationLabel(relationType) }}</span>
                  </div>

                  <div class="node-box target">
                      <span>终点</span>
                      <!-- Dynamic Target Selection -->
                      <el-select v-if="relationType === 'has_component'" v-model="targetId" placeholder="选择部件" filterable>
                         <el-option v-for="item in componentList" :key="item.name" :label="item.name" :value="item.name" />
                      </el-select>
                      <el-select v-else-if="relationType === 'has_fault'" v-model="targetId" placeholder="选择故障" filterable>
                         <el-option v-for="item in faultList" :key="item.name" :label="item.name" :value="item.name" />
                      </el-select>
                      <el-select v-else-if="relationType === 'caused_by'" v-model="targetId" placeholder="选择原因" filterable>
                         <el-option v-for="item in causeList" :key="item.name" :label="item.name" :value="item.name" />
                      </el-select>
                      <el-select v-else-if="relationType === 'solved_by'" v-model="targetId" placeholder="选择方案" filterable>
                         <el-option v-for="item in solutionList" :key="item.name" :label="item.name" :value="item.name" />
                      </el-select>
                  </div>
              </div>

              <div style="margin-top: 30px; text-align: center;">
                <el-button type="success" size="large" @click="createRelationship" :disabled="!sourceId || !targetId || !relationType" :icon="Check">建立关联</el-button>
              </div>

            </el-form>
          </div>
        </el-tab-pane>

      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { Plus, Check, Edit, CirclePlus, Link, Right } from '@element-plus/icons-vue';

const activeTab = ref('entities');
const entityType = ref('devicetype');
const entityName = ref('');

const relationType = ref('has_component');
const sourceId = ref('');
const targetId = ref('');

// Data Lists
const deviceList = ref([]);
const componentList = ref([]);
const faultList = ref([]);
const causeList = ref([]);
const solutionList = ref([]);

const apiBase = 'http://localhost:8081/api/knowledge-graph';

// Fetch lists for dropdowns
const fetchAllLists = async () => {
    try {
        const [devices, components, faults, causes, solutions] = await Promise.all([
            axios.get(`${apiBase}/devicetype`),
            axios.get(`${apiBase}/component`),
            axios.get(`${apiBase}/faultphenomenon`),
            axios.get(`${apiBase}/faultcause`),
            axios.get(`${apiBase}/solution`)
        ]);
        deviceList.value = devices.data;
        componentList.value = components.data;
        faultList.value = faults.data;
        causeList.value = causes.data;
        solutionList.value = solutions.data;
    } catch (error) {
        console.error("Error fetching lists", error);
    }
};

const refreshLists = () => {
    sourceId.value = '';
    targetId.value = '';
    fetchAllLists(); 
};

onMounted(() => {
    fetchAllLists();
});

const getRelationLabel = (type) => {
    const map = {
        'has_component': 'HAS_COMPONENT',
        'has_fault': 'HAS_POSSIBLE_FAULT',
        'caused_by': 'CAUSED_BY',
        'solved_by': 'SOLVED_BY'
    };
    return map[type];
}

const createEntity = async () => {
    if (!entityName.value) {
        ElMessage.warning('请输入名称');
        return;
    }
    
    try {
        await axios.post(`${apiBase}/${entityType.value}`, {
            name: entityName.value
        });
        ElMessage.success(`创建成功: ${entityName.value}`);
        entityName.value = '';
        fetchAllLists();
    } catch (error) {
        ElMessage.error('创建失败，可能已存在。');
    }
};

const createRelationship = async () => {
    let url = '';
    
    if (relationType.value === 'has_component') {
        url = `${apiBase}/devicetype/${sourceId.value}/component/${targetId.value}`;
    } else if (relationType.value === 'has_fault') {
        url = `${apiBase}/component/${sourceId.value}/fault/${targetId.value}`;
    } else if (relationType.value === 'caused_by') {
        url = `${apiBase}/faultphenomenon/${sourceId.value}/cause/${targetId.value}`;
    } else if (relationType.value === 'solved_by') {
        url = `${apiBase}/faultcause/${sourceId.value}/solution/${targetId.value}`;
    }

    try {
        await axios.post(url);
        ElMessage.success('关联建立成功');
        sourceId.value = '';
        targetId.value = '';
    } catch (error) {
         ElMessage.error('关联建立失败');
    }
};
</script>

<style scoped>
.data-management-container {
    max-width: 900px;
    margin: 0 auto;
}

.page-header {
    margin-bottom: 20px;
    text-align: left;
}
.page-header h2 {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 5px;
}
.page-header p {
    color: #909399;
    margin: 0;
}

.main-card {
    min-height: 500px;
}

.custom-tab-label {
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 16px;
}

.form-container {
    padding: 20px;
}

.relationship-visualizer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 40px;
    padding: 20px;
    background-color: #f5f7fa;
    border-radius: 8px;
}

.node-box {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 10px;
    text-align: center;
    font-weight: bold;
    color: #606266;
}

.arrow {
    flex: 0.5;
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #409EFF;
}

.rel-name {
    font-size: 12px;
    margin-top: 5px;
    background: #ecf5ff;
    padding: 2px 8px;
    border-radius: 10px;
}
</style>