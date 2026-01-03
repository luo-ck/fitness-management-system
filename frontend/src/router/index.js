import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import CoachList from '../views/CoachList.vue'
import UserCenter from '../views/UserCenter.vue'
import UserProfile from '../views/UserProfile.vue'
import UserMyTraining from '../views/UserMyTraining.vue'
import UserMyPage from '../views/UserMyPage.vue'
import CoachLogin from '../views/CoachLogin.vue'
import AdminLogin from '../views/AdminLogin.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import AdminUserManagement from '../views/AdminUserManagement.vue'
import AdminCoachManagement from '../views/AdminCoachManagement.vue'
import CoachApplication from '../views/CoachApplication.vue'
import UserInbox from '../views/UserInbox.vue'
import AdminCoachApplications from '../views/AdminCoachApplications.vue'
import AdminMessageManagement from '../views/AdminMessageManagement.vue'
import ChatView from '../views/ChatView.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/coach/login',
    name: 'CoachLogin',
    component: CoachLogin
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/coaches',
    name: 'CoachList',
    component: CoachList,
    meta: { requiresAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: UserCenter,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: UserProfile,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/my-training',
    name: 'UserMyTraining',
    component: UserMyPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/my',
    name: 'UserMyPage',
    component: UserMyPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/coach/dashboard',
    name: 'CoachDashboard',
    component: () => import('../views/CoachDashboard.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/coach/student/:userId',
    name: 'CoachStudentDetail',
    component: () => import('../views/CoachStudentDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/coach/student/:userId/plans',
    name: 'CoachTrainingPlans',
    component: () => import('../views/CoachTrainingPlans.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/coach/student/:userId/plan/:planId?',
    name: 'CoachTrainingPlanEdit',
    component: () => import('../views/CoachTrainingPlanEdit.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/coach/profile',
    name: 'CoachProfile',
    component: () => import('../views/CoachProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUserManagement',
    component: AdminUserManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/coaches',
    name: 'AdminCoachManagement',
    component: AdminCoachManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/coach-applications',
    name: 'AdminCoachApplications',
    component: AdminCoachApplications,
    meta: { requiresAuth: true }
  },
  {   path: '/admin/messages',
    name: 'AdminMessageManagement',
    component: AdminMessageManagement,
    meta: { requiresAuth: true }
  },
  {   path: '/admin/feedback',
    name: 'AdminFeedbackManagement',
    component: () => import('../views/AdminFeedbackManagement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/coach-application',
    name: 'CoachApplication',
    component: CoachApplication,
    meta: { requiresAuth: true }
  },
  {
    path: '/user/inbox',
    name: 'UserInbox',
    component: UserInbox,
    meta: { requiresAuth: true }
  },
  {
    path: '/chat',
    name: 'ChatView',
    component: ChatView,
    meta: { requiresAuth: true }
  },
  {
    path: '/coach/chat',
    name: 'CoachChatView',
    component: () => import('../views/CoachChatView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫，验证用户是否已登录
router.beforeEach((to, from, next) => {
  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 获取用户类型和认证信息
    const userType = localStorage.getItem('userType') || 'user'
    const isAuthenticated = !!localStorage.getItem('token')
    
    if (!isAuthenticated) {
      // 根据路径跳转到对应的登录页面
      // 注意：区分/coaches（教练列表，用户访问）和/coach（教练登录后页面，教练访问）
      if (to.path === '/coaches' || to.path.startsWith('/user')) {
        // 教练列表和用户相关页面，跳转到用户登录
        next('/login')
      } else if (to.path.startsWith('/coach')) {
        // 教练相关页面，跳转到教练登录
        next('/coach/login')
      } else if (to.path.startsWith('/admin')) {
        // 管理员相关页面，跳转到管理员登录
        next('/admin/login')
      } else {
        // 默认跳转到用户登录
        next('/login')
      }
      return
    }
    
    // 检查当前登录用户类型是否匹配访问路径
    if (to.path.startsWith('/admin')) {
      // 管理员相关页面，需要管理员权限
      next()
    } else if (to.path === '/coaches') {
      // 教练列表页面，任何认证用户都可以访问
      next()
    } else if (to.path.startsWith('/coach')) {
      // 教练相关页面，需要教练权限
      if (userType === 'coach') {
        next()
      } else {
        // 非教练用户，跳转到教练登录
        next('/coach/login')
      }
    } else if (to.path.startsWith('/user')) {
      // 用户相关页面，需要用户权限
      if (userType === 'user') {
        next()
      } else {
        // 非用户，跳转到用户登录
        next('/login')
      }
    } else {
      // 其他页面，允许访问
      next()
    }
  }
  next()
})

export default router
