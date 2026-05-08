import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/products', name: 'Products', component: () => import('../views/Products.vue') },
  { path: '/articles', name: 'Articles', component: () => import('../views/Articles.vue') },
  { path: '/profile', name: 'Profile', component: () => import('../views/Profile.vue') },
  { path: '/cart', name: 'Cart', component: () => import('../views/Cart.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
