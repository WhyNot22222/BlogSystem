<template>
  <div v-if="visible" class="preview-dialog-overlay" @click.self="close">
    <div class="preview-dialog" @click.stop>
      <div class="preview-header">
        <h2>{{ title || '博客预览' }}</h2>
        <button @click="close" class="close-btn">×</button>
      </div>
      <div class="preview-container">
        <h1 class="preview-title">{{ post.title || '无标题' }}</h1>

        <div class="preview-meta">
          <span class="preview-author" v-if="post.author">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
            {{ post.author.name || '未知作者' }}
          </span>

          <span class="preview-date" v-if="post.createdAt">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
              <line x1="16" y1="2" x2="16" y2="6"/>
              <line x1="8" y1="2" x2="8" y2="6"/>
              <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            {{ formatDate(post.createdAt) }}
          </span>

          <span class="preview-category" v-if="post.category">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
            </svg>
            {{ post.category }}
          </span>
        </div>

        <div class="preview-tags" v-if="post.tags && post.tags.length > 0">
          <span v-for="tag in post.tags" :key="tag" class="preview-tag">{{ tag }}</span>
        </div>

        <div class="preview-cover" v-if="post.coverUrl">
          <img :src="post.coverUrl" alt="文章封面" />
        </div>

        <div class="preview-summary" v-if="post.summary">
          <p>{{ post.summary }}</p>
        </div>

        <div class="preview-content">
          <div v-if="post.formattedContent" v-html="post.formattedContent"></div>
          <p v-else class="empty-content">暂无内容</p>
        </div>

        <!-- 添加详情按钮 -->
        <div class="preview-actions">
          <button
              @click="goToDetail"
              class="detail-btn"
              :disabled="!post.id"
          >
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 18l6-6-6-6"/>
            </svg>
            查看详情
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  post: {
    type: Object,
    default: () => ({})
  },
  title: {
    type: String,
    default: '文章预览'
  }
})

const emit = defineEmits(['close'])
const router = useRouter()

const close = () => {
  emit('close')
}

const goToDetail = () => {
  if (props.post.id) {
    const routeData = router.resolve({
      path: '/blog-detail',
      query: { 
        postId: props.post.id,
      }
    });
    window.open(routeData.href, '_blank');
    close();
  }
}

const formatDate = (dateInput) => {
  if (!dateInput) return '未设置'
  let date = dateInput
  if (typeof dateInput === 'string') {
    date = new Date(dateInput)
  }
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

watch(() => props.visible, async (newVal) => {
  if (newVal && props.post?.id) {
    try {
      const res = await request.put(`/posts/${props.post.id}/views`)
      if (res.code === '200') {
        // 如果父级需要更新数据，可以emit事件
      }
    } catch (error) {
      ElMessage.log('浏览量统计失败')
      console.error('views update error:', error)
    }
  }
})
</script>

<style scoped>
@import url("../assets/css/BlogEditor.css");

/* 详情按钮样式 */
.preview-actions {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: center;
}

.detail-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.detail-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
}

.detail-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(102, 126, 234, 0.3);
}

.detail-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  box-shadow: none;
}

.detail-btn svg {
  width: 16px;
  height: 16px;
  transition: transform 0.3s ease;
}

.detail-btn:hover:not(:disabled) svg {
  transform: translateX(2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-btn {
    padding: 10px 20px;
    font-size: 13px;
  }

  .preview-actions {
    margin-top: 20px;
    padding-top: 16px;
  }
}
</style>
