import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '工作台' } },
      { path: 'projects', name: 'Projects', component: () => import('@/views/project/ProjectList.vue'), meta: { title: '项目管理' } },
      { path: 'projects/create', name: 'ProjectCreate', component: () => import('@/views/project/ProjectCreate.vue'), meta: { title: '创建项目' } },
      { path: 'projects/:id', name: 'ProjectDetail', component: () => import('@/views/project/ProjectDetail.vue'), meta: { title: '项目详情' } },
      { path: 'reviews', name: 'Reviews', component: () => import('@/views/review/ReviewList.vue'), meta: { title: '评审管理' } },
      { path: 'reviews/my-tasks', name: 'MyReview', component: () => import('@/views/review/MyReview.vue'), meta: { title: '我的评审任务', roles: ['expert', 'teacher', 'college_admin', 'school_admin'] } },
      { path: 'reviews/score/:id', name: 'ReviewScore', component: () => import('@/views/review/ReviewScore.vue'), meta: { title: '评审打分' } },
      { path: 'milestones', name: 'Milestones', component: () => import('@/views/milestone/MilestoneList.vue'), meta: { title: '里程碑管理' } },
      { path: 'achievements', name: 'Achievements', component: () => import('@/views/achievement/AchievementList.vue'), meta: { title: '成果管理' } },
      { path: 'achievements/create', name: 'AchievementCreate', component: () => import('@/views/achievement/AchievementCreate.vue'), meta: { title: '提交成果' } },
      { path: 'messages', name: 'Messages', component: () => import('@/views/message/MessageList.vue'), meta: { title: '消息中心' } },
      { path: 'users', name: 'Users', component: () => import('@/views/user/UserList.vue'), meta: { title: '用户管理', roles: ['school_admin', 'college_admin'] } },
      { path: 'experts', name: 'Experts', component: () => import('@/views/expert/ExpertList.vue'), meta: { title: '专家管理', roles: ['school_admin', 'college_admin'] } },
      { path: 'colleges', name: 'Colleges', component: () => import('@/views/college/CollegeList.vue'), meta: { title: '学院管理', roles: ['school_admin'] } },
      { path: 'categories', name: 'Categories', component: () => import('@/views/category/CategoryList.vue'), meta: { title: '类别管理', roles: ['school_admin'] } },
      { path: 'statistics', name: 'Statistics', component: () => import('@/views/statistics/StatisticsPage.vue'), meta: { title: '数据统计', roles: ['school_admin', 'college_admin'] } },
      { path: 'profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人中心' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '高校创新项目管理系统'} - 高校创新项目管理系统`
  const userStore = useUserStore()
  const token = userStore.token

  if (to.meta.guest) {
    if (token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (!token) {
      next('/login')
    } else {
      const userRole = userStore.role
      const requiredRoles = to.meta.roles as string[] | undefined
      if (requiredRoles && !requiredRoles.includes(userRole)) {
        next('/dashboard')
      } else {
        next()
      }
    }
  }
})

export default router
