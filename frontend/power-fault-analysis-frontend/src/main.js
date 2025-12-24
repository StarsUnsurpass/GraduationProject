import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router' // Import the router instance

createApp(App).use(ElementPlus).use(router).mount('#app')
