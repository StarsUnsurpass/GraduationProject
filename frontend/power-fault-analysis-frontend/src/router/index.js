import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import KnowledgeGraph from '../views/KnowledgeGraph.vue'
import FaultAnalysis from '../views/FaultAnalysis.vue'
import DataManagement from '../views/DataManagement.vue'
import Login from '../views/Login.vue'
import UserManagement from '../views/UserManagement.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/knowledge-graph',
    name: 'KnowledgeGraph',
    component: KnowledgeGraph,
    meta: { requiresAuth: true }
  },
  {
    path: '/fault-analysis',
    name: 'FaultAnalysis',
    component: FaultAnalysis,
    meta: { requiresAuth: true }
  },
  {
    path: '/data-management',
    name: 'DataManagement',
    component: DataManagement,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
      path: '/user-management',
      name: 'UserManagement',
      component: UserManagement,
      meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  
  if (to.meta.requiresAuth && !token) {
    next('/login');
  } else if (to.meta.requiresAdmin && user.role !== 'ADMIN') {
     alert('无权访问此页面');
     next(from.path); // or home
  } else {
    next();
  }
})

export default router
