<template>
  <div class="follow-container">
    <!-- 左侧：关注的人发布的博客列表 -->
    <div class="blog-feed">
      <div class="feed-header">
        <h2 class="section-title">关注动态</h2>
      </div>

      <div class="blog-list">
        <div v-for="blog in followedBlogs" :key="blog.id" class="blog-card">
          <div class="blog-header">
            <div class="author-info">
              <el-avatar :size="46" :src="blog.authorAvatar" class="author-avatar"></el-avatar>
              <div class="author-details">
                <div class="author-name">{{ blog.authorName }}</div>
                <div class="post-meta">
                  <span class="post-time">{{ blog.postTime }}</span>
                  <span class="post-category" v-if="blog.category">{{ blog.category }}</span>
                </div>
              </div>
            </div>
            <div class="follow-status" v-if="blog.isNew">
              <span class="new-post-badge">新内容</span>
            </div>
          </div>

          <div class="blog-content" @click="viewBlogDetail(blog.id)">
            <h3 class="blog-title">{{ blog.title }}</h3>
            <p class="blog-summary">{{ blog.summary }}</p>

            <div v-if="blog.coverImage" class="blog-image" :class="{'wide-image': blog.wideImage}">
              <img :src="blog.coverImage" :alt="blog.title" />
            </div>
          </div>

          <div class="blog-footer">
            <div class="blog-tags" v-if="blog.tags && blog.tags.length">
              <span v-for="tag in blog.tags" :key="tag" class="blog-tag"># {{ tag }}</span>
            </div>

            <div class="blog-actions">
              <div class="action-button" :class="{'active': blog.isLiked}" @click="toggleLike(blog)">
                <el-icon><ThumbsUp /></el-icon>
                <span>{{ blog.likes }}</span>
              </div>
              <div class="action-button">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ blog.comments }}</span>
              </div>
              <div class="action-button">
                <el-icon><Share /></el-icon>
                <span>分享</span>
              </div>
              <div class="action-button">
                <el-icon><Star /></el-icon>
                <span>收藏</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="followedBlogs.length === 0" class="empty-state">
          <el-empty description="暂无关注动态">
            <template #image>
              <img src="https://cdn.jsdelivr.net/gh/lucide-icons/lucide/icons/rss.svg" class="empty-icon" />
            </template>
            <el-button type="primary" class="discover-btn">发现更多博主</el-button>
          </el-empty>
        </div>

        <div class="load-more" v-if="followedBlogs.length > 0">
          <el-button plain>加载更多</el-button>
        </div>
      </div>
    </div>

    <!-- 右侧：关注列表 -->
    <div class="following-list">
      <div class="following-header">
        <h2 class="section-title">我的关注 <span class="count">({{ followingUsers.length }})</span></h2>
        <el-button type="primary" size="small" class="manage-btn">管理</el-button>
      </div>

      <div class="search-following">
        <el-input
            v-model="searchQuery"
            placeholder="搜索关注的人"
            clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="users-list">
        <div v-for="user in filteredFollowingUsers" :key="user.id" class="user-item" @click="viewUserProfile(user.id)">
          <div class="user-info">
            <el-avatar :size="50" :src="user.avatar" class="user-avatar"></el-avatar>
            <div class="user-details">
              <div class="user-name-row">
                <span class="user-name">{{ user.name }}</span>
                <span v-if="user.isVerified" class="verified-badge">
                  <el-icon><Check /></el-icon>
                </span>
              </div>
              <div class="user-bio">{{ user.bio }}</div>
            </div>
          </div>
          <div class="follow-button-container">
            <el-button
                size="small"
                :type="user.isFollowing ? 'default' : 'primary'"
                :class="{ 'following-btn': user.isFollowing }"
                @click.stop="toggleFollow(user)"
            >
              <el-icon v-if="user.isFollowing"><Check /></el-icon>
              <span>{{ user.isFollowing ? '已关注' : '关注' }}</span>
            </el-button>
          </div>
        </div>

        <div v-if="filteredFollowingUsers.length === 0" class="empty-state">
          <el-empty description="未找到匹配的用户" />
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  ChatDotRound,
  Share,
  Search,
  Star,
  Check
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 搜索关注用户
const searchQuery = ref('')

