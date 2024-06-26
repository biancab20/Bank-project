import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/login',
      name: 'LoginPage',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/SignUpView.vue')
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/UserDashboardView.vue')
    },
    {
      path: '/employee-dashboard',
      name: 'dashboard',
      component: () => import('../views/EmployeeDashboardView.vue')
    }
  ]
})

export default router
