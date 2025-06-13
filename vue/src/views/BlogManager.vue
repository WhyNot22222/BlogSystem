<template>
  <div v-if="hasPermission" class="blog-manager">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">
            <BookOpen class="header-icon" :size="28" />
            博客管理
          </h1>
          <p class="page-subtitle">管理您的所有博客文章</p>
        </div>
        <div class="header-right">
          <el-button type="primary" size="large" @click="goToEditor()" color="#e83929">
            <Plus :size="16" class="button-icon" />
            新建文章
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon published">
          <CheckCircle :size="24" color="white" />
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ publishedCount }}</div>
          <div class="stat-label">已发布</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon draft">
          <Edit :size="24" color="white" />
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ draftCount }}</div>
          <div class="stat-label">草稿</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total">
          <FileText :size="24" color="white" />
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ blogs.length }}</div>
          <div class="stat-label">总文章</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-bar">
        <div class="filter-left">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索文章标题..."
              prefix-icon="Search"
              clearable
              style="width: 300px"
              @input="handleSearch"
          />
          <el-select
              v-model="statusFilter"
              placeholder="筛选状态"
              clearable
              style="width: 150px; margin-left: 12px"
              @change="handleFilter"
          >
            <el-option label="全部" value="" />
            <el-option label="已发布" value="published" />
            <el-option label="草稿" value="draft" />
          </el-select>
        </div>
        <div class="filter-right">
          <el-button @click="fetchBlogs" :loading="loading">
            <RefreshCw :size="16" class="button-icon" />
            刷新
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 博客列表 -->
    <el-card class="table-card" shadow="never">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="filteredBlogs.length === 0" class="empty-state">
        <div class="empty-icon">
          <FileX :size="64" color="#d1d5db" />
        </div>
        <h3>暂无文章</h3>
        <p>{{ searchKeyword ? '没有找到匹配的文章' : '开始创建您的第一篇文章吧！' }}</p>
        <el-button type="primary" @click="goToEditor()" v-if="!searchKeyword">
          <Plus :size="16" class="button-icon" />
          新建文章
        </el-button>
      </div>

      <div v-else class="blog-grid">
        <div
            v-for="blog in filteredBlogs"
            :key="blog.id"
            class="blog-card"
            @click="showPreview(blog)"
        >
          <div class="blog-cover">
            <img :src="blog.coverUrl" :alt="blog.title" />
            <div class="blog-status" :class="blog.status">
              <CheckCircle v-if="blog.status === 'published'" :size="16" color="white" />
              <Edit v-else :size="16" color="white" />
              {{ formatStatus(blog) }}
            </div>
          </div>

          <div class="blog-content">
            <h3 class="blog-title">{{ blog.title }}</h3>
            <div class="blog-meta">
              <div class="meta-item">
                <Calendar :size="14" />
                {{ formatDate(blog.createdAt) }}
              </div>
              <div class="meta-item" v-if="blog.views">
                <Eye :size="14" />
                {{ blog.views }}
              </div>
              <div class="meta-item" v-if="blog.likes">
                <Heart :size="14" />
                {{ blog.likes }}
              </div>
            </div>

            <div class="blog-tags" v-if="blog.tags && blog.tags.length">
              <span v-for="tag in blog.tags.slice(0, 3)" :key="tag" class="tag">
                {{ tag }}
              </span>
            </div>
          </div>

          <div class="blog-actions" @click.stop>
            <el-button size="small" type="primary" @click="goToEditor(blog.id)">
              <Edit :size="14" />
              编辑
            </el-button>
            <el-button size="small" @click="showPreview(blog)">
              <Eye :size="14" />
              预览
            </el-button>
            <el-button size="small" type="danger" @click="deleteBlog(blog.id)">
              <Trash2 :size="14" />
              删除
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <BlogPreviewDialog
        :visible="previewVisible"
        :post="currentPost"
        @close="previewVisible = false"
    />
  </div>

  <div v-else class="permission-error">
    <el-result
        icon="error"
        title="权限不足"
        sub-title="您没有权限访问用户管理页面"
    >
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useStore } from 'vuex'
import BlogPreviewDialog from '@/components/BlogPreviewDialog.vue'
import * as postHelper from "@/utils/postHelper.js";
import * as userHelper from "@/utils/userHelper.js";
import defaultCover from '@/assets/imgs/default-cover.jpg'
import {
  BookOpen,
  Plus,
  CheckCircle,
  Edit,
  FileText,
  RefreshCw,
  FileX,
  Calendar,
  Eye,
  Heart,
  Trash2
} from 'lucide-vue-next'

