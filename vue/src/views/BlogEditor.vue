<template>
  <div class="editor-container">
    <!-- 顶部导航栏 -->
    <header class="editor-header">
      <div class="header-left">
        <button class="back-button" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
        </button>
        <h1 class="editor-title">创建新博客</h1>
      </div>
      <div class="header-center">
        <el-tag v-if="postStatus === 'draft'" type="info" effect="plain" class="status-tag">草稿</el-tag>
        <el-tag v-else-if="postStatus === 'published'" type="success" effect="plain" class="status-tag">已发布</el-tag>
        <el-tag v-else-if="postStatus === 'archived'" type="warning" effect="plain" class="status-tag">已归档</el-tag>
        <span class="last-saved" v-if="lastSavedTime">
          上次保存: {{ formatTime(lastSavedTime) }}
        </span>
      </div>
      <div class="header-right">
        <el-dropdown trigger="click" @command="handleStatusChange">
          <el-button class="status-button">
            {{ getStatusText(postStatus) }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="draft">草稿</el-dropdown-item>
              <el-dropdown-item command="published">发布</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button class="draft-button" @click="saveDraft">
          <el-icon><Document /></el-icon>
          保存
        </el-button>
        <el-button type="primary" class="publish-button" @click="publishBlog">
          <el-icon><Upload /></el-icon>
          发布文章
        </el-button>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <div class="editor-content-wrapper">
      <!-- 左侧编辑区 -->
      <div class="editor-main">
        <!-- 标题输入 -->
        <div class="title-container">
          <el-input
              v-model="blogTitle"
              placeholder="请输入文章标题"
              class="title-input"
              maxlength="100"
              show-word-limit
              :show-word-limit-options="{
              background: false,
              separator: '/'
            }"
          />
        </div>

        <!-- 摘要输入 -->
        <div class="summary-container">
          <el-input
              v-model="blogSummary"
              type="textarea"
              placeholder="请输入文章摘要（可选，不超过200字）"
              :rows="2"
              maxlength="200"
              show-word-limit
              resize="none"
              class="summary-input"
          />
        </div>

        <!-- 内容编辑区 -->
        <div class="content-container">
          <el-input
              v-model="blogContent"
              type="textarea"
              placeholder="开始撰写您的博客内容..."
              :rows="18"
              resize="none"
              class="content-input"
          />
        </div>

        <!-- 底部工具栏 -->
        <div class="editor-toolbar">
          <div class="toolbar-left">
            <span class="word-count">{{ blogContent.length }} 字</span>
          </div>
          <div class="toolbar-right">
            <el-button size="small" type="primary" text @click="previewPost">
              <el-icon><View /></el-icon>
              预览
            </el-button>
          </div>
        </div>
      </div>

      <!-- 右侧设置区 -->
      <div class="editor-sidebar">
        <!-- 发布信息 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <el-icon><Calendar /></el-icon>
            发布信息
          </h3>
          <div class="info-item">
            <span class="info-label">创建时间</span>
            <span class="info-value">{{ formatDate(createdAt) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">最后更新</span>
            <span class="info-value">{{ formatDate(updatedAt) }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">发布时间</span>
            <div class="date-picker-wrapper">
              <el-date-picker
                  v-model="publishedAt"
                  type="datetime"
                  placeholder="选择发布时间"
                  format="YYYY-MM-DD HH:mm"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  :disabled="postStatus !== 'published'"
                  class="date-picker"
              />
            </div>
          </div>
          <div class="info-item">
            <span class="info-label">作者</span>
            <span class="info-value">{{ authorName }}</span>
          </div>
        </div>

        <!-- 文章分类 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <el-icon><Folder /></el-icon>
            文章分类
          </h3>
          <el-select
              v-model="category"
              placeholder="选择分类"
              class="category-select"
              popper-class="category-dropdown"
          >
            <el-option
                v-for="item in categories"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </div>

        <!-- 标签 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <el-icon><PriceTag /></el-icon>
            标签
          </h3>
          <div class="tags-container">
            <el-tag
                v-for="tag in dynamicTags"
                :key="tag"
                class="tag-item"
                closable
                :disable-transitions="false"
                @close="handleClose(tag)"
            >
              {{ tag }}
            </el-tag>
          </div>
          <div class="add-tag-container">
            <el-input
                v-if="inputVisible"
                ref="tagInput"
                v-model="inputValue"
                class="tag-input"
                size="small"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
            />
            <el-button
                v-else
                class="button-new-tag"
                size="small"
                @click="showInput"
            >
              + 新标签
            </el-button>
          </div>
          <div class="tag-hint" v-if="dynamicTags.length > 0">
            已添加 {{ dynamicTags.length }}/5 个标签
          </div>
        </div>

        <!-- 封面图片 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <el-icon><Picture /></el-icon>
            封面图片
          </h3>
          <el-upload
              class="cover-uploader"
              action="#"
              :http-request="uploadCover"
              :show-file-list="false"
              :before-upload="beforeCoverUpload"
          >
            <div v-if="coverUrl" class="cover-preview">
              <img :src="coverUrl" class="cover-image" alt="文章封面" />
              <div class="cover-actions">
                <el-button
                    type="danger"
                    circle
                    size="small"
                    @click.stop="removeCover"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <span>点击上传封面图片</span>
              <span class="upload-hint">建议尺寸 1200×675px，不超过2MB</span>
            </div>
          </el-upload>
        </div>

        <!-- 文章设置 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <el-icon><Setting /></el-icon>
            文章设置
          </h3>
          <div class="setting-item">
            <span class="setting-label">允许评论</span>
            <el-switch v-model="allowComments" />
          </div>
          <div class="setting-item">
            <span class="setting-label">公开可见</span>
            <el-switch v-model="isPublic" />
          </div>
          <div class="setting-item">
            <span class="setting-label">置顶文章</span>
            <el-switch v-model="isPinned" />
          </div>
        </div>
      </div>
    </div>

    <!-- 预览对话框 -->
    <el-dialog
        v-model="previewVisible"
        title="文章预览"
        width="70%"
        class="preview-dialog"
        destroy-on-close
    >
      <div class="preview-container">
        <h1 class="preview-title">{{ blogTitle || '无标题' }}</h1>
        <div class="preview-meta">
          <span class="preview-author">
            <el-icon><User /></el-icon>
            {{ authorName }}
          </span>
          <span class="preview-date">
            <el-icon><Calendar /></el-icon>
            {{ formatDate(publishedAt || new Date()) }}
          </span>
          <span class="preview-category" v-if="category">
            <el-icon><Folder /></el-icon>
            {{ getCategoryLabel(category) }}
          </span>
        </div>
        <div class="preview-tags" v-if="dynamicTags.length > 0">
          <el-tag v-for="tag in dynamicTags" :key="tag" size="small" class="preview-tag">{{ tag }}</el-tag>
        </div>
        <div class="preview-cover" v-if="coverUrl">
          <img :src="coverUrl" alt="文章封面" />
        </div>
        <div class="preview-summary" v-if="blogSummary">
          <p>{{ blogSummary }}</p>
        </div>
        <div class="preview-content">
          <p v-if="blogContent">{{ blogContent }}</p>
          <p v-else class="empty-content">暂无内容</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  ArrowDown,
  Plus,
  Picture,
  VideoCamera,
  Link,
  Document,
  Delete,
  View,
  Calendar,
  Folder,
  Setting,
  User,
  PriceTag,
  Upload
} from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const blogTitle = ref('')
const blogSummary = ref('')
const blogContent = ref('')
const category = ref('')
const coverUrl = ref('')
const allowComments = ref(true)
const isPublic = ref(true)
const isOriginal = ref(true)
const isPinned = ref(false)
const hasUnsavedChanges = ref(false)
const autoSaveInterval = ref(null)
const lastSavedTime = ref(null)
const previewVisible = ref(false)

// 文章状态
const postStatus = ref('draft') // draft, published, archived
const createdAt = ref(new Date())
const updatedAt = ref(new Date())
const publishedAt = ref(null)
const authorName = ref('当前用户') // 这里应该从用户系统获取

// 分类选项
const categories = [
  { value: 'info', label: '资讯' },
  { value: 'tech', label: '技术' },
  { value: 'operation', label: '运维' },
  { value: 'ai', label: '人工智能' },
  { value: 'java', label: 'Java' },
  { value: 'python', label: 'Python' },
  { value: 'frontend', label: '前端' },
  { value: 'backend', label: '后端' }
];

// 获取分类标签
const getCategoryLabel = (value) => {
  const category = categories.find(item => item.value === value)
  return category ? category.label : '未分类'
}

// 标签相关
const dynamicTags = ref([])
const inputVisible = ref(false)
const inputValue = ref('')
const tagInput = ref(null)

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'draft': '草稿',
    'published': '已发布',
  }
  return statusMap[status] || '草稿'
}