// 模拟数据 - 关注的博客
const followedBlogs = ref([
  {
    id: 1,
    authorName: '你一身傲骨怎能输',
    authorAvatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    postTime: '1分钟前',
    title: '正则语言的超能力与短板揭秘',
    summary: '正则语言是一种简单且直接的字符串筛选规则，具有闭包性、有限记忆、不能处理复杂配对...',
    coverImage: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    likes: 12,
    comments: 3,
    isNew: true,
    isLiked: false,
    category: '技术',
    tags: ['正则表达式', '编程语言']
  },
  {
    id: 2,
    authorName: '独泪了无痕',
    authorAvatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    postTime: '25分钟前',
    title: '带你搞懂@Valid和@Validated的区别',
    summary: '本文介绍了Spring Boot中用于参数校验的两个核心注解@Valid和@Validated的用法及区别...',
    coverImage: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    likes: 24,
    comments: 8,
    isNew: false,
    isLiked: true,
    category: '后端',
    wideImage: true,
    tags: ['Spring Boot', '参数校验']
  },
  {
    id: 3,
    authorName: 'fei_sun',
    authorAvatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    postTime: '1小时前',
    title: '【计网】作业5',
    summary: '最小的212.56.1000 0100.0。可分配2^9 -2=510-2=508。根据子网掩码，比较网络部分是否...',
    coverImage: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    likes: 18,
    comments: 5,
    isNew: false,
    isLiked: false,
    category: '学习笔记',
    tags: ['计算机网络', '子网掩码']
  },
  {
    id: 4,
    authorName: 'FBI HackerHarry浩',
    authorAvatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    postTime: '6小时前',
    title: 'Linux云计算训练营笔记day11（Linux CentOS7）',
    summary: 'Linux云计算是一种通过互联网按需提供计算资源的服务模式，广泛应用于服务器管理和项目...',
    coverImage: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    likes: 32,
    comments: 7,
    isNew: false,
    isLiked: true,
    category: '运维',
    tags: ['Linux', '云计算', 'CentOS7']
  }
])

// 模拟数据 - 关注的用户
const followingUsers = ref([
  {
    id: 1,
    name: '你一身傲骨怎能输',
    avatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    bio: '技术博主 | 正则表达式专家',
    isFollowing: true,
    isVerified: true
  },
  {
    id: 2,
    name: '独泪了无痕',
    avatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    bio: 'Spring Boot 开发者 | 技术分享',
    isFollowing: true,
    isVerified: true
  },
  {
    id: 3,
    name: 'fei_sun',
    avatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    bio: '计算机网络 | 学习笔记分享',
    isFollowing: true
  },
  {
    id: 4,
    name: 'FBI HackerHarry浩',
    avatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    bio: 'Linux云计算 | 运维工程师',
    isFollowing: true,
    isVerified: true
  },
  {
    id: 5,
    name: '技术小达人',
    avatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    bio: '前端开发 | UI设计 | 用户体验',
    isFollowing: true
  }
])

// 推荐关注用户
const recommendedUsers = ref([
  {
    id: 101,
    name: '前端开发日记',
    avatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    bio: 'Vue & React 开发者',
    followers: '2.3k',
    isFollowing: false
  },
  {
    id: 102,
    name: '全栈工程师',
    avatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    bio: '分享全栈开发技巧',
    followers: '5.1k',
    isFollowing: false
  },
  {
    id: 103,
    name: 'AI研究员',
    avatar: 'https://hebbkx1anhila5yf.public.blob.vercel-storage.com/image-vLagbjvmjN3BCyqNQ8Ih1BNMAcujss.png',
    bio: '人工智能与机器学习',
    followers: '3.7k',
    isFollowing: false
  }
])