const store = useStore()
const userId = computed(() => store.getters.userId)

const router = useRouter()
const blogs = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const hasPermission = ref(false)

// 统计数据
const publishedCount = computed(() =>
    blogs.value.filter(blog => blog.status === 'published').length
)
const draftCount = computed(() =>
    blogs.value.filter(blog => blog.status === 'draft').length
)

// 筛选后的博客列表
const filteredBlogs = computed(() => {
  let result = blogs.value

  // 按关键词搜索
  if (searchKeyword.value) {
    result = result.filter(blog =>
        blog.title.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }

  // 按状态筛选
  if (statusFilter.value) {
    result = result.filter(blog => blog.status === statusFilter.value)
  }

  return result
})

const fetchBlogs = async () => {
  loading.value = true
  try {
    const response = await request.get(`/posts/user-posts?userId=${userId.value}`)
    blogs.value = await Promise.all(response.data.map(async post => {
      return {
        ...post,
        coverUrl: await postHelper.fetchCover(post.userId, post.id) || defaultCover,
        author: {
          name: await userHelper.getUserName(post.userId),
          avatar: await userHelper.fetchUserAvatar(post.userId)
        }
      }
    }));
    console.log('获取博客列表：', response.data)
  } catch (error) {
    ElMessage.error('获取博客列表失败')
  } finally {
    loading.value = false
  }
}

const deleteBlog = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这篇文章吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await request.delete(`/posts/${id}`)
    ElMessage.success('文章删除成功')
    fetchBlogs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除文章失败')
    }
  }
}

const goToEditor = (postId) => {
  const routeData = router.resolve({
    path: '/blog-editor',
    query: postId ? { postId } : {}
  });
  window.open(routeData.href, '_blank');
}

const formatStatus = (row) => {
  return row.status === 'published' ? '已发布' : '草稿'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

const handleFilter = () => {
  // 筛选逻辑已在计算属性中处理
}

onMounted(async () => {
  hasPermission.value = true
  await fetchBlogs();
})

const previewVisible = ref(false)
const currentPost = ref(null)

const showPreview = (post) => {
  currentPost.value = {
    id: post.id,
    title: post.title,
    content: post.content,
    coverUrl: post.coverUrl,
    formattedContent: post.formattedContent,
    author: {
      name: post.author.name || '未知作者',
    },
    createdAt: post.createdAt,
    views: post.views,
    likes: post.likes,
    tags: post.tags || [],
    category: post.category
  }
  previewVisible.value = true
}
</script>

<style scoped>
.blog-manager {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.permission-error {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.page-title .header-icon {
  color: #3b82f6;
}

.page-subtitle {
  color: #6b7280;
  margin: 0;
  font-size: 16px;
}

.header-right {
  display: flex;
  gap: 12px;
}

/* 按钮中的图标样式 */
.button-icon {
  margin-right: 6px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.published {
  background: linear-gradient(135deg, #10b981, #059669);
}

.stat-icon.draft {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.stat-icon.total {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1;
}

.stat-label {
  color: #6b7280;
  font-size: 14px;
  margin-top: 4px;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 24px;
  border: none;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-left {
  display: flex;
  align-items: center;
}

.filter-right {
  display: flex;
  gap: 12px;
}

/* 表格卡片 */
.table-card {
  border: none;
}

.loading-container {
  padding: 24px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 24px;
  color: #6b7280;
}

.empty-icon {
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-state p {
  margin: 0 0 24px 0;
}

/* 博客网格 */
.blog-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  padding: 24px 0;
}

.blog-card {
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.blog-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.blog-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.blog-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.blog-card:hover .blog-cover img {
  transform: scale(1.05);
}

.blog-status {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  backdrop-filter: blur(10px);
}

.blog-status.published {
  background: rgba(16, 185, 129, 0.9);
  color: white;
}

.blog-status.draft {
  background: rgba(245, 158, 11, 0.9);
  color: white;
}

.blog-content {
  padding: 20px 20px 10px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.blog-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.blog-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 0;
  margin-top: auto;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
}

.blog-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.tag {
  padding: 4px 8px;
  background: #f3f4f6;
  color: #374151;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.blog-actions {
  padding: 16px 20px;
  border-top: 1px solid #f3f4f6;
  display: flex;
  gap: 8px;
  background: #fafafa;
  margin-top: auto;
}

.blog-actions .el-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .blog-manager {
    padding: 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .filter-bar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .filter-left {
    flex-direction: column;
    gap: 12px;
  }

  .blog-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>