// 处理状态变更
const handleStatusChange = (command) => {
  postStatus.value = command

  if (command === 'published' && !publishedAt.value) {
    publishedAt.value = new Date()
  }

  hasUnsavedChanges.value = true
  ElMessage.success(`状态已更改为${getStatusText(command)}`)
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '未设置'

  if (typeof date === 'string') {
    date = new Date(date)
  }

  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

// 格式化时间（相对时间）
const formatTime = (date) => {
  if (!date) return ''

  const now = new Date()
  const diff = Math.floor((now - date) / 1000) // 秒数差

  if (diff < 60) {
    return '刚刚'
  } else if (diff < 3600) {
    return `${Math.floor(diff / 60)}分钟前`
  } else if (diff < 86400) {
    return `${Math.floor(diff / 3600)}小时前`
  } else {
    return formatDate(date)
  }
}

const handleClose = (tag) => {
  dynamicTags.value.splice(dynamicTags.value.indexOf(tag), 1)
  hasUnsavedChanges.value = true
}

const showInput = () => {
  if (dynamicTags.value.length >= 5) {
    ElMessage.warning('最多添加5个标签')
    return
  }

  inputVisible.value = true
  nextTick(() => {
    tagInput.value.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    if (dynamicTags.value.length >= 5) {
      ElMessage.warning('最多添加5个标签')
      inputValue.value = ''
      inputVisible.value = false
      return
    }

    // 检查标签是否已存在
    if (dynamicTags.value.includes(inputValue.value)) {
      ElMessage.warning('标签已存在')
      inputValue.value = ''
      return
    }

    dynamicTags.value.push(inputValue.value)
    hasUnsavedChanges.value = true
  }
  inputVisible.value = false
  inputValue.value = ''
}

// 封面上传
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('封面图片只能是图片格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('封面图片大小不能超过 2MB!')
    return false
  }
  return true
}

const uploadCover = (options) => {
  const file = options.file
  // 这里应该是上传到服务器的逻辑，这里简化为本地预览
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    coverUrl.value = reader.result
    hasUnsavedChanges.value = true
    ElMessage.success('封面上传成功')
  }
}

