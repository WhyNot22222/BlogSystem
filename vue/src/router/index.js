import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Manager from '../views/Manager.vue'
import Blog from '../views/Blog.vue'
import LoginRegister from '../views/LoginRegister.vue'
import Community from '../views/Community.vue'
import AccountSettings from '../views/AccountSettings.vue'
import Follow from '../views/Follow.vue'
import Collection from '../views/Collection.vue'
import EnhancedBlogEditor from '../views/EnhancedBlogEditor.vue'
import BlogDetail from '../views/BlogDetail.vue'
import Test from '../views/Test.vue'
import EnhancedCommunity from '../views/EnhancedCommunity.vue'
import NotFound from '../views/404.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Manager,
      redirect: '/login',
      children: [
        { path: '/home', component: EnhancedCommunity },
        { path: '/blog', component: Community },
        { path: '/community', component: EnhancedCommunity },
        { path: '/subscribe', component: Blog },
        { path: '/follow', component: Follow },
        { path: '/collect', component: Collection },
        { path: '/history', component: Blog },
        { path: '/manage/articles', component: Blog },
        { path: '/manage/users', component: Blog },
        { path: '/manage/comments', component: Blog },
        { path: '/settings/profile', component: AccountSettings },
        { path: '/settings/website', component: Blog },
        {
          path: '/enhanced-community',
          component: EnhancedCommunity,
        },
      ]
    },
    {
      path: '/login',
      component: LoginRegister,
    },
    {
      path: '/404',
      name: '404',
      component: NotFound,
    },
    {
      path: '/:pathMatch(.*)',
      redirect: '/404'
    },
    {
      path: '/blog-editor',
      component: EnhancedBlogEditor
    },
    {
      path: '/blog-detail',
      component: BlogDetail
    },
    {
      path: '/test',
      component: Test
    }
  ],
})

export default router
