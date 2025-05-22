<template>
  <div class="blog-page">
    <!-- Category Navigation -->
    <div class="category-nav">
      <div class="category-nav-left">
        <el-button type="primary" class="category-btn active">推荐</el-button>
        <el-button class="category-btn">付费专栏</el-button>
        <el-button class="category-btn">VIP文章</el-button>
      </div>
    </div>

    <!-- Main Content Area -->
    <div class="content-container">
      <!-- Loading 状态 -->
      <div v-if="loading" class="loading-container">
        正在加载数据...
      </div>
      
      <!-- Error 状态 -->
      <div v-if="error" class="error-container">
        {{ error }}
      </div>

      <!-- Blog List -->
      <div v-else class="blog-list">
        <div v-for="post in posts" :key="post.id" class="blog-item">
          <div class="author-info">
            <el-avatar :size="36" :src="post.authorAvatar">
              <img :src=avatarUrl alt="用户头像">
            </el-avatar>
            <span class="author-name">{{ post.authorName }}</span>
          </div>
          <div class="blog-content">
            <h2 class="blog-title">{{ post.title }}</h2>
            <p class="blog-desc">{{ post.summary }}</p>

            <div class="blog-meta">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                阅读 {{ post.viewCount }}
              </span>
              <span class="meta-item">
                <el-icon><Thumb /></el-icon>
                {{ post.likeCount }} 赞
              </span>
              <span class="meta-item">
                <el-icon><Collection /></el-icon>
                收藏 {{ post.collectionCount }}
              </span>
              <el-dropdown>
                <span class="more-actions">
                  <el-icon><MoreFilled /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>举报</el-dropdown-item>
                    <el-dropdown-item>不感兴趣</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </div>
      
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  ArrowRight,
  View,
  Collection,
  MoreFilled
} from '@element-plus/icons-vue'
import request from '@/utils/request'

import { onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const posts = ref([])
const loading = ref(false)
const error = ref(null)

const fetchPosts = async () => {
  try {
    loading.value = true
    const res = await request.get('/posts/selectAll')
    if (res.code === '200') {
      posts.value = await Promise.all(
        res.data.map(async item => {
          try {
            const userRes = await request.post(`/user/getUser?userId=${item.userId}`)
            const avatarUrl = await fetchUserAvatar(item.userId)
            return {
              id: item.id,
              title: item.title,
              summary: item.summary,
              coverUrl: item.coverUrl,
              authorName: userRes.data?.username || '未知用户',
              authorAvatar: avatarUrl || '', // 使用获取到的头像地址
              viewCount: item.viewCount,
              likeCount: item.likeCount
            }
          } catch (e) {
            console.error('用户信息获取失败:', e)
            return {
              ...item,
              authorName: '未知用户',
              authorAvatar: ''
            }
          }
        })
      )
    }
  } catch (err) {
    error.value = '无法加载文章，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 头像获取
const fetchUserAvatar = async (userId) => {
  try {
    const res = await request.get('/file/getAvatar', {
      params: { userId },
      responseType: 'json'
    })
    if (res.code === '200') {
      const binaryString = window.atob(res.data)
      const bytes = new Uint8Array(binaryString.length)
      for (let i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i)
      }
      const blob = new Blob([bytes], { type: 'image/jpeg' })
      return URL.createObjectURL(blob)
    }
    return ''
  } catch (error) {
    console.error('头像加载失败:', error)
    return ''
  }
}

// 在组件挂载时自动获取数据
onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.blog-page {
  width: 100%;
}

/* Category Navigation */
.category-nav {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.category-nav-left {
  display: flex;
  gap: 10px;
}

.category-btn {
  font-weight: 500;
}

.category-btn.active {
  background-color: #e83929;
  border-color: #e83929;
  color: white;
}


/* Content Container */
.content-container {
  display: flex;
  gap: 20px;
}

/* Blog List */
.blog-list {
  flex: 1;
}

.blog-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.author-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 10px;
}

.author-name {
  font-weight: 500;
  color: #333;
}

.blog-content {
  flex: 1;
}

.blog-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 10px 0;
  color: #333;
  line-height: 1.4;
}

.blog-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 15px 0;
  line-height: 1.6;
}

.blog-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #999;
  font-size: 13px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.more-actions {
  cursor: pointer;
  color: #999;
}

/* Ad Item */
.ad-item {
  border: 1px solid #eee;
  background-color: #fafafa;
}

.ad-banner {
  margin-top: 10px;
  border-radius: 4px;
  overflow: hidden;
}

.ad-image {
  width: 5%;
  height: auto;
  object-fit: cover;
}

/* Responsive Adjustments */
@media (max-width: 1200px) {
  .content-container {
    flex-direction: column;
  }

  .sidebar-right {
    width: 100%;
  }

  .author-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .author-card {
    border-bottom: none;
  }
}

@media (max-width: 768px) {
  .author-list {
    grid-template-columns: 1fr;
  }
}

.cover-image {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 15px;
}
</style>