const removeCover = () => {
  coverUrl.value = ''
  hasUnsavedChanges.value = true
  ElMessage.success('封面已移除')
}

// 自动保存功能
const setupAutoSave = () => {
  autoSaveInterval.value = setInterval(() => {
    if (hasUnsavedChanges.value && blogTitle.value) {
      autoSaveDraft()
    }
  }, 60000) // 每分钟自动保存一次
}

const autoSaveDraft = () => {
  // 这里应该是自动保存草稿的API调用
  console.log('自动保存草稿:', getPostData())

  lastSavedTime.value = new Date()
  updatedAt.value = new Date()
  hasUnsavedChanges.value = false
  // 不显示消息，静默保存
}

// 获取文章数据
const getPostData = () => {
  return {
    title: blogTitle.value,
    content: blogContent.value,
    summary: blogSummary.value,
    status: postStatus.value,
    created_at: createdAt.value,
    updated_at: new Date(),
    published_at: publishedAt.value,
    category: category.value,
    tags: dynamicTags.value,
    cover_url: coverUrl.value,
    allow_comments: allowComments.value,
    is_public: isPublic.value,
    is_original: isOriginal.value,
    is_pinned: isPinned.value,
    custom_url: customUrl.value
  }
}

// 返回按钮
const goBack = () => {
  if (hasUnsavedChanges.value) {
    ElMessageBox.confirm(
        '您有未保存的内容，确定要离开吗？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    ).then(() => {
      window.close() // 关闭当前标签页
    }).catch(() => {
      // 用户取消，不做任何操作
    })
  } else {
    window.close() // 关闭当前标签页
  }
}

// 保存草稿
const saveDraft = () => {
  if (!blogTitle.value) {
    ElMessage.warning('请输入文章标题')
    return
  }

  // 这里应该是保存草稿的API调用
  console.log('保存文章:', getPostData())

  lastSavedTime.value = new Date()
  updatedAt.value = new Date()

  ElMessage.success('保存成功')
  hasUnsavedChanges.value = false
}