// 过滤关注用户列表
const filteredFollowingUsers = computed(() => {
  if (!searchQuery.value) return followingUsers.value
  return followingUsers.value.filter(user =>
      user.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.bio.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// 切换关注状态
const toggleFollow = (user) => {
  user.isFollowing = !user.isFollowing
  if (user.isFollowing) {
    ElMessage({
      message: `已关注 ${user.name}`,
      type: 'success',
      offset: 70
    })
  } else {
    ElMessage({
      message: `已取消关注 ${user.name}`,
      type: 'info',
      offset: 70
    })
  }
}

// 切换点赞状态
const toggleLike = (blog) => {
  blog.isLiked = !blog.isLiked
  if (blog.isLiked) {
    blog.likes++
    ElMessage({
      message: '点赞成功',
      type: 'success',
      offset: 70
    })
  } else {
    blog.likes--
    ElMessage({
      message: '已取消点赞',
      type: 'info',
      offset: 70
    })
  }
}

// 查看博客详情
const viewBlogDetail = (blogId) => {
  console.log('查看博客详情:', blogId)
  // 实际应用中这里会跳转到博客详情页
  ElMessage({
    message: `正在查看博客 ID: ${blogId}`,
    type: 'info',
    offset: 70
  })
}

// 查看用户资料
const viewUserProfile = (userId) => {
  console.log('查看用户资料:', userId)
  // 实际应用中这里会跳转到用户资料页
  ElMessage({
    message: `正在查看用户 ID: ${userId}`,
    type: 'info',
    offset: 70
  })
}

// 模拟获取数据
const fetchFollowedBlogs = () => {
  // 实际应用中这里会调用API获取数据
  console.log('获取关注的博客数据')
}

const fetchFollowingUsers = () => {
  // 实际应用中这里会调用API获取数据
  console.log('获取关注的用户数据')
}

onMounted(() => {
  fetchFollowedBlogs()
  fetchFollowingUsers()
})
</script>

<style scoped>
.follow-container {
  display: flex;
  gap: 24px;
  height: 100%;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

/* 左侧博客列表样式 */
.blog-feed {
  flex: 1;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  height: calc(100vh - 100px);
  overflow-y: auto;
}

.feed-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background-color: #fff;
  z-index: 10;
  border-radius: 12px 12px 0 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  margin: 0;
}

.count {
  font-size: 14px;
  color: #999;
  margin-left: 5px;
  font-weight: normal;
}

.blog-list {
  padding: 0 24px 24px;
}

.blog-card {
  padding: 24px 0;
  border-bottom: 1px solid #f0f0f0;
}

.blog-card:last-child {
  border-bottom: none;
}

.blog-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  font-size: 15px;
  color: #333;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}

.post-time {
  font-size: 13px;
  color: #999;
}

.post-category {
  font-size: 12px;
  color: #e83929;
  background-color: #fff2f0;
  padding: 2px 8px;
  border-radius: 10px;
}

.new-post-badge {
  font-size: 12px;
  color: #fff;
  background-color: #e83929;
  padding: 2px 8px;
  border-radius: 10px;
}

.blog-content {
  margin-bottom: 16px;
  cursor: pointer;
}

.blog-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
  line-height: 1.4;
}

.blog-summary {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
  line-height: 1.6;
}

.blog-image {
  width: 100%;
  margin-top: 12px;
  border-radius: 10px;
  overflow: hidden;
  max-height: 240px;
}

.blog-image.wide-image {
  max-height: 300px;
}

.blog-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.blog-content:hover .blog-image img {
  transform: scale(1.02);
}

.blog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.blog-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.blog-tag {
  font-size: 13px;
  color: #666;
  background-color: #f5f5f5;
  padding: 4px 10px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.blog-tag:hover {
  background-color: #eee;
  color: #333;
}

.blog-actions {
  display: flex;
  gap: 20px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 16px;
  transition: all 0.2s;
}

.action-button:hover {
  color: #e83929;
  background-color: #fff2f0;
}

.action-button.active {
  color: #e83929;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 右侧关注列表样式 */
.following-list {
  width: 320px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  height: calc(100vh - 100px);
  overflow-y: auto;
}

.following-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background-color: #fff;
  z-index: 10;
  border-radius: 12px 12px 0 0;
}

.manage-btn {
  font-size: 13px;
  height: 32px;
  padding: 0 12px;
  background-color: #e83929;
  border: none;
}

.search-following {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}


.users-list {
  padding: 16px 24px;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s;
}

.user-item:hover {
  background-color: #f9f9f9;
}

.user-item:last-child {
  border-bottom: none;
}

.user-info {
  display: flex;
  gap: 12px;
  align-items: center;
  flex: 1;
  max-width: calc(100% - 80px);
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.user-details {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  width: 100%;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-name {
  font-weight: 600;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
}

.verified-badge {
  color: #fff;
  background-color: #1da1f2;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.verified-badge .el-icon {
  font-size: 10px;
}

.user-bio {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 2px;
}

/* 添加按钮容器修复对齐问题 */
.follow-button-container {
  width: 80px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  flex-shrink: 0;
}

.follow-button-container .el-button {
  width: 100%;
  padding: 0;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.following-btn {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.empty-state {
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  width: 60px;
  height: 60px;
  opacity: 0.5;
}

.discover-btn {
  margin-top: 16px;
  background-color: #e83929;
  border: none;
}


.recommended-user .user-info {
  flex: 1;
}


/* 确保两个区域独立滚动 */
.blog-feed, .following-list {
  overflow-y: auto;
  scrollbar-width: thin;
}

.blog-feed::-webkit-scrollbar, .following-list::-webkit-scrollbar {
  width: 6px;
}

.blog-feed::-webkit-scrollbar-thumb, .following-list::-webkit-scrollbar-thumb {
  background-color: #ddd;
  border-radius: 3px;
}

.blog-feed::-webkit-scrollbar-track, .following-list::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .follow-container {
    flex-direction: column;
  }

  .following-list {
    width: 100%;
    height: auto;
    max-height: 500px;
  }

  .blog-feed {
    height: auto;
    max-height: calc(100vh - 600px);
  }
}

@media (max-width: 768px) {
  .blog-actions {
    gap: 12px;
  }

  .action-button {
    font-size: 13px;
  }

  .blog-title {
    font-size: 16px;
  }

  .follow-button-container {
    width: 70px;
  }
}
</style>