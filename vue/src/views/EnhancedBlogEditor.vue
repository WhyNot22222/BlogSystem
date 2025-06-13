<template>
  <div class="editor-container">
    <!-- 顶部导航栏 -->
    <header class="editor-header">
      <div class="header-left">
        <button class="back-button" @click="goBack">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="m12 19-7-7 7-7"/>
            <path d="m19 12H5"/>
          </svg>
        </button>
        <h1 class="editor-title">{{ postId ? '编辑博客' : '创建新博客' }}</h1>
      </div>
      <div class="header-center">
        <span v-if="postStatus === 'draft'" class="status-tag draft">草稿</span>
        <span v-else-if="postStatus === 'published'" class="status-tag published">已发布</span>
        <span v-else-if="postStatus === 'archived'" class="status-tag archived">已归档</span>
        <span class="last-saved" v-if="lastSavedTime">
          上次保存: {{ formatTime(lastSavedTime) }}
        </span>
      </div>
      <div class="header-right">
        <select class="status-select" v-model="postStatus" @change="handleStatusChange">
          <option value="draft">草稿</option>
          <option value="published">发布</option>
        </select>
        <button class="draft-button" @click="saveDraft">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
            <polyline points="17,21 17,13 7,13 7,21"/>
            <polyline points="7,3 7,8 15,8"/>
          </svg>
          保存
        </button>
        <button class="publish-button" @click="publishBlog">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="m3 16 4 4 4-4"/>
            <path d="M7 20V4"/>
            <path d="M20 8l-4-4-4 4"/>
            <path d="M17 4v16"/>
          </svg>
          发布文章
        </button>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <div class="editor-content-wrapper">
      <!-- 左侧编辑区 -->
      <div class="editor-main">
        <!-- 标题输入 -->
        <div class="title-container">
          <input
              v-model="blogTitle"
              placeholder="请输入文章标题"
              class="title-input"
              maxlength="100"
          />
          <span class="title-count">{{ blogTitle.length }}/100</span>
        </div>

        <!-- 摘要输入 -->
        <div class="summary-container">
          <textarea
              v-model="blogSummary"
              placeholder="请输入文章摘要（可选，不超过200字）"
              rows="2"
              maxlength="200"
              class="summary-input"
          ></textarea>
          <span class="summary-count">{{ blogSummary.length }}/200</span>
        </div>

        <!-- 富文本工具栏 -->
        <div class="formatting-toolbar">
          <div class="toolbar-group">
            <button
                class="format-btn"
                :class="{ active: isFormatActive('h2') }"
                @click="toggleFormat('h2')"
                title="标题2"
            >
              <strong>H2</strong>
            </button>
            <button
                class="format-btn"
                :class="{ active: isFormatActive('h3') }"
                @click="toggleFormat('h3')"
                title="标题3"
            >
              <strong>H3</strong>
            </button>
          </div>

          <div class="toolbar-separator"></div>

          <div class="toolbar-group">
            <button
                class="format-btn"
                :class="{ active: isFormatActive('strong') }"
                @click="toggleFormat('strong')"
                title="粗体"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M6 4h8a4 4 0 0 1 4 4 4 4 0 0 1-4 4H6z"/>
                <path d="M6 12h9a4 4 0 0 1 4 4 4 4 0 0 1-4 4H6z"/>
              </svg>
            </button>
            <button
                class="format-btn"
                :class="{ active: isFormatActive('em') }"
                @click="toggleFormat('em')"
                title="斜体"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="19" y1="4" x2="10" y2="4"/>
                <line x1="14" y1="20" x2="5" y2="20"/>
                <line x1="15" y1="4" x2="9" y2="20"/>
              </svg>
            </button>
          </div>

          <div class="toolbar-separator"></div>

          <div class="toolbar-group">
            <button
                class="format-btn"
                :class="{ active: isFormatActive('ul') }"
                @click="toggleFormat('ul')"
                title="无序列表"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="8" y1="6" x2="21" y2="6"/>
                <line x1="8" y1="12" x2="21" y2="12"/>
                <line x1="8" y1="18" x2="21" y2="18"/>
                <line x1="3" y1="6" x2="3.01" y2="6"/>
                <line x1="3" y1="12" x2="3.01" y2="12"/>
                <line x1="3" y1="18" x2="3.01" y2="18"/>
              </svg>
            </button>
            <button
                class="format-btn"
                :class="{ active: isFormatActive('ol') }"
                @click="toggleFormat('ol')"
                title="有序列表"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="10" y1="6" x2="21" y2="6"/>
                <line x1="10" y1="12" x2="21" y2="12"/>
                <line x1="10" y1="18" x2="21" y2="18"/>
                <path d="M4 6h1v4"/>
                <path d="M4 10h2"/>
                <path d="M6 18H4c0-1 2-2 2-3s-1-1.5-2-1"/>
              </svg>
            </button>
          </div>

          <div class="toolbar-separator"></div>

          <div class="toolbar-group">
            <button class="format-btn" @click="clearFormatting" title="清除格式">
              <Close />
            </button>
          </div>

          <div class="toolbar-separator"></div>

          <div class="toolbar-group">
            <button class="format-btn" @click="togglePreviewMode" title="预览模式">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
            </button>
          </div>
        </div>

        <!-- 内容编辑区 -->
        <div class="content-container">
          <div v-if="!previewMode" class="editor-mode">
            <textarea
                ref="contentTextarea"
                v-model="blogContent"
                placeholder="开始撰写您的博客内容..."
                class="content-input"
                @select="updateSelection"
                @keyup="updateSelection"
                @mouseup="updateSelection"
            ></textarea>
          </div>
          <div v-else class="preview-mode">
            <div class="formatted-content" v-html="formattedContent"></div>
          </div>
        </div>

        <!-- 底部工具栏 -->
        <div class="editor-toolbar">
          <div class="toolbar-left">
            <span class="word-count">{{ blogContent.length }} 字</span>
            <span class="mode-indicator">{{ previewMode ? '预览模式' : '编辑模式' }}</span>
          </div>
          <div class="toolbar-right">
            <button class="preview-btn" @click="previewPost">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              完整预览
            </button>
          </div>
        </div>
      </div>

      <!-- 右侧设置区 -->
      <div class="editor-sidebar">
        <!-- 发布信息 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
              <line x1="16" y1="2" x2="16" y2="6"/>
              <line x1="8" y1="2" x2="8" y2="6"/>
              <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
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
            <input
                v-model="publishedAt"
                type="datetime-local"
                :disabled="postStatus !== 'published'"
                class="date-input"
            />
          </div>
          <div class="info-item">
            <span class="info-label">作者</span>
            <span class="info-value">{{ authorName }}</span>
          </div>
        </div>

        <!-- 文章分类 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
            </svg>
            文章分类
          </h3>
          <select v-model="category" class="category-select">
            <option value="">选择分类</option>
            <option v-for="item in categories" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>
        </div>

        <!-- 标签 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
              <line x1="7" y1="7" x2="7.01" y2="7"/>
            </svg>
            标签
          </h3>
          <div class="tags-container">
            <span v-for="tag in dynamicTags" :key="tag" class="tag-item">
              {{ tag }}
              <button @click="handleClose(tag)" class="tag-close">×</button>
            </span>
          </div>
          <div class="add-tag-container">
            <input
                v-if="inputVisible"
                ref="tagInput"
                v-model="inputValue"
                class="tag-input"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
                placeholder="输入标签名"
            />
            <button v-else class="button-new-tag" @click="showInput">
              + 新标签
            </button>
          </div>
          <div class="tag-hint" v-if="dynamicTags.length > 0">
            已添加 {{ dynamicTags.length }}/5 个标签
          </div>
        </div>

        <!-- 封面图片 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
              <circle cx="9" cy="9" r="2"/>
              <path d="m21 15-3.086-3.086a2 2 0 0 0-2.828 0L6 21"/>
            </svg>
            封面图片
          </h3>
          <div class="cover-upload-area">
            <div v-if="coverUrl" class="cover-preview">
              <img :src="coverUrl" class="cover-image" alt="文章封面" />
              <button class="cover-remove" @click="removeCover">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3,6 5,6 21,6"/>
                  <path d="m19,6v14a2,2 0 0,1 -2,2H7a2,2 0 0,1 -2,-2V6m3,0V4a2,2 0 0,1 2,-2h4a2,2 0 0,1 2,2v2"/>
                </svg>
              </button>
            </div>
            <div v-else class="upload-placeholder" @click="triggerFileUpload">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="12" y1="5" x2="12" y2="19"/>
                <line x1="5" y1="12" x2="19" y2="12"/>
              </svg>
              <span>点击上传封面图片</span>
              <span class="upload-hint">建议尺寸 1200×675px，不超过5MB</span>
            </div>
            <input
                ref="fileInput"
                type="file"
                accept="image/*"
                @change="uploadCover"
                style="display: none"
            />
          </div>
        </div>

        <!-- 文章设置 -->
        <div class="sidebar-section">
          <h3 class="section-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="3"/>
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1 1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
            </svg>
            文章设置
          </h3>
          <div class="setting-item">
            <span class="setting-label">允许评论</span>
            <label class="switch">
              <input type="checkbox" v-model="allowComments">
              <span class="slider"></span>
            </label>
          </div>
          <div class="setting-item">
            <span class="setting-label">公开可见</span>
            <label class="switch">
              <input type="checkbox" v-model="isPublic">
              <span class="slider"></span>
            </label>
          </div>
          <div class="setting-item">
            <span class="setting-label">置顶文章</span>
            <label class="switch">
              <input type="checkbox" v-model="isPinned">
              <span class="slider"></span>
            </label>
          </div>
        </div>
      </div>
    </div>

    <!-- 预览对话框 -->
    <div v-if="previewVisible" class="preview-dialog-overlay" @click="previewVisible = false">
      <div class="preview-dialog" @click.stop>
        <div class="preview-header">
          <h2>文章预览</h2>
          <button @click="previewVisible = false" class="close-btn">×</button>
        </div>
        <div class="preview-container">
          <h1 class="preview-title">{{ blogTitle || '无标题' }}</h1>
          <div class="preview-meta">
            <span class="preview-author">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              {{ authorName }}
            </span>
            <span class="preview-date">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                <line x1="16" y1="2" x2="16" y2="6"/>
                <line x1="8" y1="2" x2="8" y2="6"/>
                <line x1="3" y1="10" x2="21" y2="10"/>
              </svg>
              {{ formatDate(publishedAt || new Date()) }}
            </span>
            <span class="preview-category" v-if="category">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
              </svg>
              {{ getCategoryLabel(category) }}
            </span>
          </div>
          <div class="preview-tags" v-if="dynamicTags.length > 0">
            <span v-for="tag in dynamicTags" :key="tag" class="preview-tag">{{ tag }}</span>
          </div>
          <div class="preview-cover" v-if="coverUrl">
            <img :src="coverUrl" alt="文章封面" />
          </div>
          <div class="preview-summary" v-if="blogSummary">
            <p>{{ blogSummary }}</p>
          </div>
          <div class="preview-content">
            <div v-if="formattedContent" v-html="formattedContent"></div>
            <p v-else class="empty-content">暂无内容</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onBeforeUnmount, watch } from 'vue'
