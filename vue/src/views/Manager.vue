<template>
  <div class="app-container">
    <!-- Header Area -->
    <header class="header">
      <div class="logo-container">
        <el-button class="menu-toggle" text @click="toggleSidebar">
          <el-icon><Menu /></el-icon>
        </el-button>
        <div class="logo">
          <span class="logo-icon">📚</span>
          <span class="logo-text">MyBLOG</span>
        </div>
      </div>

      <div class="search-container">
        <el-input
            placeholder="搜索内容"
            clearable
            class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" class="search-button">搜索</el-button>
      </div>

      <div class="user-actions">
        <el-dropdown trigger="click">
          <div class="user-avatar">
            <el-avatar
                :size="40"
                @error="handleAvatarError">
              <img :src="avatarUrl" alt="avatar">
            </el-avatar>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleProfileSettings">
                <el-icon><UserFilled /></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button
            type="primary"
            class="create-button"
            @click="openBlogEditor">
          <el-icon><Plus /></el-icon>创作
        </el-button>
      </div>
    </header>

    <!-- Main Content Area -->
    <div class="main-container">
      <!-- Sidebar Navigation - Fixed -->
      <aside class="sidebar" :class="{ 'collapsed': isCollapsed }">
        <el-menu
            default-active="1"
            class="sidebar-menu"
            :collapse="isCollapsed"
            @select="handleMenuSelect"
            background-color="#f7f8fa"
            text-color="#333"
            active-text-color="#e83929"
        >
          <el-menu-item index="1">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <el-menu-item index="2">
            <el-icon><Document /></el-icon>
            <span>博客</span>
          </el-menu-item>

          <el-menu-item index="3">
            <el-icon><ChatDotRound /></el-icon>
            <span>社区</span>
          </el-menu-item>

          <el-menu-item index="4">
            <el-icon><Star /></el-icon>
            <span>关注</span>
          </el-menu-item>

          <el-menu-item index="5">
            <el-icon><Collection /></el-icon>
            <span>收藏</span>
          </el-menu-item>

          <el-menu-item index="6">
            <el-icon><Clock /></el-icon>
            <span>历史</span>
          </el-menu-item>

          <el-divider></el-divider>

          <el-sub-menu index="7">
            <template #title>
              <el-icon><Management /></el-icon>
              <span>数据管理</span>
            </template>
            <el-menu-item index="7-1">文章管理</el-menu-item>
            <el-menu-item index="7-2">用户管理</el-menu-item>
            <el-menu-item index="7-3">评论管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="8">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="8-1">个人设置</el-menu-item>
            <el-menu-item index="8-2">网站设置</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </aside>

      <!-- Content Area - Scrollable -->
      <main class="content" :class="{ 'content-expanded': isCollapsed }">
        <RouterView v-slot="{ Component }" :key="$route.fullPath">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </RouterView>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { onMounted, ref, onUnmounted } from 'vue'
import defaultAvatar from '@/imgs/default.jpg'  // 导入默认头像
import {
  House,
  Menu,
  Search,
  Plus,
  Document,
  ChatDotRound,
  Management,
  Setting,
  Star,
  Collection,
  Clock,
  UserFilled,
  SwitchButton
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const isCollapsed = ref(false)

const avatarUrl = ref('')
const store = useStore()
const userId = computed(() => store.getters.userId)
console.log('userId: ', userId.value)

if (!userId.value) {
  ElMessage.warning('请先登录')
  router.push('/login')  // 跳转到登录界面
}

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 路由跳转逻辑
const handleMenuSelect = (index) => {
  store.commit('clearCurrentData')
  const routeMap = {
    '1': '/home',               // 首页
    '2': '/blog',               // 博客
    '3': '/community',          // 社区
    '4': '/follow',             // 关注
    '5': '/collect',            // 收藏
    '6': '/history',            // 历史
    '7-1': '/manage/articles',  // 文章管理
    '7-2': '/manage/users',     // 用户管理
    '7-3': '/manage/comments',  // 评论管理
    '8-1': '/settings/profile', // 个人设置
    '8-2': '/settings/website'  // 网站设置
  }
  if (routeMap[index]) {
    router.push(routeMap[index])
  }
}

// 处理头像加载错误
const handleAvatarError = () => {
  avatarUrl.value = defaultAvatar
}

// 获取头像方法
const fetchAvatar = async () => {
  try {
    request.get('/file/getAvatar', {
      params: { userId: userId.value },
      responseType: 'json'
    }).then(res => {
      if (res.code === '200') {
        // 将 Base64 字符串转换为二进制数据
        const binaryString = window.atob(res.data);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
          bytes[i] = binaryString.charCodeAt(i);
        }
        // 创建 Blob 对象
        const blob = new Blob([bytes], { type: 'image/jpeg' });
        // 创建临时 URL
        avatarUrl.value = URL.createObjectURL(blob);
      } else {
        ElMessage.error('头像加载失败：' + res.msg);
        avatarUrl.value = defaultAvatar;  // 使用导入的默认头像
      }
    })
  } catch (error) {
    console.error('头像加载失败:', error);
    avatarUrl.value = defaultAvatar;  // 使用导入的默认头像
  }
}

