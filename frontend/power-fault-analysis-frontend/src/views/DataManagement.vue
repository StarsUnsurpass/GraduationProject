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

              <el-form-item label="描述/备注">
                  <el-input v-model="entityDescription" type="textarea" placeholder="请输入备注信息..." />
              </el-form-item>

              <el-form-item label="实体属性 (JSON/文本)">
                  <el-input v-model="entityAttributes" type="textarea" placeholder="例如: 电压: 220V (换行) 电流: 5A" />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" size="large" @click="createEntity" :icon="Plus">立即创建</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- Tab 2: Manage Entities -->
        <el-tab-pane name="manage">
          <template #label>
            <span class="custom-tab-label">
              <el-icon><List /></el-icon>
              <span>管理实体 (Manage)</span>
            </span>
          </template>
          
          <div class="form-container">
            <el-form inline>
                <el-form-item label="查看类型">
                    <el-select v-model="manageType" placeholder="请选择类型" style="width: 200px">
                      <el-option label="设备类型 (DeviceType)" value="devicetype" />
                      <el-option label="设备部件 (Component)" value="component" />
                      <el-option label="故障现象 (FaultPhenomenon)" value="faultphenomenon" />
                      <el-option label="故障原因 (FaultCause)" value="faultcause" />
                      <el-option label="解决方案 (Solution)" value="solution" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button @click="fetchManageList" :icon="Search">刷新</el-button>
                </el-form-item>
            </el-form>

            <el-table :data="manageList" v-loading="manageLoading" border stripe style="width: 100%; margin-top: 20px;">
                <el-table-column prop="name" label="名称" width="200" />
                <el-table-column prop="description" label="描述/备注" show-overflow-tooltip />
                <el-table-column prop="attributes" label="属性" show-overflow-tooltip />
                <el-table-column label="操作" width="180">
                    <template #default="scope">
                        <el-button size="small" :icon="Edit" circle @click="openEditDialog(scope.row)" style="margin-right: 5px;"/>
                        <el-popconfirm title="确定删除该实体及相关关系吗？" @confirm="deleteEntity(scope.row.name)">
                            <template #reference>
                                <el-button type="danger" size="small" :icon="Delete" circle />
                            </template>
                        </el-popconfirm>
                    </template>
                </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- Tab 3: Create Relationships -->
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

         <!-- Tab 4: Manage Relationships -->
        <el-tab-pane name="manage-relations">
          <template #label>
            <span class="custom-tab-label">
              <el-icon><Link /></el-icon>
              <span>管理关系 (Rel)</span>
            </span>
          </template>
          
           <div class="form-container">
               <el-button @click="fetchRelationships" :icon="Search" style="margin-bottom: 15px;">刷新关系列表</el-button>
               
               <el-table :data="relationships" v-loading="relationLoading" border stripe style="width: 100%;" height="500">
                    <el-table-column prop="source" label="起点 (Source)" />
                    <el-table-column prop="name" label="关系 (Type)" width="180">
                        <template #default="scope">
                            <el-tag>{{ scope.row.name }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="target" label="终点 (Target)" />
                    <el-table-column label="操作" width="100">
                        <template #default="scope">
                            <el-popconfirm title="确定删除此关系？" @confirm="deleteRelationship(scope.row)">
                                <template #reference>
                                    <el-button type="danger" size="small" :icon="Delete" circle />
                                </template>
                            </el-popconfirm>
                        </template>
                    </el-table-column>
               </el-table>
           </div>
        </el-tab-pane>


        <!-- Tab 5: Batch Import -->
        <el-tab-pane name="import">
          <template #label>
            <span class="custom-tab-label">
              <el-icon><Upload /></el-icon>
              <span>批量导入 (Excel)</span>
            </span>
          </template>

          <div class="form-container">
            <el-alert title="提示: 上传 Excel 文件批量导入数据。" type="warning" show-icon :closable="false" style="margin-bottom:20px;">
                <template #default>
                    <div>
                        <p>请确保文件包含以下 Sheet: <b>DeviceType, Component, FaultPhenomenon, FaultCause, Solution, Relationships</b></p>
                        <p>Relationship Sheet 列顺序: Source | Target | Type (HAS_COMPONENT, HAS_POSSIBLE_FAULT, CAUSED_BY, SOLVED_BY)</p>
                    </div>
                </template>
            </el-alert>

            <div style="margin-bottom: 20px;">
                <el-button type="success" plain @click="downloadTemplate" :icon="Download">下载 Excel 模板</el-button>
            </div>

            <el-upload
              class="upload-demo"
              drag
              :action="uploadUrl"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :show-file-list="false"
              name="file"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                拖拽文件到此处 或 <em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 .xlsx 文件
                </div>
              </template>
            </el-upload>
          </div>
        </el-tab-pane>

      </el-tabs>
    </el-card>

    <!-- Edit Dialog -->
    <el-dialog v-model="showEditDialog" title="编辑实体信息">
        <el-form :model="editForm">
            <el-form-item label="名称 (ID)">
                <el-input v-model="editForm.name" placeholder="修改名称 (慎重: 会影响所有关联)" />
            </el-form-item>
            <el-form-item label="描述/备注">
                <el-input v-model="editForm.description" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="实体属性">
                <el-input v-model="editForm.attributes" type="textarea" :rows="3" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showEditDialog = false">取消</el-button>
                <el-button type="primary" @click="updateEntity">保存</el-button>
            </span>
        </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { Plus, Check, Edit, CirclePlus, Link, Right, Upload, UploadFilled, Download, Delete, List, Search } from '@element-plus/icons-vue';

const activeTab = ref('entities');
const entityType = ref('devicetype');
const entityName = ref('');
const entityDescription = ref('');
const entityAttributes = ref('');

// Manage Tab State
const manageType = ref('devicetype');
const manageList = ref([]);
const manageLoading = ref(false);

const showEditDialog = ref(false);
const editForm = ref({ name: '', originalName: '', description: '', attributes: '', type: '' });

// Manage Relationships State
const relationships = ref([]);
const relationLoading = ref(false);

const relationType = ref('has_component');
const sourceId = ref('');
const targetId = ref('');

// Data Lists
const deviceList = ref([]);
const componentList = ref([]);
const faultList = ref([]);
const causeList = ref([]);
const solutionList = ref([]);

const apiBase = import.meta.env.VITE_API_BASE_URL + '/api/knowledge-graph';
const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/api/data/import';
const templateUrl = import.meta.env.VITE_API_BASE_URL + '/power_fault_template.xlsx';

const downloadTemplate = () => {
    window.open(templateUrl, '_blank');
};

// Map lowercase type to Backend Class Name
const getCapitalizedType = (type) => {
    const map = {
        'devicetype': 'DeviceType',
        'component': 'Component',
        'faultphenomenon': 'FaultPhenomenon',
        'faultcause': 'FaultCause',
        'solution': 'Solution'
    };
    return map[type] || type;
}

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

const fetchManageList = async () => {
    manageLoading.value = true;
    try {
        const res = await axios.get(`${apiBase}/${manageType.value}`);
        manageList.value = res.data;
    } catch (e) {
        ElMessage.error("获取列表失败");
    } finally {
        manageLoading.value = false;
    }
}

const fetchRelationships = async () => {
    relationLoading.value = true;
    try {
        const res = await axios.get(`${apiBase}/whole-graph`);
        relationships.value = res.data.links;
    } catch (e) {
        ElMessage.error("获取关系列表失败");
    } finally {
        relationLoading.value = false;
    }
}

const createEntity = async () => {
    if (!entityName.value) {
        ElMessage.warning('请输入名称');
        return;
    }
    
    try {
        await axios.post(`${apiBase}/${entityType.value}`, {
            name: entityName.value,
            description: entityDescription.value,
            attributes: entityAttributes.value
        });
        ElMessage.success(`创建成功: ${entityName.value}`);
        entityName.value = '';
        entityDescription.value = '';
        entityAttributes.value = '';
        fetchAllLists();
    } catch {
        ElMessage.error('创建失败，可能已存在。');
    }
};

const deleteEntity = async (name) => {
    try {
        await axios.delete(`${apiBase}/${manageType.value}/${name}`);
        ElMessage.success("删除成功");
        fetchManageList();
        fetchAllLists(); 
    } catch (e) {
        ElMessage.error("删除失败");
    }
}

const openEditDialog = (row) => {
    editForm.value = {
        name: row.name,
        originalName: row.name,
        description: row.description,
        attributes: row.attributes,
        type: manageType.value
    };
    showEditDialog.value = true;
};

const updateEntity = async () => {
    try {
        // Handle Rename if name changed
        if (editForm.value.name !== editForm.value.originalName) {
            await axios.put(`${apiBase}/rename`, {
                label: getCapitalizedType(editForm.value.type),
                oldName: editForm.value.originalName,
                newName: editForm.value.name
            });
        }

        // Update other properties
        await axios.post(`${apiBase}/${editForm.value.type}`, {
            name: editForm.value.name, // Use new name
            description: editForm.value.description,
            attributes: editForm.value.attributes
        });
        
        ElMessage.success("更新成功");
        showEditDialog.value = false;
        fetchManageList();
        fetchAllLists(); // Refresh dropdowns as name might have changed
    } catch (e) {
        console.error(e);
        ElMessage.error("更新失败: " + (e.response?.data?.message || e.message));
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
    } catch {
         ElMessage.error('关联建立失败');
    }
};

const deleteRelationship = async (row) => {
    let url = '';
    if (row.name === 'HAS_COMPONENT') {
        url = `${apiBase}/devicetype/${row.source}/component/${row.target}`;
    } else if (row.name === 'HAS_POSSIBLE_FAULT') {
        url = `${apiBase}/component/${row.source}/fault/${row.target}`;
    } else if (row.name === 'CAUSED_BY') {
         url = `${apiBase}/faultphenomenon/${row.source}/cause/${row.target}`;
    } else if (row.name === 'SOLVED_BY') {
         url = `${apiBase}/faultcause/${row.source}/solution/${row.target}`;
    }
    
    if (!url) return;

    try {
        await axios.delete(url);
        ElMessage.success("关系删除成功");
        fetchRelationships();
    } catch (e) {
        ElMessage.error("删除失败");
    }
}

const refreshLists = () => {
    sourceId.value = '';
    targetId.value = '';
    fetchAllLists(); 
};

const getRelationLabel = (type) => {
    const map = {
        'has_component': 'HAS_COMPONENT',
        'has_fault': 'HAS_POSSIBLE_FAULT',
        'caused_by': 'CAUSED_BY',
        'solved_by': 'SOLVED_BY'
    };
    return map[type];
}

const handleUploadSuccess = () => {
  ElMessage.success('批量导入成功！');
  fetchAllLists();
};

const handleUploadError = () => {
  ElMessage.error('导入失败，请检查文件格式。');
};

// Watchers
watch(manageType, () => {
    fetchManageList();
});
watch(activeTab, (val) => {
    if (val === 'manage') {
        fetchManageList();
    } else if (val === 'manage-relations') {
        fetchRelationships();
    }
});

onMounted(() => {
    fetchAllLists();
});
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
    background-color: var(--el-fill-color-light);
    border-radius: 8px;
    transition: background-color 0.3s;
}

.node-box {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 10px;
    text-align: center;
    font-weight: bold;
    color: var(--el-text-color-regular);
}

.arrow {
    flex: 0.5;
    display: flex;
    flex-direction: column;
    align-items: center;
    color: var(--el-color-primary);
}

.rel-name {
    font-size: 12px;
    margin-top: 5px;
    background: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    padding: 2px 8px;
    border-radius: 10px;
}
</style>
