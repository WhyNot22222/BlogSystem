<template>
  <div class="collections-container">
    <div class="collections-header">
      <div class="header-main">
        <h2 class="page-title">我的收藏</h2>
        <button class="create-btn" @click="showCreateCollectionDialog" style="position: relative; top: 12px;">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="action-icon">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          新建收藏夹
        </button>
      </div>
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
        <div class="collection-header">
          <div class="collection-info" @click="toggleCollection(collection.id)">
            <h3 class="collection-title">{{ collection.name }}</h3>
            <div class="collection-meta">
              <span class="update-time">最近更新 {{ collection.lastUpdate }}</span>
              <span class="separator">·</span>
              <span class="blog-count">{{ collection.blogs.length }} 内容</span>
              <span class="separator">·</span>
            </div>
          </div>
          <div class="collection-actions">
            <div class="dropdown" :class="{ 'dropdown-open': activeDropdown === collection.id }">
              <button class="more-btn" @click.stop="toggleDropdown(collection.id)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="more-icon">
                  <circle cx="12" cy="12" r="1"></circle>
                  <circle cx="19" cy="12" r="1"></circle>
                  <circle cx="5" cy="12" r="1"></circle>
                </svg>
              </button>
              <div class="dropdown-menu" v-if="activeDropdown === collection.id" @click.stop>
                <button class="dropdown-item" @click="showRenameDialog(collection)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="dropdown-icon">
                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                  </svg>
                  重命名
                </button>
                <button class="dropdown-item delete" @click="confirmDeleteCollection(collection.id)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="dropdown-icon">
                    <polyline points="3,6 5,6 21,6"></polyline>
                    <path d="m19,6v14a2,2 0 0,1 -2,2H7a2,2 0 0,1 -2,-2V6m3,0V4a2,2 0 0,1 2,-2h4a2,2 0 0,1 2,2v2"></path>
                  </svg>
                  删除收藏夹
                </button>
              </div>
            </div>
            <button
                class="expand-btn"
                :class="{ 'expanded': collection.expanded }"
                @click.stop="toggleCollection(collection.id)"
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
                  <span class="blog-author">{{ blog.author?.username }}</span>
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
      <button class="create-collection-btn" @click="showCreateCollectionDialog">
        创建收藏夹
      </button>
    </div>

    <!-- 创建收藏夹对话框 -->
    <el-dialog
        v-model="createDialogVisible"
        title="📁 创建收藏夹"
        width="450px"
        :show-close="true"
        :close-on-click-modal="false"
        class="create-dialog modern-dialog"
    >
      <el-form :model="newCollection" label-position="top" class="create-form">
        <el-form-item label="收藏夹名称" required>
          <el-input
              v-model="newCollection.name"
              placeholder="请输入收藏夹名称"
              maxlength="20"
              show-word-limit
              class="form-input"
          />
        </el-form-item>
        <el-form-item label="隐私设置">
          <el-radio-group v-model="newCollection.isPrivate" class="privacy-group">
            <el-radio :label="false" class="privacy-option">
              <div class="privacy-content">
                <span class="privacy-icon">🌐</span>
                <div class="privacy-text">
                  <div class="privacy-title">公开</div>
                  <div class="privacy-desc">所有人可见</div>
                </div>
              </div>
            </el-radio>
            <el-radio :label="true" class="privacy-option">
              <div class="privacy-content">
                <span class="privacy-icon">🔒</span>
                <div class="privacy-text">
                  <div class="privacy-title">私密</div>
                  <div class="privacy-desc">仅自己可见</div>
                </div>
              </div>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述（可选）">
          <el-input
              v-model="newCollection.description"
              type="textarea"
              :rows="3"
              placeholder="为这个收藏夹添加描述..."
              maxlength="100"
              show-word-limit
              class="form-textarea"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="createDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button
              type="primary"
              @click="createNewCollection"
              :loading="createLoading"
              :disabled="!newCollection.name.trim()"
              class="create-btn"
          >
            创建收藏夹
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 重命名收藏夹对话框 -->
    <el-dialog
        v-model="renameDialogVisible"
        title="✏️ 重命名收藏夹"
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
        class="rename-dialog modern-dialog"
    >
      <el-form :model="editingCollection" label-position="top" class="rename-form">
        <el-form-item label="收藏夹名称" required>
          <el-input
              v-model="editingCollection.name"
              placeholder="请输入收藏夹名称"
              maxlength="20"
              show-word-limit
              class="form-input"
              ref="renameInput"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="renameDialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button
              type="primary"
              @click="renameCollection"
              :loading="renameLoading"
              :disabled="!editingCollection.name.trim()"
              class="save-btn"
          >
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { useStore } from 'vuex';
import { formatDate } from '@/utils/dateUtils'
import { ElMessageBox, ElMessage } from 'element-plus'

const store = useStore();
const userId = computed(() => store.getters.userId)

const router = useRouter()
const loading = ref(false)
const createLoading = ref(false)
const renameLoading = ref(false)