// 发布文章
const publishBlog = () => {
  if (!blogTitle.value) {
    ElMessage.warning('请输入文章标题')
    return
  }

  if (!blogContent.value) {
    ElMessage.warning('请输入文章内容')
    return
  }

  if (!category.value) {
    ElMessage.warning('请选择文章分类')
    return
  }

  // 设置状态为已发布
  postStatus.value = 'published'

  // 如果没有设置发布时间，则设置为当前时间
  if (!publishedAt.value) {
    publishedAt.value = new Date()
  }

  // 这里应该是发布文章的API调用
  console.log('发布文章:', getPostData())

  ElMessage.success('文章发布成功')
  setTimeout(() => {
    window.close() // 发布成功后关闭当前标签页
  }, 1500)
}

// 预览文章
const previewPost = () => {
  previewVisible.value = true
}

// 监听内容变化
const watchContentChanges = () => {
  const handleChange = () => {
    hasUnsavedChanges.value = true
  }

  // 监听标题和内容变化
  const titleInput = document.querySelector('.title-input input')
  const summaryTextarea = document.querySelector('.summary-input textarea')
  const contentTextarea = document.querySelector('.content-input textarea')

  if (titleInput) titleInput.addEventListener('input', handleChange)
  if (summaryTextarea) summaryTextarea.addEventListener('input', handleChange)
  if (contentTextarea) contentTextarea.addEventListener('input', handleChange)

  // 清理函数
  return () => {
    if (titleInput) titleInput.removeEventListener('input', handleChange)
    if (summaryTextarea) summaryTextarea.removeEventListener('input', handleChange)
    if (contentTextarea) contentTextarea.removeEventListener('input', handleChange)
  }
}

// 生命周期钩子
onMounted(() => {
  let cleanup;
  try {
    cleanup = watchContentChanges();
  } catch (error) {
    console.error("Error setting up content change watchers:", error);
    cleanup = () => {}; // Provide a no-op cleanup function in case of error
  }
  setupAutoSave()

  // 添加页面关闭前的提示
  window.addEventListener('beforeunload', (e) => {
    if (hasUnsavedChanges.value) {
      e.preventDefault()
      e.returnValue = ''
    }
  })

  onBeforeUnmount(() => {
    cleanup()
    clearInterval(autoSaveInterval.value)

    window.removeEventListener('beforeunload', (e) => {
      if (hasUnsavedChanges.value) {
        e.preventDefault()
        e.returnValue = ''
      }
    })
  })
})
</script>

<style scoped>
.editor-container {
  min-height: 100vh;
  background-color: #f7f9fc;
  display: flex;
  flex-direction: column;
  color: #333;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

/* 顶部导航栏样式 */
.editor-header {
  height: 64px;
  background-color: #fff;
  border-bottom: 1px solid #eaedf2;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-center {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-tag {
  font-weight: 500;
}

.last-saved {
  color: #8c8c8c;
  font-size: 13px;
}

.back-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  color: #333;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.back-button:hover {
  background-color: #f5f5f5;
  transform: translateX(-2px);
}

.editor-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.header-right {
  display: flex;
  gap: 12px;
}

.status-button {
  border: 1px solid #e6e6e6;
  background-color: #fff;
  color: #595959;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.status-button:hover {
  border-color: #d9d9d9;
  background-color: #fafafa;
}

