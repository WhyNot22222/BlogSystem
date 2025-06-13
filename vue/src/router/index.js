import { createRouter, createWebHistory } from 'vue-router'
import Manager from '../views/Manager.vue'
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
import BlogManager from '../views/BlogManager.vue'
import CommentManager from '../views/CommentManager.vue'
import UserManager from '../views/UserManager.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Manager,
      redirect: '/login',
      children: [
        { path: '/home', component: Community },
        { path: '/community', component: EnhancedCommunity },
        { path: '/follow', component: Follow },
        { path: '/collect', component: Collection },
        { path: '/manage/articles', component: BlogManager },
        { path: '/manage/users', component: UserManager },
        { path: '/manage/comments', component: CommentManager },
        { path: '/settings/profile', component: AccountSettings },
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