const collections = ref([])
const activeDropdown = ref(null)
const createDialogVisible = ref(false)
const renameDialogVisible = ref(false)
const newCollection = ref({
  name: '',
  isPrivate: false,
  description: ''
})
const editingCollection = ref({ id: null, name: '' })
const renameInput = ref(null)

// 点击外部关闭下拉菜单
const handleClickOutside = (event) => {
  if (activeDropdown.value !== null) {
    activeDropdown.value = null
  }
}

onMounted(() => {
  fetchCollections()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})

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

const toggleDropdown = (collectionId) => {
  if (activeDropdown.value === collectionId) {
    activeDropdown.value = null
  } else {
    activeDropdown.value = collectionId
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

// 显示创建收藏夹对话框
const showCreateCollectionDialog = () => {
  newCollection.value = {
    name: '',
    isPrivate: false,
    description: ''
  }
  createDialogVisible.value = true
}

// 创建新收藏夹
const createNewCollection = async () => {
  if (!newCollection.value.name.trim()) {
    ElMessage.warning('请输入收藏夹名称')
    return
  }

  createLoading.value = true

  try {
    const res = await request.post('/favorites/collection', null, {
      params: {
        name: newCollection.value.name,
        description: newCollection.value.description,
        isPublic: !newCollection.value.isPrivate,
        userId: userId.value
      }
    })

    if (res.code === '200') {
      ElMessage.success('创建收藏夹成功')
      createDialogVisible.value = false

      // 添加新创建的收藏夹到列表
      collections.value.unshift({
        id: res.data,
        name: newCollection.value.name,
        description: newCollection.value.description,
        lastUpdate: new Date().toISOString().split('T')[0],
        expanded: false,
        blogs: []
      })

      // 重置表单
      newCollection.value = {
        name: '',
        isPrivate: false,
        description: ''
      }
    } else {
      ElMessage.error(res.message || '创建收藏夹失败')
    }
  } catch (error) {
    console.log('创建收藏夹失败:', error)
    ElMessage.error('创建收藏夹失败')
  } finally {
    createLoading.value = false
  }
}

// 显示重命名对话框
const showRenameDialog = (collection) => {
  editingCollection.value = {
    id: collection.id,
    name: collection.name
  }
  renameDialogVisible.value = true
  activeDropdown.value = null

  // 自动聚焦到输入框
  nextTick(() => {
    if (renameInput.value) {
      renameInput.value.focus()
      renameInput.value.select()
    }
  })
}

// 重命名收藏夹
const renameCollection = async () => {
  if (!editingCollection.value.name.trim()) {
    ElMessage.warning('请输入收藏夹名称')
    return
  }

  renameLoading.value = true

  try {
    const res = await request.put('/favorites/collection/rename', null, {
      params: {
        id: editingCollection.value.id,
        name: editingCollection.value.name,
      }
    })

    if (res.code === '200') {
      ElMessage.success('重命名收藏夹成功')
      renameDialogVisible.value = false

      // 更新收藏夹名称
      const collection = collections.value.find(c => c.id === editingCollection.value.id)
      if (collection) {
        collection.name = editingCollection.value.name
      }
    } else {
      ElMessage.error(res.message || '重命名收藏夹失败')
    }
  } catch (error) {
    console.log('重命名收藏夹失败:', error)
    ElMessage.error('重命名收藏夹失败')
  } finally {
    renameLoading.value = false
  }
}

// 确认删除收藏夹
const confirmDeleteCollection = async (collectionId) => {
  try {
    await ElMessageBox.confirm('确定要删除该收藏夹吗？删除后将无法恢复。', '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteCollection(collectionId)
  } catch (error) {
    return
  }
}

// 删除收藏夹
const deleteCollection = async (collectionId) => {
  try {
    const res = await request.delete('/favorites/collection/delete', {
      params: {
        collectionId,
        userId: userId.value
      }
    })

    if (res.code === '200') {
      ElMessage.success('删除收藏夹成功')
      // 从列表中移除该收藏夹
      const index = collections.value.findIndex(c => c.id === collectionId)
      if (index > -1) {
        collections.value.splice(index, 1)
      }
    } else {
      ElMessage.error(res.message || '删除收藏夹失败')
    }
  } catch (error) {
    console.log('删除收藏夹失败:', error)
    ElMessage.error('删除收藏夹失败')
  }
}

// 关闭对话框
const handleDialogClose = () => {
  createDialogVisible.value = false
  renameDialogVisible.value = false
}
</script>

<style scoped>

/* 头部布局 */
.collections-header {
  margin-bottom: 24px;
}

.header-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.create-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.action-icon {
  width: 16px;
  height: 16px;
}

.collections-stats {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 14px;
}

/* 收藏夹列表 */
.collections-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.collection-item {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.collection-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.collection-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.collection-header:hover {
  background-color: #fafafa;;
  border-radius: 16px;
}

.collection-info {
  flex: 1;
  cursor: pointer;
}

.collection-title {
  margin: 0 0 6px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.collection-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 13px;
}

.separator {
  color: #ccc;
}

.collection-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 下拉菜单 */
.dropdown {
  position: relative;
  display: inline-block;
  z-index: 100;
}

.more-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.more-btn:hover {
  background-color: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.more-icon {
  width: 18px;
  height: 18px;
  color: #666;
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  background-color: white;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  border-radius: 12px;
  min-width: 160px;
  z-index: 1000;
  border: 1px solid #e8e8e8;
  overflow: hidden;
  animation: fadeInDown 0.3s ease;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  width: 100%;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: all 0.2s;
}

.dropdown-item:hover {
  background-color: #f8f9fa;
}

.dropdown-item.delete {
  color: #f56c6c;
}

.dropdown-item.delete:hover {
  background-color: #fef0f0;
}

.dropdown-icon {
  width: 16px;
  height: 16px;
}

.expand-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: none;
  border: 2px solid #e9ecef;
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  color: #666;
  transition: all 0.3s;
  font-weight: 500;
}

.expand-btn:hover {
  border-color: #667eea;
  color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.expand-icon {
  width: 14px;
  height: 14px;
  transition: transform 0.3s;
}

.expand-btn.expanded .expand-icon {
  transform: rotate(180deg);
}

/* 收藏夹内容 */
.collection-content {
  padding: 0 24px 24px;
  background: #fafafa;
  border-radius: 0 0 25px 25px;
}

.blogs-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 16px;
}

.blog-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.blog-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.blog-tag {
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.blog-info {
  flex: 1;
  min-width: 0;
}

.blog-title {
  margin: 0 0 6px 0;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.blog-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
  font-size: 12px;
}

.blog-actions {
  display: flex;
  align-items: center;
}

.remove-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  color: #999;
  transition: all 0.2s;
}

.remove-btn:hover {
  color: #f56c6c;
  background: rgba(245, 108, 108, 0.1);
}

.remove-btn svg {
  width: 16px;
  height: 16px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.empty-state p {
  margin: 0 0 32px 0;
  color: #666;
  font-size: 15px;
  line-height: 1.6;
}

.create-collection-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.create-collection-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 对话框样式 */
.modern-dialog :deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.modern-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 24px 28px;
  margin: 0;
}

.modern-dialog :deep(.el-dialog__title) {
  font-weight: 600;
  font-size: 18px;
}

.modern-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.modern-dialog :deep(.el-dialog__close) {
  color: white;
  font-size: 20px;
}

.modern-dialog :deep(.el-dialog__close):hover {
  color: rgba(255, 255, 255, 0.8);
}

/* 表单样式 */
.create-form,
.rename-form {
  padding: 28px;
}

.form-input :deep(.el-input__wrapper),
.form-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #e9ecef;
  padding: 12px 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-input :deep(.el-input__wrapper):hover,
.form-textarea :deep(.el-textarea__inner):hover {
  border-color: #667eea;
}

.form-input :deep(.el-input__wrapper.is-focus),
.form-textarea :deep(.el-textarea__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 隐私设置样式 */
.privacy-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.privacy-option {
  background: #f8f9fa;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  padding: 16px;
  transition: all 0.3s ease;
  margin: 0;
  cursor: pointer;
}

.privacy-option:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.privacy-option :deep(.el-radio__input.is-checked) + .el-radio__label {
  color: #667eea;
}

.privacy-option :deep(.el-radio__input.is-checked .el-radio__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

.privacy-content {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.privacy-icon {
  font-size: 20px;
}

.privacy-text {
  display: flex;
  flex-direction: row;
  gap: 4px;
}

.privacy-title {
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
}

.privacy-desc {
  font-size: 13px;
  color: #6c757d;
  margin-left: 10px;
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 28px 28px;
  background: #f8f9fa;
}

.cancel-btn {
  border-radius: 20px;
  padding: 10px 24px;
  border: 2px solid #e9ecef;
  background: white;
  color: #6c757d;
  transition: all 0.3s ease;
  font-weight: 500;
}

.cancel-btn:hover {
  border-color: #6c757d;
  color: #495057;
  transform: translateY(-1px);
}

.create-btn,
.save-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-weight: 500;
  transition: all 0.3s ease;
  color: white;
}

.create-btn:hover,
.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.create-btn:disabled,
.save-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .collections-container {
    padding: 16px;
  }

  .header-main {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .page-title {
    text-align: center;
    font-size: 20px;
  }

  .collection-header {
    padding: 16px 20px;
  }

  .collection-content {
    padding: 0 20px 20px;
  }

  .dropdown-menu {
    right: -10px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 95% !important;
    margin: 5vh auto;
  }

  .create-form,
  .rename-form {
    padding: 20px;
  }

  .dialog-footer {
    padding: 16px 20px 20px;
  }
}

@media (max-width: 480px) {
  .blog-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .blog-actions {
    align-self: flex-end;
  }

  .privacy-group {
    gap: 8px;
  }

  .privacy-option {
    padding: 12px;
  }
}
</style>