import { Close } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import request from '@/utils/request'
import { useStore } from 'vuex'
import { ElMessage } from "element-plus"
import * as postHelper from '@/utils/postHelper'

const MAX_FILE_SIZE_MB = 5; // 最大5MB

const router = useRouter()
const route = useRoute()

const store = useStore();
const userId = computed(() => store.getters.userId)

const postId = ref(null)
const blogTitle = ref('')
const blogSummary = ref('')
const blogContent = ref('')
const category = ref('')
const coverUrl = ref('')
const allowComments = ref(true)
const isPublic = ref(true)
const isPinned = ref(false)
const hasUnsavedChanges = ref(false)
const autoSaveInterval = ref(null)
const lastSavedTime = ref(null)
const previewVisible = ref(false)
const postStatus = ref('draft')
const createdAt = ref(new Date())
const updatedAt = ref(new Date())
const publishedAt = ref('')
const authorName = ref('当前用户')
const dynamicTags = ref([])
const inputVisible = ref(false)
const inputValue = ref('')
const tagInput = ref(null)
const fileInput = ref(null)

// 新增的富文本相关数据
const previewMode = ref(false)
const contentTextarea = ref(null)
const selectionStart = ref(0)
const selectionEnd = ref(0)

// 新增的格式化内容计算属性
const formattedContent = computed(() => {
  if (!blogContent.value) return ''

  // 将纯文本转换为格式化的HTML
  let content = blogContent.value

  // 处理标题
  content = content.replace(/^## (.+)$/gm, '<h2>$1</h2>')
  content = content.replace(/^### (.+)$/gm, '<h3>$1</h3>')

  // 处理粗体和斜体
  content = content.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
  content = content.replace(/\*(.+?)\*/g, '<em>$1</em>')

  // 处理列表
  content = content.replace(/^\* (.+)$/gm, '<li>$1</li>')
  content = content.replace(/^(\d+)\. (.+)$/gm, '<li>$2</li>')

  // 包装连续的li为ul或ol
  content = content.replace(/(<li>.*<\/li>)/gs, (match) => {
    if (blogContent.value.includes('1. ') || blogContent.value.includes('2. ')) {
      return `<ol>${match}</ol>`
    }
    return `<ul>${match}</ul>`
  })

  // 处理段落
  content = content.replace(/^(?!<[h|u|o|l]).+$/gm, '<p>$&</p>')

  // 清理空段落
  content = content.replace(/<p><\/p>/g, '')

  return content
})

const categories = ref([])

const fetchCategories = async () => {
  try {
    const response = await request.get('/categories')
    if (response.code === '200') {
      categories.value = response.data.map(item => ({
        value: item.id,
        label: item.name
      }))
    } else {
      ElMessage.error(response.msg || '获取分类失败')
    }
  } catch (error) {
    ElMessage.error('获取分类失败，请检查网络连接')
    console.error('Fetch categories error:', error)
  }
}

// 文本格式化方法
const updateSelection = () => {
  if (contentTextarea.value) {
    selectionStart.value = contentTextarea.value.selectionStart
    selectionEnd.value = contentTextarea.value.selectionEnd
  }
}

const isFormatActive = (format) => {
  const content = blogContent.value
  const start = selectionStart.value
  const end = selectionEnd.value

  if (start === end) return false

  const selectedText = content.substring(start, end)

  switch (format) {
    case 'h2':
      return selectedText.startsWith('## ') && selectedText.endsWith(' ##')
    case 'h3':
      return selectedText.startsWith('### ') && selectedText.endsWith(' ###')
    case 'strong':
      return selectedText.startsWith('**') && selectedText.endsWith('**')
    case 'em':
      return selectedText.startsWith('*') && selectedText.endsWith('*')
    case 'ul':
      return selectedText.includes('* ')
    case 'ol':
      return /^\d+\. /.test(selectedText)
    default:
      return false
  }
}

const toggleFormat = (format) => {
  const textarea = contentTextarea.value
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = blogContent.value.substring(start, end)

  if (!selectedText) return

  let formattedText = ''

  switch (format) {
    case 'h2':
      if (isFormatActive('h2')) {
        formattedText = selectedText.replace(/^## /, '').replace(/ ##$/, '')
      } else {
        formattedText = `## ${selectedText}`
      }
      break
    case 'h3':
      if (isFormatActive('h3')) {
        formattedText = selectedText.replace(/^### /, '').replace(/ ###$/, '')
      } else {
        formattedText = `### ${selectedText}`
      }
      break
    case 'strong':
      if (isFormatActive('strong')) {
        formattedText = selectedText.replace(/^\*\*/, '').replace(/\*\*$/, '')
      } else {
        formattedText = `**${selectedText}**`
      }
      break
    case 'em':
      if (isFormatActive('em')) {
        formattedText = selectedText.replace(/^\*/, '').replace(/\*$/, '')
      } else {
        formattedText = `*${selectedText}*`
      }
      break
    case 'ul':
      if (isFormatActive('ul')) {
        formattedText = selectedText.replace(/^\* /gm, '')
      } else {
        formattedText = selectedText.replace(/^(.+)$/gm, '* $1')
      }
      break
    case 'ol':
      if (isFormatActive('ol')) {
        formattedText = selectedText.replace(/^\d+\. /gm, '')
      } else {
        const lines = selectedText.split('\n')
        formattedText = lines.map((line, index) => `${index + 1}. ${line}`).join('\n')
      }
      break
    default:
      formattedText = selectedText
  }

  const newContent = blogContent.value.substring(0, start) + formattedText + blogContent.value.substring(end)
  blogContent.value = newContent
  hasUnsavedChanges.value = true

  // 恢复选择
  nextTick(() => {
    textarea.focus()
    textarea.setSelectionRange(start, start + formattedText.length)
  })
}

const clearFormatting = () => {
  const textarea = contentTextarea.value
  if (!textarea) return

  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = blogContent.value.substring(start, end)

  if (!selectedText) return

  // 移除所有格式标记
  let cleanText = selectedText
      .replace(/^#{1,6} /gm, '')
      .replace(/\*\*(.+?)\*\*/g, '$1')
      .replace(/\*(.+?)\*/g, '$1')
      .replace(/^\* /gm, '')
      .replace(/^\d+\. /gm, '')

  const newContent = blogContent.value.substring(0, start) + cleanText + blogContent.value.substring(end)
  blogContent.value = newContent
  hasUnsavedChanges.value = true

  nextTick(() => {
    textarea.focus()
    textarea.setSelectionRange(start, start + cleanText.length)
  })
}

const togglePreviewMode = () => {
  previewMode.value = !previewMode.value
}

// 原有方法保持不变
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

const formatTime = (date) => {
  if (!date) return ''
  const now = new Date()
  const diff = Math.floor((now - date) / 1000)
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

const saveTags = async () => {
  try {
    // 步骤1：获取现有标签ID
    const existingTagsRes = await request.get('/post-tags', {
      params: { postId: postId.value }
    });
    const l1 = existingTagsRes.data;

    // 步骤2：创建新标签并获取ID
    const l2 = await Promise.all(
      dynamicTags.value.map(async tag => {
        try {
          const res = await request.post('/tags', null, {
            params: { name: tag }
          });
          return res.data;
        } catch (error) {
          ElMessage.error(`标签${tag}创建失败：${error.msg}`);
          return null;
        }
      })
    );
    const validTagIds = l2.filter(id => id !== null);

    // 步骤3：计算差集
    const l3 = l1.filter(id => validTagIds.includes(id));
    const toDelete = l1.filter(id => !l3.includes(id));
    const toAdd = validTagIds.filter(id => !l3.includes(id));

    console.log('l1:', l2)
    console.log('l2:', l2)
    console.log('l3:', l3)
    console.log('toDelete:', toDelete)
    console.log('toAdd:', toAdd)

    // 执行删除和新增操作
    if (toDelete.length > 0) {
      await request.delete('/post-tags/batch', {
        params: {
          postId: postId.value,
          tagIds: toDelete.join(',')
        }
      });
    }

    if (toAdd.length > 0) {
      await request.post('/post-tags', null, {
        params: {
          postId: Number(postId.value),
          tagIds: toAdd.join(',')
        }
      });
    }

    ElMessage.success('标签同步成功');
  } catch (error) {
    ElMessage.error('标签保存失败：' + (error.msg || error.message));
    console.error('标签保存错误:', error);
  }
}

const showInput = () => {
  if (dynamicTags.value.length >= 5) {
    alert('最多添加5个标签')
    return
  }
  inputVisible.value = true
  nextTick(() => {
    tagInput.value?.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    if (dynamicTags.value.length >= 5) {
      alert('最多添加5个标签')
      inputValue.value = ''
      inputVisible.value = false
      return
    }
    if (dynamicTags.value.includes(inputValue.value)) {
      alert('标签已存在')
      inputValue.value = ''
      return
    }
    dynamicTags.value.push(inputValue.value)
    hasUnsavedChanges.value = true
  }
  inputVisible.value = false
  inputValue.value = ''
}

const triggerFileUpload = () => {
  fileInput.value?.click()
}

const uploadCover = (event) => {
  const file = event.target.files[0]
  if (!file) return

  const isImage = file.type.startsWith('image/')

  if (!isImage) {
    alert('封面图片只能是图片格式!')
    return
  }
  if (file.size > MAX_FILE_SIZE_MB * 1024 * 1024) {
    alert(`文件大小超过${MAX_FILE_SIZE_MB}MB限制`);
    return;
  }

  // 创建预览URL
  const reader = new FileReader()
  reader.onload = (e) => {
    coverUrl.value = e.target.result
    hasUnsavedChanges.value = true
  }
  reader.readAsDataURL(file)
}

const removeCover = () => {
  coverUrl.value = ''
  hasUnsavedChanges.value = true
}

// 封面上传至后端方法
const uploadCoverToBackend = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId.value);
    formData.append('postId', postId.value || '0'); // 临时处理未保存的postId

    const response = await request.post('/file/uploadCover', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (response.code === '200') {
      const previewUrl = URL.createObjectURL(file);
      coverUrl.value = previewUrl;
      hasUnsavedChanges.value = true;
      ElMessage.success('封面上传成功');
    }
  } catch (error) {
    ElMessage.error('封面上传失败');
    console.error('Upload error:', error);
  }
}

const getPostData = () => {
  return {
    title: blogTitle.value,
    content: blogContent.value, // 纯文本内容
    formattedContent: formattedContent.value, // 格式化内容
    summary: blogSummary.value,
    status: postStatus.value,
    createdAt: createdAt.value,
    updatedAt: new Date(),
    publishedAt: publishedAt.value,
    categoryId: category.value,
    tags: dynamicTags.value,
    allowComments: allowComments.value,
    isPublic: isPublic.value,
    isPinned: isPinned.value
  }
}

const saveDraft = async () => {
  if (!blogTitle.value) {
    ElMessage.warning('请输入文章标题')
    return
  }
  const postData = getPostData()
  postData.userId = userId.value
  postData.views = 0

  try {
    let response;
    if (postId.value) {
      response = await request.put(`/posts/${postId.value}`, postData);
    } else {
      response = await request.post('/posts', postData);
    }

    if (response.code === '200') {
      if (!postId.value) {
        localStorage.setItem('draftId', String(response.data.id));
      }
      await saveTags()
      await uploadCoverToBackend(fileInput.value?.files[0])
      postId.value = response.data.id || postId.value;
      lastSavedTime.value = new Date();
      updatedAt.value = new Date();
      isPublic.value = response.data.public;
      isPinned.value = response.data.pinned;
      hasUnsavedChanges.value = false;
      ElMessage.success('保存草稿成功');
    } else {
      ElMessage.error(response.msg || '保存草稿失败');
    }
  } catch (error) {
    ElMessage.error('保存草稿失败：', error);
  }
}

const handleStatusChange = () => {
  if (postStatus.value === 'published' && !publishedAt.value) {
    publishedAt.value = new Date().toISOString().slice(0, 16)
  }
  hasUnsavedChanges.value = true
}

const publishBlog = async () => {
  if (!blogTitle.value) {
    alert('请输入文章标题')
    return
  }
  if (!blogContent.value) {
    alert('请输入文章内容')
    return
  }
  if (!category.value) {
    alert('请选择文章分类')
    return
  }

  const postData = getPostData()
  postData.status = 'published'
  postData.userId = userId.value
  postData.views = 0
  if (!postData.publishedAt) {
    postData.publishedAt = new Date()
  }

  console.log('发布文章——postData: ', postData)

  try {
    let response
    if (postId.value) {
      response = await request.put(`/posts/${postId.value}`, postData)
    } else {
      response = await request.post('/posts', postData)
    }
    if (response.code === '200') {
      localStorage.removeItem('draftId')
      postId.value = response.data.id
      postStatus.value = 'published'
      publishedAt.value = new Date(postData.publishedAt)
      lastSavedTime.value = new Date()
      updatedAt.value = new Date()
      isPublic.value = response.data.isPublic
      isPinned.value = response.data.isPinned
      hasUnsavedChanges.value = false
      ElMessage.success('文章发布成功')
      setTimeout(() => {
        router.push('/home')
      }, 1500)
    } else {
      ElMessage.error(response.msg || '发布文章失败')
    }
  } catch (error) {
    ElMessage.error('发布文章失败，请检查网络连接')
    console.error('Publish blog error:', error)
  }
}

const previewPost = () => {
  previewVisible.value = true
}

const getCategoryLabel = (value) => {
  const cat = categories.value.find(item => item.value === value)
  return cat ? cat.label : '未分类'
}

const goBack = () => {
  if (hasUnsavedChanges.value) {
    if (confirm('您有未保存的内容，确定要离开吗？')) {
      router.push('/home')
    }
  } else {
    router.push('/home')
  }
}

// 监听内容变化
const watchContentChanges = () => {
  const handleChange = () => {
    hasUnsavedChanges.value = true
  }
  const titleInput = document.querySelector('.title-input input')
  const summaryTextarea = document.querySelector('.summary-input textarea')
  const contentTextarea = document.querySelector('.content-input textarea')
  if (titleInput) titleInput.addEventListener('input', handleChange)
  if (summaryTextarea) summaryTextarea.addEventListener('input', handleChange)
  if (contentTextarea) contentTextarea.addEventListener('input', handleChange)
  return () => {
    if (titleInput) titleInput.removeEventListener('input', handleChange)
    if (summaryTextarea) summaryTextarea.removeEventListener('input', handleChange)
    if (contentTextarea) contentTextarea.removeEventListener('input', handleChange)
  }
}

// 自动保存
const setupAutoSave = () => {
  autoSaveInterval.value = setInterval(() => {
    if (hasUnsavedChanges.value && blogTitle.value) {
      saveDraft()
    }
  }, 60000)
}

const fetchPost = async (id) => {
  try {
    const response = await request.get(`/posts/${id}`)
    if (response.code === '200') {
      const post = response.data
      console.log('获取文章成功:', post)
      const tagIds = await postHelper.fetchTags(post.id)
      const tagNames = await Promise.all(
        tagIds.map(async tagId => {
          const name = await postHelper.fetchTagName(Number(tagId));
          return name;
        })
      );

      blogTitle.value = post.title
      blogContent.value = post.content
      blogSummary.value = post.summary
      category.value = post.categoryId
      coverUrl.value = await postHelper.fetchCover(userId.value, post.id)
      allowComments.value = post.allowComments
      isPublic.value = post.public
      isPinned.value = post.pinned
      postStatus.value = post.status.toLowerCase()
      createdAt.value = new Date(post.createdAt)
      updatedAt.value = new Date(post.updatedAt)
      publishedAt.value = post.publishedAt ? new Date(post.publishedAt) : null
      dynamicTags.value = tagNames || []
      postId.value = post.id
      hasUnsavedChanges.value = false

      // 设置作者信息
      if (post.user && post.user.username) {
        authorName.value = post.user.username
      }
    }
  } catch (error) {
    console.error('获取文章失败:', error)
    ElMessage.error('加载文章失败，请检查网络连接')
  }
}

const fetchBlogData = async (postId) => {
  try {
    const response = await request.get(`/posts/${postId}`)
    if (response.code === '200') {
      const postData = response.data
      blogTitle.value = postData.title
      blogContent.value = postData.content
      blogSummary.value = postData.summary
      category.value = postData.categoryId
      dynamicTags.value = postData.tags || []
      postStatus.value = postData.status
      coverUrl.value = await postHelper.fetchCover(userId.value, postData.id)
      createdAt.value = postData.createdAt
      updatedAt.value = postData.updatedAt
      publishedAt.value = postData.publishedAt
      const tagIds = await postHelper.fetchTags(postData.id)
      const tagNames = await Promise.all(
        tagIds.map(async tagId => {
          const name = await postHelper.fetchTagName(Number(tagId));
          return name;
        })
      );
      dynamicTags.value = tagNames || []
    }
  } catch (error) {
    ElMessage.error('加载博客数据失败：' + error)
    console.log('Fetch blog data error:', error)
  }
}

watch(
  () => route.query.postId,
  async (newPostId) => {
    if (newPostId) {
      postId.value = newPostId
      await fetchBlogData(newPostId)
    }
  }
)

// 生命周期钩子
onMounted(async () => {
  postId.value = route.query.postId
  if (postId.value) {
    await fetchBlogData(postId.value)
  }

  fetchCategories()
  setupAutoSave()

  // 从本地存储获取草稿ID
  const savedDraftId = localStorage.getItem('draftId')
  if (savedDraftId && savedDraftId !== 'null' && !isNaN(savedDraftId)) {
    postId.value = Number(savedDraftId) // 确保转换为数字类型
    fetchPost(postId.value)
  }

  let cleanup
  try {
    cleanup = watchContentChanges()
  } catch (error) {
    console.error("Error setting up content change watchers:", error)
    cleanup = () => {}
  }

  if (route.params.id) {
    postId.value = route.params.id
    fetchPost(postId.value)
  }

  window.addEventListener('beforeunload', (e) => {
    if (hasUnsavedChanges.value) {
      e.preventDefault()
    }
    localStorage.removeItem('draftId')
  })

  onBeforeUnmount(() => {
    cleanup()
    clearInterval(autoSaveInterval.value)
    window.removeEventListener('beforeunload', (e) => {
      if (hasUnsavedChanges.value) {
        e.preventDefault()
      }
    })
  })
})
</script>

<style scoped src="../assets/css/BlogEditor.css">
</style>