<template>
  <div class="collections-container">
    <div class="collections-header">
      <h2 class="page-title">我的收藏</h2>
      <div class="collections-stats">
        <span>共 {{ collections.length }} 个收藏夹</span>
        <span>{{ totalBlogs }} 篇博客</span>
      </div>
    </div>

    <div class="collections-list">
      <div
          v-for="collection in collections"
          :key="collection.id"
          class="collection-item"
      >
        <!-- 收藏夹头部 -->
        <div class="collection-header" @click="toggleCollection(collection.id)">
          <div class="collection-info">
            <h3 class="collection-title">{{ collection.name }}</h3>
            <div class="collection-meta">
              <span class="update-time">最近更新 {{ collection.lastUpdate }}</span>
              <span class="separator">·</span>
              <span class="blog-count">{{ collection.blogs.length }} 内容</span>
              <span class="separator">·</span>
            </div>
          </div>
          <div class="collection-actions">
            <button
                class="expand-btn"
                :class="{ 'expanded': collection.expanded }"
            >
              <svg
                  class="expand-icon"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
              >
                <polyline points="6,9 12,15 18,9"></polyline>
              </svg>
              {{ collection.expanded ? '收起' : '展开' }}
            </button>
          </div>
        </div>

        <!-- 收藏夹内容 -->
        <div
            v-if="collection.expanded"
            class="collection-content"
        >
          <div class="blogs-list">
            <div
                v-for="blog in collection.blogs"
                :key="blog.id"
                class="blog-item"
                @click="navigateToBlog(blog.id)"
            >
              <div class="blog-tag">博客</div>
              <div class="blog-info">
                <h4 class="blog-title">{{ blog.title }}</h4>
                <div class="blog-meta">
                  <span class="blog-author">{{ blog.author.username }}</span>
                  <span class="separator">·</span>
                  <span class="blog-date">{{ formatDate(blog.publishedAt) }}</span>
                  <span class="separator">·</span>
                  <span class="blog-views">{{ blog.views }} 阅读</span>
                </div>
              </div>
              <div class="blog-actions">
                <button
                    class="remove-btn"
                    @click.stop="removeBlogFromCollection(collection.id, blog.id)"
                >
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3,6 5,6 21,6"></polyline>
                    <path d="m19,6v14a2,2 0 0,1 -2,2H7a2,2 0 0,1 -2,-2V6m3,0V4a2,2 0 0,1 2,-2h4a2,2 0 0,1 2,2v2"></path>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="collections.length === 0" class="empty-state">
      <div class="empty-icon">📚</div>
      <h3>还没有收藏夹</h3>
      <p>开始收藏您感兴趣的博客吧</p>
      <button class="create-collection-btn" @click="createCollection">
        创建收藏夹
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { useStore } from 'vuex';
import { formatDate } from '@/utils/dateUtils'
import { ElMessageBox, ElMessage } from 'element-plus'

const store = useStore();
const userId = computed(() => store.getters.userId)

const router = useRouter()
const loading = ref(false)

const collections = ref([])

// 获取收藏夹列表的方法
const fetchCollections = async () => {
  try {
    loading.value = true
    const collectionsRes = await request.get('/favorites/collections/list', {
      params: { userId: userId.value }
    })

    if (collectionsRes.code === '200') {
      collections.value = await Promise.all(
        collectionsRes.data.map(async c => {
          // 先获取收藏夹中的博客数据
          const itemsRes = await request.get('/favorites/collection/posts', {
            params: { collectionId: c.id }
          })
          
          if (itemsRes.code !== '200') return { ...c, expanded: false, blogs: [] }

          // 为每个博客获取用户信息
          const blogsWithUser = await Promise.all(
            itemsRes.data.map(async blog => {
              const userRes = await request.post('/user/getUser', null, {
                params: { userId: blog.userId }
              })
              return {
                ...blog,
                author: userRes.code === '200' ? userRes.data : null
              }
            })
          )

          return {
            ...c,
            expanded: false,
            blogs: blogsWithUser
          }
        })
      )
    }
  } finally {
    loading.value = false
  }
}

// 获取单个收藏夹内容的方法
const fetchCollectionItems = async (collectionId) => {
  try {
    const res = await request.get('/favorites/collection/posts', {
      params: { collectionId }
    })
    if (res.code === '200') {
      const collection = collections.value.find(c => c.id === collectionId)
      if (collection) {
        collection.blogs = res.data
      }
    }
  } catch (error) {
    console.log('获取收藏内容失败:', error)
  }
}

// 计算属性
const totalBlogs = computed(() => {
  return collections.value.reduce((total, collection) => {
    return total + collection.blogs.length
  }, 0)
})

// 方法
const toggleCollection = async (collectionId) => {
  const collection = collections.value.find(c => c.id === collectionId)
  if (collection) {
    // 当展开且未加载过内容时请求数据
    if (!collection.expanded && collection.blogs.length === 0) {
      await fetchCollectionItems(collectionId)
    }
    collection.expanded = !collection.expanded
  }
}

const navigateToBlog = (blogId) => {
  // 跳转到博客详情页
  const routeData = router.resolve({
    path: '/blog-detail',
    query: {
      postId: blogId,
    }
  });
  window.open(routeData.href, '_blank');
}

const removeBlogFromCollection = async (collectionId, blogId) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该博客吗？', '操作确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.delete('/favorites/remove', {
      params: {
        postId: blogId,
        userId: userId.value
      }
    })
    if (res.code === '200') {
      const collection = collections.value.find(c => c.id === collectionId)
      const index = collection.blogs.findIndex(b => b.id === blogId)
      if (index > -1) {
        collection.blogs.splice(index, 1)
        ElMessage.success('已取消收藏')
      }
    }
  } catch (error) {
    // 捕获用户取消操作的情况
    if (error !== 'cancel') {
      console.error('移除收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

const createCollection = () => {
  // 创建新收藏夹的逻辑
  console.log('创建新收藏夹')
}

// 生命周期
onMounted(() => {
  fetchCollections()
})
</script>

<style scoped src="../assets/css/Collection.css">
</style>