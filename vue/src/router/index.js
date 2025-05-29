import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Manager from '../views/Manager.vue'
import Blog from '../views/Blog.vue'
import LoginRegister from '../views/LoginRegister.vue'
import Community from '../views/Community.vue'
import AccountSettings from '../views/AccountSettings.vue'
import Follow from '../views/Follow.vue'
import Collection from '../views/Collection.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Manager,
      redirect: '/login',
      children: [
        { path: '/home', component: Home },
        { path: '/blog', component: Blog },
        { path: '/community', component: Community },
        { path: '/subscribe', component: Blog },
        { path: '/follow', component: Follow },
        { path: '/collect', component: Collection },
        { path: '/history', component: Blog },
        { path: '/manage/articles', component: Blog },
        { path: '/manage/users', component: Blog },
        { path: '/manage/comments', component: Blog },
        { path: '/settings/profile', component: AccountSettings },
        { path: '/settings/website', component: Blog },
      ]
    },
    {
      path: '/login',
      component: LoginRegister,
    },
    {
      path: '/404',
      name: '404',
      component: import('../views/404.vue')
    },
    {
      path: '/:pathMatch(.*)',
      redirect: '/404'
    },
    {
      path: '/blog-editor',
      component: import('../views/BlogEditor.vue')
    },
    {
      path: '/enhanced-blog-editor',
      component: import('../views/EnhancedBlogEditor.vue')
    },
    {
      path: '/blog-detail',
      component: import('../views/BlogDetail.vue')
    },
    {
      path: '/test',
      component: import('../views/Test.vue')
    }
  ],
})

export default router