.draft-button {
  border: 1px solid #e6e6e6;
  background-color: #fff;
  color: #595959;
  border-radius: 6px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.draft-button:hover {
  border-color: #d9d9d9;
  background-color: #fafafa;
}

.publish-button {
  background-color: #1890ff;
  border: none;
  color: #fff;
  border-radius: 6px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.publish-button:hover {
  background-color: #40a9ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(24, 144, 255, 0.2);
}

/* 主要内容区域样式 */
.editor-content-wrapper {
  display: flex;
  flex: 1;
  padding: 24px;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

/* 左侧编辑区样式 */
.editor-main {
  flex: 1;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all 0.3s ease;
}

.editor-main:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.title-container {
  padding: 24px 24px 16px;
}

.title-input {
  border: none;
  font-size: 24px;
  font-weight: 600;
}

.title-input :deep(input) {
  border: none;
  padding: 0;
  height: 44px;
  color: #262626;
  transition: all 0.2s ease;
}

.title-input :deep(input:focus) {
  box-shadow: none;
}

.title-input :deep(.el-input__suffix) {
  color: #bfbfbf;
  font-size: 12px;
}

.summary-container {
  padding: 0 24px 16px;
  border-bottom: 1px dashed #f0f0f0;
}

.summary-input {
  border: none;
  font-size: 14px;
}

.summary-input :deep(textarea) {
  border: none;
  padding: 0;
  color: #595959;
  line-height: 1.6;
  transition: all 0.2s ease;
}

.summary-input :deep(textarea:focus) {
  box-shadow: none;
}

.content-container {
  flex: 1;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.content-input {
  height: 100%;
}

.content-input :deep(textarea) {
  border: none;
  padding: 0;
  font-size: 16px;
  line-height: 1.8;
  color: #262626;
  height: 100%;
  transition: all 0.2s ease;
}

.content-input :deep(textarea:focus) {
  box-shadow: none;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background-color: #fafafa;
}

.toolbar-left {
  color: #8c8c8c;
  font-size: 13px;
}

.word-count {
  padding: 4px 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
}

/* 右侧设置区样式 */
.editor-sidebar {
  width: 340px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  height: fit-content;
}

.sidebar-section {
  background-color: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.sidebar-section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px;
  color: #262626;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title .el-icon {
  color: #1890ff;
}

.info-item, .seo-item {
  margin-bottom: 14px;
  display: flex;
  flex-direction: column;
}

.info-item:last-child, .seo-item:last-child {
  margin-bottom: 0;
}

.info-label, .seo-label, .setting-label {
  font-size: 14px;
  color: #595959;
  margin-bottom: 6px;
}

.info-value {
  font-size: 14px;
  color: #262626;
  background-color: #f5f7fa;
  padding: 6px 10px;
  border-radius: 4px;
}

.date-picker-wrapper {
  width: 100%;
}

.date-picker {
  width: 100%;
}

.category-select {
  width: 100%;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
  min-height: 32px;
}

.tag-item {
  margin: 0;
  border-radius: 4px;
  background-color: #e6f7ff;
  border-color: #91d5ff;
  color: #1890ff;
}

.add-tag-container {
  margin-top: 12px;
}

.tag-input {
  width: 100%;
}

.tag-hint {
  margin-top: 8px;
  font-size: 12px;
  color: #8c8c8c;
  text-align: right;
}

.button-new-tag {
  width: 100%;
  border: 1px dashed #d9d9d9;
  color: #8c8c8c;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.button-new-tag:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.cover-uploader {
  width: 100%;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #8c8c8c;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #fafafa;
}

.upload-placeholder:hover {
  border-color: #1890ff;
  color: #1890ff;
  background-color: #f0f7ff;
}

.upload-icon {
  font-size: 32px;
  margin-bottom: 8px;
  color: #bfbfbf;
}

.upload-placeholder:hover .upload-icon {
  color: #1890ff;
}

.upload-hint {
  font-size: 12px;
  margin-top: 8px;
  color: #bfbfbf;
}

.cover-preview {
  width: 100%;
  height: 100%;
  position: relative;
  border-radius: 8px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.cover-preview:hover .cover-image {
  transform: scale(1.05);
}

.cover-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.cover-preview:hover .cover-actions {
  opacity: 1;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

/* 预览对话框样式 */
.preview-dialog :deep(.el-dialog__header) {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.preview-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.preview-container {
  padding: 32px;
  max-height: 70vh;
  overflow-y: auto;
}

.preview-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 24px;
  color: #262626;
  line-height: 1.3;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 20px;
  color: #8c8c8c;
  font-size: 14px;
}

.preview-author, .preview-date, .preview-category {
  display: flex;
  align-items: center;
  gap: 6px;
}

.preview-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
}

.preview-tag {
  background-color: #e6f7ff;
  border-color: #91d5ff;
  color: #1890ff;
}

.preview-cover {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
}

.preview-cover img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
}

.preview-summary {
  background-color: #f7f9fc;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  font-size: 16px;
  color: #595959;
  line-height: 1.6;
  border-left: 4px solid #1890ff;
}

.preview-content {
  font-size: 16px;
  line-height: 1.8;
  color: #262626;
}

.empty-content {
  color: #bfbfbf;
  font-style: italic;
  text-align: center;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .editor-content-wrapper {
    flex-direction: column;
  }

  .editor-sidebar {
    width: 100%;
  }

  .sidebar-section {
    margin-bottom: 0;
  }
}

@media (max-width: 768px) {
  .editor-header {
    padding: 12px 16px;
    height: auto;
    flex-wrap: wrap;
  }

  .header-center {
    display: none;
  }

  .header-right {
    width: 100%;
    margin-top: 12px;
    justify-content: flex-end;
  }

  .editor-content-wrapper {
    padding: 16px;
  }

  .title-input :deep(input) {
    font-size: 20px;
  }

  .preview-title {
    font-size: 24px;
  }

  .preview-container {
    padding: 20px;
  }
}
</style>