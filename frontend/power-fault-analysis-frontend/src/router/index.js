import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import KnowledgeGraph from '../views/KnowledgeGraph.vue'
import FaultAnalysis from '../views/FaultAnalysis.vue'
import DataManagement from '../views/DataManagement.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/knowledge-graph',
    name: 'KnowledgeGraph',
    component: KnowledgeGraph
  },
  {
    path: '/fault-analysis',
    name: 'FaultAnalysis',
    component: FaultAnalysis
  },
  {
    path: '/data-management',
    name: 'DataManagement',
    component: DataManagement
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
