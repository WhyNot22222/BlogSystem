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
        <el-button type="primary" class="search-button" style="margin-left: 10px; background-color: #e83929; border: none;">搜索</el-button>
      </div>

      <div class="user-actions">
        <el-dropdown trigger="click">
          <div class="user-avatar">
            <el-avatar
                :size="36"
                @error="handleAvatarError">
              <img :src="avatarUrl" alt="avatar">
            </el-avatar>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleProfileSettings">个人设置</el-dropdown-item>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button
            type="primary"
            class="create-button"
            style="background-color: #e83929; border: none;"
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
            <el-icon><Connection /></el-icon>
            <span>订阅</span>
          </el-menu-item>

          <el-menu-item index="5">
            <el-icon><Star /></el-icon>
            <span>关注</span>
          </el-menu-item>

          <el-menu-item index="6">
            <el-icon><Collection /></el-icon>
            <span>收藏</span>
          </el-menu-item>

          <el-menu-item index="7">
            <el-icon><Clock /></el-icon>
            <span>历史</span>
          </el-menu-item>

          <el-divider></el-divider>

          <el-sub-menu index="8">
            <template #title>
              <el-icon><Management /></el-icon>
              <span>数据管理</span>
            </template>
            <el-menu-item index="8-1">文章管理</el-menu-item>
            <el-menu-item index="8-2">用户管理</el-menu-item>
            <el-menu-item index="8-3">评论管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="9">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="9-1">个人设置</el-menu-item>
            <el-menu-item index="9-2">网站设置</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </aside>

      <!-- Content Area - Scrollable -->
      <main class="content" :class="{ 'content-expanded': isCollapsed }">
        <RouterView />
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
  Download,
  Reading,
  ChatDotRound,
  Management,
  Setting,
  ArrowRight,
  InfoFilled,
  Connection,
  Star,
  Collection,
  Clock
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
  const routeMap = {
    '1': '/home',               // 首页
    '2': '/blog',               // 博客
    '3': '/community',          // 社区
    '4': '/subscribe',          // 订阅
    '5': '/follow',             // 关注
    '6': '/collect',            // 收藏
    '7': '/history',            // 历史
    '8-1': '/manage/articles',  // 文章管理
    '8-2': '/manage/users',     // 用户管理
    '8-3': '/manage/comments',  // 评论管理
    '9-1': '/settings/profile', // 个人设置
    '9-2': '/settings/website'  // 网站设置
  }
  if (routeMap[index]) {
    router.push(routeMap[index])
  }
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
}

/* Header Styles */
.header {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
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
  min-width: 150px;
}

.search-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 600px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  min-width: 150px;
  justify-content: flex-end;
}

/* Main Container Styles */
.main-container {
  display: flex;
  min-height: calc(100vh - 60px);
  margin-top: 60px; /* 为固定的header留出空间 */
}

/* Sidebar Styles - 固定不滚动 */
.sidebar {
  width: 200px;
  background-color: #f7f8fa;
  border-right: 1px solid #eee;
  transition: width 0.3s;
  position: fixed;
  top: 60px; /* 在header下方 */
  left: 0;
  bottom: 0;
  overflow-y: auto; /* 如果侧边栏内容过多，允许在侧边栏内部滚动 */
  z-index: 90;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-menu {
  height: 100%;
  border-right: none;
}

/* Content Styles - 可滚动 */
.content {
  flex: 1;
  padding: 20px;
  margin-left: 240px; /* 与sidebar宽度相同 */
  transition: margin-left 0.3s;
  overflow-y: auto; /* 允许内容区域垂直滚动 */
  min-height: calc(100vh - 60px);
}

.content-expanded {
  margin-left: 64px; /* 与折叠后的sidebar宽度相同 */
}


.news-image img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.project-logo img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
}
</style>