// 组件挂载时获取头像
onMounted(() => {
  fetchAvatar()
})

// 组件卸载时清理 URL
onUnmounted(() => {
  if (avatarUrl.value && avatarUrl.value.startsWith('blob:')) {
    URL.revokeObjectURL(avatarUrl.value);
  }
})

const handleProfileSettings = () => {
  router.push('/settings/profile')
}

const handleLogout = () => {
  store.dispatch('logout')
  router.push('/login')
  ElMessage.success('退出登录成功')
}

const openBlogEditor = () => {
  // Open blog editor in a new tab
  window.open('/blog-editor', '_blank')
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  display: flex;
  flex-direction: column;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

/* Header Styles */
.header {
  height: 64px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  justify-content: space-between;
}

.logo-container {
  display: flex;
  align-items: center;
  min-width: 180px;
}

.menu-toggle {
  margin-right: 12px;
  font-size: 20px;
  color: #606266;
  transition: all 0.3s;
}

.menu-toggle:hover {
  color: #e83929;
  transform: scale(1.1);
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  letter-spacing: 0.5px;
}

.search-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 600px;
}

.search-input {
  border-radius: 20px;
  transition: all 0.3s;
}

.search-input:focus-within {
  box-shadow: 0 0 0 2px rgba(232, 57, 41, 0.2);
}

.search-button {
  margin-left: 12px;
  border-radius: 20px;
  background-color: #e83929;
  border: none;
  padding: 10px 20px;
  transition: all 0.3s;
}

.search-button:hover {
  background-color: #d62c1e;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(232, 57, 41, 0.3);
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  min-width: 180px;
  justify-content: flex-end;
}

.user-avatar {
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.create-button {
  background-color: #e83929;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.create-button:hover {
  background-color: #d62c1e;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(232, 57, 41, 0.3);
}

/* Main Container Styles */
.main-container {
  display: flex;
  min-height: calc(100vh - 64px);
  margin-top: 64px; /* 为固定的header留出空间 */
}

/* Sidebar Styles - 固定不滚动 */
.sidebar {
  width: 220px;
  background-color: #f7f8fa;
  border-right: 1px solid #eee;
  transition: all 0.3s ease;
  position: fixed;
  top: 64px; /* 在header下方 */
  left: 0;
  bottom: 0;
  overflow-y: auto; /* 如果侧边栏内容过多，允许在侧边栏内部滚动 */
  z-index: 90;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.03);
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-menu {
  height: 100%;
  border-right: none;
  padding-top: 12px;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 0;
  border-radius: 0 24px 24px 0;
  margin-right: 12px;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(232, 57, 41, 0.1);
  color: #e83929;
  font-weight: 500;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(0, 0, 0, 0.05);
}

.sidebar-menu :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  margin: 4px 0;
}

.sidebar-menu :deep(.el-divider--horizontal) {
  margin: 16px 0;
}

/* Content Styles - 可滚动 */
.content {
  flex: 1;
  padding: 24px;
  margin-left: 240px; /* 与sidebar宽度相同 */
  margin-top:20px; /* 与header高度相同 */
  transition: margin-left 0.3s ease;
  overflow-y: auto; /* 允许内容区域垂直滚动 */
  min-height: calc(100vh - 108px);
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-right: 24px;
  margin-bottom: 24px;
}

.content-expanded {
  margin-left: 64px; /* 与折叠后的sidebar宽度相同 */
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .sidebar {
    width: 64px;
  }

  .content {
    margin-left: 64px;
    padding: 16px;
    margin-right: 16px;
    margin-bottom: 16px;
  }

  .search-container {
    max-width: 300px;
  }

  .logo-text {
    display: none;
  }
}

@media (max-width: 576px) {
  .header {
    padding: 0 12px;
  }

  .search-container {
    max-width: 200px;
  }

  .search-button {
    padding: 8px 12px;
  }

  .create-button {
    padding: 8px 12px;
  }
}
</style>