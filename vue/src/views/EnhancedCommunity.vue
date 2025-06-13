<template>
  <div class="community-container">
    <!-- 顶部导航和筛选区 -->
    <div class="community-header">
      <div class="filter-tabs">
        <el-tabs v-model="activeTab" class="modern-tabs">
          <el-tab-pane label="🔥 热门" name="hot"></el-tab-pane>
          <el-tab-pane label="⚡ 最新" name="latest"></el-tab-pane>
          <el-tab-pane label="👍 点赞最多" name="mostLiked"></el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 热门标签区 -->
    <div class="hot-tags">
      <div class="tag-section">
        <span class="tag-title">🏷️ 热门话题</span>
        <div class="tags-container">
          <el-tag
              type=""
              effect="plain"
              class="tag-item modern-tag"
              @click="filterByTag('')"
              :class="{ 'active-tag': selectedTag === '' }"
          >
            全部
          </el-tag>
          <el-tag
              v-for="tag in hotTags"
              :key="tag.id"
              :type="tag.type"
              effect="plain"
              class="tag-item modern-tag"
              @click="filterByTag(tag.name)"
              :class="{ 'active-tag': selectedTag === tag.name }"
          >
            #{{ tag.name }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="community-content">
      <!-- 帖子列表 -->
      <div class="posts-container">
        <div v-if="loading" class="loading-container">
          <div class="loading-skeleton">
            <el-skeleton :rows="5" animated />
          </div>
        </div>

        <div v-else-if="error" class="error-container">
          <div class="error-card">
            <div class="error-icon">⚠️</div>
            <div class="error-message">{{ error }}</div>
            <el-button type="primary" @click="fetchAndProcessPosts">重新加载</el-button>
          </div>
        </div>

        <template v-else>
          <div v-for="post in filteredPosts" :key="post.id" class="post-card modern-card">
            <!-- 帖子头部 -->
            <div class="post-header">
              <div class="user-info">
                <div class="avatar-container">
                  <el-avatar :size="44" :src="post.author.avatar" class="user-avatar">
                    <template #default>
                      <div class="avatar-fallback">👤</div>
                    </template>
                  </el-avatar>
                  <div class="online-indicator"></div>
                </div>
                <div class="user-details">
                  <div class="username">{{ post.author.name }}</div>
                  <div class="post-time">
                    <svg class="time-icon" viewBox="0 0 16 16" fill="currentColor">
                      <path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/>
                      <path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/>
                    </svg>
                    {{ formatTime(post.createdAt) }}
                  </div>
                </div>
              </div>

              <div class="follow-section" v-if="post.author.id !== currentUser.id">
                <el-button
                    :type="post.author.isFollowed ? 'default' : 'primary'"
                    size="small"
                    class="follow-btn"
                    @click="toggleFollow(post.author)"
                    :loading="post.author.followLoading"
                >
                  <span v-if="!post.author.isFollowed">+ 关注</span>
                  <span v-else>✓ 已关注</span>
                </el-button>
              </div>
            </div>

            <!-- 帖子标题 -->
            <div class="post-title" @click="viewPostDetail(post)">
              {{ post.title }}
            </div>

            <!-- 帖子内容 -->
            <div class="post-content" @click="viewPostDetail(post)">
              {{ post.summary }}
            </div>

            <!-- 帖子图片 -->
            <div v-if="post.images && post.images.length > 0" class="post-images">
              <div
                  v-for="(image, index) in post.images.slice(0, 4)"
                  :key="index"
                  class="image-wrapper"
                  :class="`image-${post.images.length > 1 ? 'grid' : 'single'}`"
              >
                <el-image
                    :src="image.url"
                    fit="cover"
                    class="post-image"
                    :preview-src-list="post.images.map(img => img.url)"
                    :initial-index="index"
                />
                <div v-if="index === 3 && post.images.length > 4" class="more-images">
                  +{{ post.images.length - 4 }}
                </div>
              </div>
            </div>

            <!-- 标签 -->
            <div class="post-tags" v-if="post.tags && post.tags.length > 0">
              <el-tag
                  v-for="tag in post.tags"
                  :key="tag"
                  size="small"
                  effect="plain"
                  class="post-tag"
                  @click.stop="filterByTag(tag)"
              >
                #{{ tag }}
              </el-tag>
            </div>

            <!-- 操作按钮 -->
            <div class="post-actions">
              <div class="actions-left">
                <div class="action-item like-action" @click="toggleLike(post)" :class="{ 'active': post.isLiked }">
                  <div class="action-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
                    </svg>
                  </div>
                  <span class="action-text">{{ post.likes }}</span>
                </div>

                <div class="action-item comment-action" @click="openComments(post)">
                  <div class="action-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                    </svg>
                  </div>
                  <span class="action-text">{{ post.comments.length }}</span>
                </div>

                <div class="action-item favorite-action" @click="openFavoritesModal(post)" :class="{ 'active': post.isFavorited }">
                  <div class="action-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                    </svg>
                  </div>
                  <span class="action-text">{{ post.favorites }}</span>
                </div>
              </div>

              <div class="actions-right">
                <div class="share-action" @click="sharePost(post)">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="18" cy="5" r="3"/>
                    <circle cx="6" cy="12" r="3"/>
                    <circle cx="18" cy="19" r="3"/>
                    <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/>
                    <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>
                  </svg>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-if="filteredPosts.length === 0" class="empty-state">
            <div class="empty-card">
              <div class="empty-icon">📝</div>
              <div class="empty-title">暂无帖子</div>
              <div class="empty-subtitle">快来发布第一篇帖子吧！</div>
            </div>
          </div>

          <!-- 加载更多 -->
          <div v-if="hasMorePosts && !loading" class="load-more">
            <el-button class="load-more-btn" @click="loadMorePosts">
              <span>加载更多内容</span>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M19 14l-7 7-7-7"/>
              </svg>
            </el-button>
          </div>
        </template>
      </div>
    </div>

    <!-- 博客预览对话框 -->
    <BlogPreviewDialog
        :visible="showPostDetail"
        :post="previewPost"
        title="博客预览"
        @close="showPostDetail = false"
    />

    <!-- 评论模态框 -->
    <el-dialog
        v-model="showComments"
        title="💬 评论互动"
        width="50%"
        destroy-on-close
        :append-to-body="true"
        :lock-scroll="false"
        class="comments-dialog modern-dialog"
    >
      <!-- 帖子标题 -->
      <div class="comment-post-title">{{ commentPost.title }}</div>

      <!-- 评论输入框 -->
      <div class="comment-input-container">
        <!-- 当前用户信息 -->
        <div class="comment-input-header">
          <el-avatar :size="36" :src="currentUser.avatar" class="comment-avatar"></el-avatar>
          <span class="comment-username">{{ currentUser.name }}</span>
        </div>

        <!-- 回复目标显示 -->
        <div v-if="replyTarget" class="reply-target">
          回复 <span class="reply-target-name">@{{ replyTarget.author.name }}</span>:
          <el-button size="mini" type="text" @click="cancelReply">取消</el-button>
        </div>

        <el-input
            v-model="newComment"
            type="textarea"
            :rows="replyTarget ? 2 : 3"
            placeholder="写下你的想法..."
            resize="none"
            class="comment-textarea"
        ></el-input>
        <div class="comment-input-actions">
          <el-button type="primary" @click="submitComment" :loading="commentSubmitting" class="submit-comment-btn">
            {{ replyTarget ? '回复' : '发表评论' }}
          </el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list-header">
        <span>💭 全部评论 ({{ commentPost.comments.length }})</span>
      </div>

      <div class="comment-list">
        <div v-if="commentPost.comments.length === 0" class="empty-comments">
          <div class="empty-comment-icon">💬</div>
          <div class="empty-comment-text">暂无评论，快来发表第一条评论吧！</div>
        </div>

        <div v-else class="comments-container">
          <div v-for="comment in commentPost.comments" :key="comment.id" class="comment-item">
            <!-- 评论头部 -->
            <div class="comment-item-header">
              <div class="comment-user-info">
                <el-avatar :size="32" :src="comment.author.avatar" class="comment-user-avatar"></el-avatar>
                <div class="comment-user-details">
                  <div class="comment-username">{{ comment.author.name }}</div>
                  <div class="comment-time">{{ formatTime(comment.createdAt) }}</div>
                </div>
              </div>
            </div>

            <!-- 评论内容 -->
            <div class="comment-item-content">
              {{ comment.content }}

              <!-- 显示回复目标 -->
              <div v-if="comment.replyTo" class="reply-to-info">
                回复 <span class="reply-to-name">@{{ comment.replyTo.author.name }}</span>
              </div>
            </div>

            <!-- 评论操作 -->
            <div class="comment-item-actions">
              <div class="comment-action-item" @click="toggleCommentLike(comment)" :class="{ 'active': comment.isLiked }">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
                </svg>
                <span>{{ comment.likes }}</span>
              </div>
              <div class="comment-action-item" @click="setReplyTarget(comment)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                </svg>
                <span>回复</span>
              </div>
            </div>

            <!-- 回复列表 -->
            <div v-if="comment.replies && comment.replies.length > 0" class="replies-container">
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <!-- 回复头部 -->
                <div class="reply-header">
                  <el-avatar :size="28" :src="reply.author.avatar" class="reply-avatar"></el-avatar>
                  <div class="reply-user">
                    <span class="reply-username">{{ reply.author.name }}</span>
                    <span class="reply-time">{{ formatTime(reply.createdAt) }}</span>
                  </div>
                </div>

                <!-- 回复内容 -->
                <div class="reply-content">
                  <span v-if="reply.replyTo" class="reply-to">@{{ reply.replyTo.author.name }}</span>
                  {{ reply.content }}
                </div>

                <!-- 回复操作 -->
                <div class="reply-actions">
                  <div class="reply-action" @click="toggleReplyLike(reply)" :class="{ 'active': reply.isLiked }">
                    <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
                    </svg>
                    <span>{{ reply.likes }}</span>
                  </div>
                  <div class="reply-action" @click="setReplyTarget(reply, comment)">
                    <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                    </svg>
                    <span>回复</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 收藏夹选择模态框 -->
    <el-dialog
        v-model="showFavoritesModal"
        title="💾 添加到收藏夹"
        width="480px"
        :show-close="true"
        :close-on-click-modal="false"
        :close-on-press-escape="true"
        class="favorites-modal modern-dialog"
    >
      <div class="favorites-content">
        <!-- 创建收藏夹选项 -->
        <div class="create-folder-item" @click="showCreateFolderDialog">
          <div class="folder-info">
            <div class="create-icon">➕</div>
            <span class="folder-name">创建新收藏夹</span>
          </div>
        </div>

        <!-- 收藏夹列表 -->
        <div class="favorites-list">
          <div
              v-for="folder in favoriteFolders"
              :key="folder.id"
              class="folder-item"
              :class="{ 'recently-used': folder.isRecentlyUsed }"
          >
            <div class="folder-info">
              <div class="folder-main">
                <div class="folder-name">{{ folder.name }}</div>
                <div class="folder-meta">
                  <span class="item-count">{{ folder.itemCount }}条内容</span>
                  <span class="separator">·</span>
                  <span class="privacy-status">{{ folder.isPrivate ? '🔒 私密' : '🌐 公开' }}</span>
                  <el-tag
                      v-if="folder.isRecentlyUsed"
                      type="warning"
                      size="small"
                      effect="plain"
                      class="recent-tag"
                  >
                    最近使用
                  </el-tag>
                </div>
              </div>
            </div>
            <el-button
                type="primary"
                size="small"
                plain
                class="collect-btn"
                @click="addToFolder(folder)"
                :loading="folder.loading"
            >
              收藏
            </el-button>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 创建收藏夹对话框 -->
    <el-dialog
        v-model="showCreateDialog"
        title="📁 创建收藏夹"
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
        class="create-dialog modern-dialog"
    >
      <el-form :model="newFolder" label-position="top" class="create-form">
        <el-form-item label="收藏夹名称" required>
          <el-input
              v-model="newFolder.name"
              placeholder="请输入收藏夹名称"
              maxlength="20"
              show-word-limit
              class="form-input"
          />
        </el-form-item>
        <el-form-item label="隐私设置">
          <el-radio-group v-model="newFolder.isPrivate" class="privacy-group">
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
              v-model="newFolder.description"
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
          <el-button @click="showCreateDialog = false" class="cancel-btn">取消</el-button>
          <el-button
              type="primary"
              @click="createFolder"
              :loading="createLoading"
              :disabled="!newFolder.name.trim()"
              class="create-btn"
          >
            创建收藏夹
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick, onBeforeUnmount } from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import request from '@/utils/request';
import { useStore } from "vuex";
import { fetchUserAvatar } from '@/utils/userHelper';
import * as userHelper from "@/utils/userHelper.js";
import BlogPreviewDialog from '@/components/BlogPreviewDialog.vue'
import * as postHelper from "@/utils/postHelper.js";
import defaultCover from '@/assets/imgs/default-cover.jpg'

const store = useStore();
const userId = computed(() => store.getters.userId)
const searchQuery = computed(() => store.state.searchQuery)

// 当前用户信息
const currentUser = reactive({
  id: userId.value,
  name: '',
  avatar: ''
});

// 状态变量
const loading = ref(false);
const commentSubmitting = ref(false);
const activeTab = ref('hot');
const showPostDetail = ref(false);
const showComments = ref(false);
const currentPost = ref(null);
const commentPost = ref(null);
const newComment = ref('');
const selectedTag = ref('');
const hasMorePosts = ref(true);
const page = ref(1);
const error = ref(null);
const showFavoritesModal = ref(false)
const showCreateDialog = ref(false)
const createLoading = ref(false)
const coverUrl = ref('')
const posts = ref([]);
const hotTags = ref([]);    // 热门标签
const replyTarget = ref(null); // 当前回复的目标（评论或回复）
const replyingToComment = ref(null); // 当回复回复时，所属的父评论

const fetchHotTags = async () => {
  try {
    loading.value = true;
    const res = await request.get('/tags/hot');
    if (res.code === '200') {
      console.log('Hot tags fetched:', res.data);
      hotTags.value = res.data.map(tag => ({
        id: tag.id,
        name: tag.name,
      }));
    }
  } catch (err) {
    error.value = '获取热门标签失败';
    console.error('Error fetching hot tags:', err);
  } finally {
    loading.value = false;
  }
};

// 计算属性 - 根据筛选条件过滤帖子
const filteredPosts = computed(() => {
  let result = [...posts.value];

  // 应用搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(post => {
      return (
          post.title.toLowerCase().includes(query) ||
          post.summary.toLowerCase().includes(query) ||
          (post.tags && post.tags.some(tag => tag.toLowerCase().includes(query))) ||
          post.author.name.toLowerCase().includes(query)
      )
    })
  }

  // 根据标签筛选
  if (selectedTag.value) {
    result = result.filter(post => post.tags.includes(selectedTag.value));
  }

  // 根据选项卡排序
  if (activeTab.value === 'hot') {
    // 热门排序逻辑（可保留原有算法）
    result.sort((a, b) => (b.likes + b.comments.length * 2) - (a.likes + a.comments.length * 2));
  } else if (activeTab.value === 'latest') {
    // 按时间倒序（最新在前）
    result.sort((a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    );
  } else if (activeTab.value === 'mostLiked') {
    // 按点赞数倒序
    result.sort((a, b) => b.likes - a.likes);
  }

  return result;
});

const previewPost = computed(() => {
  if (!currentPost.value) return {}

  return {
    id: currentPost.value.id,
    title: currentPost.value.title,
    author: currentPost.value.author,
    createdAt: currentPost.value.createdAt,
    category: currentPost.value.tags?.length > 0 ? currentPost.value.tags[0] : '',
    tags: currentPost.value.tags,
    coverUrl: currentPost.value.coverUrl || defaultCover,
    summary: currentPost.value.content ? currentPost.value.content.slice(0, 100) + '...' : '',
    formattedContent: currentPost.value.formattedContent
  }
})

// 生命周期钩子
onMounted(async () => {
  if (currentUser.id) {
    userHelper.fetchCurrentUserDetails(currentUser);
    // 获取当前用户详细信息 (name, avatar)
    userHelper.fetchCurrentUserDetails(currentUser)
  }
  fetchHotTags();
  fetchAndProcessPosts();
});

// 组件卸载时移除监听
onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', () => {
    store.commit('setSearchQuery', '')
  })
})

// 新增：监听 userId 的变化，以更新 currentUser 信息
watch(userId, async (newUserId, oldUserId) => {
  currentUser.id = newUserId;
  if (newUserId) {
    await fetchCurrentUserDetails();
  } else {
    currentUser.name = '访客';
    currentUser.avatar = '/placeholder.svg?height=40&width=40';
  }
}, { immediate: false });

// 方法 - 加载更多帖子
const loadMorePosts = () => {
  loading.value = true;
  page.value++;

  // 模拟API请求延迟
  setTimeout(() => {
    // 模拟没有更多数据
    if (page.value >= 3) {
      hasMorePosts.value = false;
    }
    loading.value = false;
  }, 1000);
};

// 方法 - 查看帖子详情
const viewPostDetail = (post) => {
  currentPost.value = JSON.parse(JSON.stringify(post));
  showPostDetail.value = true;
};

// 方法 - 打开评论
const openComments = (post) => {
  console.log('openComments called with post:', JSON.parse(JSON.stringify(post))); // Log the post data
  // 使用单独的变量存储评论帖子，避免与帖子详情冲突
  commentPost.value = JSON.parse(JSON.stringify(post));
  console.log('commentPost.value after assignment:', commentPost.value); // Log the assigned commentPost
  showComments.value = true;
  console.log('showComments.value after setting to true:', showComments.value); // Log the state of showComments
};

// 方法 - 点赞帖子
const toggleLike = async (post) => {
  const originalIsLiked = post.isLiked;
  const originalLikes = post.likes;

  post.isLiked = !post.isLiked;
  post.likes += post.isLiked ? 1 : -1;

  try {
    // 根据状态选择请求方法
    const response = originalIsLiked
        ? await request.delete('/likes', {
          params: {
            userId: currentUser.id,
            postId: post.id
          }
        })
        : await request.post('/likes', null, {
          params: {
            userId: currentUser.id,
            postId: post.id
          }
        });

    if (response.code === '200') {
      if (originalIsLiked) {
        ElMessage.success('取消点赞成功');
      } else {
        ElMessage.success('点赞成功');
      }
    } else {
      post.isLiked = originalIsLiked;
      post.likes = originalLikes;
      ElMessage.error(response.msg || '操作失败');
    }
  } catch (error) {
    post.isLiked = originalIsLiked;
    post.likes = originalLikes;
    ElMessage.error('请求失败，请检查网络');
    console.error('API请求异常:', error);
  }
};

// 方法 - 点赞评论
const toggleCommentLike = async (comment) => {
  try {
    // 保存原始状态用于错误回滚
    const originalIsLiked = comment.isLiked
    const originalLikes = comment.likes
    
    // 立即更新UI状态
    comment.isLiked = !comment.isLiked
    comment.likes += comment.isLiked ? 1 : -1

    // 调用后端接口
    const response = comment.isLiked 
      ? await request.post('/comments/likes', null, {
          params: {
            commentId: comment.id,
            userId: currentUser.id
          }
        })
      : await request.delete('/comments/likes', {
          params: {
            commentId: comment.id,
            userId: currentUser.id
          }
        })

    if (response.code !== '200') {
      // 回滚状态
      comment.isLiked = originalIsLiked
      comment.likes = originalLikes
      ElMessage.error(response.message || '操作失败')
    } else {
      ElMessage.success(comment.isLiked ? '点赞成功' : '取消点赞成功')
    }
  } catch (error) {
    // 请求失败回滚状态
    comment.isLiked = originalIsLiked
    comment.likes = originalLikes
    console.error('点赞操作失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  }
}

// 设置回复目标
const setReplyTarget = (target, parentComment = null) => {
  replyTarget.value = target;
  replyingToComment.value = parentComment;

  // 滚动到输入框
  nextTick(() => {
    const textarea = document.querySelector('.comment-textarea textarea');
    if (textarea) {
      textarea.focus();
      textarea.scrollIntoView({ behavior: 'smooth' });
    }
  });
};

// 取消回复
const cancelReply = () => {
  replyTarget.value = null;
  replyingToComment.value = null;
};

// 提交评论/回复
const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }

  commentSubmitting.value = true;

  try {
    const isReply = !!replyTarget.value;
    const commentData = {
      postId: commentPost.value.id,
      userId: currentUser.id,
      content: newComment.value.trim(),
      parentId: isReply ? replyTarget.value.id : null,
      replyTo: isReply ? replyTarget.value.userId : null,
      createdAt: new Date().toISOString()
    };

    const res = await request.post('/comment/add', commentData);

    if (res.code === '200') {
      const newCommentObj = {
        ...res.data,
        author: {
          name: currentUser.username,
          avatar: currentUser.avatar
        },
        likes: 0,
        isLiked: false,
        replies: [] // 初始化回复数组
      };

      // 如果是回复
      if (isReply) {
        // 如果是回复评论
        if (!replyingToComment.value) {
          // 找到被回复的评论
          const targetComment = commentPost.value.comments.find(c => c.id === replyTarget.value.id);
          if (targetComment) {
            // 初始化replies数组如果不存在
            if (!targetComment.replies) targetComment.replies = [];
            targetComment.replies.push(newCommentObj);
          }
        }
        // 如果是回复回复
        else {
          // 找到父评论
          const parentComment = commentPost.value.comments.find(c => c.id === replyingToComment.value.id);
          if (parentComment) {
            // 初始化replies数组如果不存在
            if (!parentComment.replies) parentComment.replies = [];

            // 找到被回复的回复
            const targetReply = parentComment.replies.find(r => r.id === replyTarget.value.id);
            if (targetReply) {
              // 初始化replies数组如果不存在
              if (!targetReply.replies) targetReply.replies = [];
              targetReply.replies.push(newCommentObj);
            } else {
              // 直接添加到父评论的回复列表
              parentComment.replies.push(newCommentObj);
            }
          }
        }
      }
      // 如果是顶级评论
      else {
        commentPost.value.comments.unshift(newCommentObj);
      }

      // 同步到主帖子列表
      const originalPost = posts.value.find(p => p.id === commentPost.value.id);
      if (originalPost) {
        // 使用相同的逻辑更新原始帖子
        if (isReply) {
          if (!replyingToComment.value) {
            const targetComment = originalPost.comments.find(c => c.id === replyTarget.value.id);
            if (targetComment) {
              if (!targetComment.replies) targetComment.replies = [];
              targetComment.replies.push(newCommentObj);
            }
          } else {
            const parentComment = originalPost.comments.find(c => c.id === replyingToComment.value.id);
            if (parentComment) {
              if (!parentComment.replies) parentComment.replies = [];

              const targetReply = parentComment.replies.find(r => r.id === replyTarget.value.id);
              if (targetReply) {
                if (!targetReply.replies) targetReply.replies = [];
                targetReply.replies.push(newCommentObj);
              } else {
                parentComment.replies.push(newCommentObj);
              }
            }
          }
        } else {
          originalPost.comments.unshift(newCommentObj);
        }
      }

      // 重置状态
      newComment.value = '';
      cancelReply();
      ElMessage.success(isReply ? '回复成功' : '评论成功');
    } else {
      ElMessage.error(`操作失败：${res.msg}`);
    }
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error(`请求异常：${error.message}`);
  } finally {
    commentSubmitting.value = false;
  }
};

const toggleReplyInput = (comment) => {
  // 实现显示/隐藏回复输入框逻辑
  comment.showReply = !comment.showReply;
  // 自动聚焦到输入框
  nextTick(() => {
    if (comment.showReply) {
      document.querySelector(`#reply-input-${comment.id}`)?.focus();
    }
  });
};

// 修改获取评论的方法，支持嵌套回复
const fetchComments = async (postId) => {
  try {
    const res = await request.get('/comment/post', {
      params: { postId }
    });

    if (res.code === '200') {
      // 创建评论映射和父评论数组
      const commentMap = new Map();
      const parentComments = [];
      const repliesMap = new Map();

      // 首先处理所有评论，建立映射
      res.data.forEach(comment => {
        commentMap.set(comment.id, comment);
        // 初始化回复数组
        comment.replies = [];

        if (comment.parentId === null) {
          parentComments.push(comment);
        }
      });

      // 将回复关联到父评论
      res.data.forEach(comment => {
        if (comment.parentId !== null) {
          const parentComment = commentMap.get(comment.parentId);
          if (parentComment) {
            // 设置回复目标作者信息
            comment.replyToAuthor = parentComment.author;
            parentComment.replies.push(comment);
          }
        }
      });

      return parentComments;
    }
  } catch (error) {
    console.error('获取评论失败:', error);
  }
  return [];
};

// 方法 - 根据标签筛选
const filterByTag = (tag) => {
  selectedTag.value = tag;
};

// 新增分享方法
const sharePost = (post) => {
    if (navigator.share) {
      navigator.share({
        title: post.title,
        text: post.summary,
        url: `${window.location.origin}/blog-detail?postId=${post.id}`
      })
    } else {
      navigator.clipboard.writeText(`${window.location.origin}/blog-detail?postId=${post.id}`)
      ElMessage.success('链接已复制到剪贴板')
    }
  }

// 方法 - 格式化时间
const formatTime = (dateInput) => {
  let date = dateInput;

  // 如果 dateInput 不是 Date 对象实例，并且是字符串类型，则尝试转换
  if (!(dateInput instanceof Date) && typeof dateInput === 'string') {
    date = new Date(dateInput);
  } else if (dateInput instanceof Date) {
    date = dateInput;
  } else {
    // 如果输入既不是Date实例也不是有效日期字符串，则标记为无效
    console.error('Invalid date input to formatTime:', dateInput);
    return '日期无效';
  }

  // 确保转换后或原始的date是一个有效的Date对象
  if (!(date instanceof Date) || isNaN(date.getTime())) {
    console.error('formatTime could not parse date:', dateInput);
    return '日期解析失败';
  }

  const now = new Date();
  const diff = now.getTime() - date.getTime();

  // 小于1分钟
  if (diff < 60000) {
    return '刚刚';
  }

  // 小于1小时
  if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`;
  }

  // 小于1天
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`;
  }

  // 小于1周
  if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`;
  }

  // 格式化日期
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
};

// 从后端获取和处理帖子数据
const fetchAndProcessPosts = async () => {
  loading.value = true;
  error.value = null;
  try {
    const res = await request.get('/posts/selectAll');
    if (res.code === '200' && Array.isArray(res.data)) {
      console.log('从后端获取的帖子数据:', res.data);
      posts.value = await Promise.all(
          res.data.map(async (postData) => {
            let authorName = '未知用户';
            let authorAvatar = '/placeholder.svg?height=40&width=40';

            if (postData.userId) {
              try {
                const userRes = await request.post(`/user/getUser`, null, {
                  params: { userId: postData.userId }
                });
                if (userRes.code === '200' && userRes.data) {
                  authorName = userRes.data.username || '未知用户';
                }
                authorAvatar = await fetchUserAvatar(postData.userId);

                const likeRes = await request.get(`/likes/count/${postData.id}`)
                if (likeRes.code === '200') {
                  postData.likes = likeRes.data;
                }

                const isLikedRes = await request.get(`/likes/check`, {
                  params: {
                    postId: postData.id,
                    userId: postData.userId,
                  }
                })
                if (isLikedRes.code === '200') {
                  postData.isLiked = isLikedRes.data;
                }

                const isFavoritedRes = await request.get('/favorites/check', {
                  params: {
                    postId: postData.id,
                    userId: userId.value // 当前登录用户ID
                  }
                });
                if (isFavoritedRes.code === '200') {
                  postData.isFavorited = isFavoritedRes.data;
                }

                const favoritesRes = await request.get(`/favorites/count`, {
                  params: {
                    postId: postData.id,
                  }
                });
                if (favoritesRes.code === '200') {
                  postData.favorites = favoritesRes.data;
                }

                const followCheckRes = await request.get('/follow/check', {
                  params: {
                    followerId: currentUser.id,
                    followedId: postData.userId,
                  }
                })
                if (followCheckRes.code === '200') {
                  postData.isFollowed = followCheckRes.data;
                }

                const commentRes = await request.get('/comment/post', {
                  params: {
                    postId: postData.id,
                  }
                })
                if (commentRes.code === '200') {
                  // 并行获取评论作者信息
                  postData.comments = await Promise.all(
                    commentRes.data.map(async comment => {
                      try {
                        const [userRes, likesRes, isLikedRes] = await Promise.all([
                          request.post(`/user/getUser`, null, {
                            params: { userId: comment.userId }
                          }),
                          request.get(`/comments/likes/count/${comment.id}`),
                          request.get(`/comments/likes/check`, {
                            params: {
                              commentId: comment.id,
                              userId: currentUser.id
                            }
                          })
                        ]);

                        return {
                          ...comment,
                          likes: likesRes.data || 0,
                          isLiked: isLikedRes.data || false,
                          author: {
                            name: userRes.data.username || '未知用户',
                            avatar: await fetchUserAvatar(comment.userId)
                          }
                        };
                      } catch (e) {
                        console.error(`获取评论${comment.id}相关信息失败:`, e);
                        return {
                          ...comment,
                          likes: 0,
                          isLiked: false,
                          author: {
                            name: '未知用户',
                            avatar: '/placeholder.svg?height=32&width=32'
                          }
                        };
                      }
                  })
                );
                }
              } catch (userError) {
                console.error(`获取用户 ${postData.userId} 信息失败:`, userError);
              }
            }

            // 确保 images 和 comments 字段存在且为数组
            const comments = Array.isArray(postData.comments) ? postData.comments.map(comment => ({
              ...comment,
              author: comment.author || { name: '未知用户', avatar: '/placeholder.svg?height=32&width=32' }, // 确保评论作者信息存在
              createdAt: comment.createdAt || new Date().toISOString(), // 确保评论时间存在
              likes: comment.likes || 0,
              isLiked: comment.isLiked || false,
            })) : [];
            const tagRes = await request.get('/post-tags/name', {
              params: { postId: postData.id }
            })
            coverUrl.value = await postHelper.fetchCover(postData.userId, postData.id);

            return {
              id: postData.id,
              title: postData.title,
              summary: postData.summary,
              content: postData.content,
              formattedContent: postData.formattedContent,
              views: postData.views || 0,
              author: {
                id: postData.userId,
                name: authorName,
                avatar: authorAvatar,
                isFollowed: postData.isFollowed,
                followLoading: false
              },
              coverUrl: coverUrl.value,
              createdAt: postData.createdAt || new Date().toISOString(), // 后端应提供标准日期字符串
              tags: tagRes.data || [],
              likes: postData.likes || 0,
              isLiked: postData.isLiked || false,
              favorites: postData.favorites || 0,
              isFavorited: postData.isFavorited || false,
              comments: comments,
            };
          })
      );
      console.log('处理后的帖子数据:', posts.value); // 调试输出
      hasMorePosts.value = false;
    } else if (res.code !== '200') {
      throw new Error(res.msg || '获取帖子失败');
    } else {
      posts.value = []; // 如果 res.data 不是数组，则设置为空
      hasMorePosts.value = false;
    }
  } catch (err) {
    console.error('获取社区帖子失败:', err);
    error.value = `无法加载帖子：${err.message || '请稍后重试'}`;
    posts.value = []; // 出错时清空帖子
    hasMorePosts.value = false;
  } finally {
    loading.value = false;
  }
};

// 关注/取消关注用户
const toggleFollow = async (author) => {
  if (!currentUser.id) {
    ElMessage.warning('请先登录');
    return;
  }

  const originalIsFollowed = author.isFollowed;
  author.followLoading = true;

  try {
    const response = originalIsFollowed
        ? await request.delete('/follow/unfollow', {
          params: {
            followerId: currentUser.id,
            followedId: author.id
          }
        })
        : await request.post('/follow/add', null, {
          params: {
            followerId: currentUser.id,
            followedId: author.id
          }
        });

    if (response.code === '200') {
      author.isFollowed = !originalIsFollowed;

      // 同步更新所有相关帖子的关注状态
      posts.value.forEach(post => {
        if (post.author.id === author.id) {
          post.author.isFollowed = author.isFollowed;
        }
      });

      // 同步更新当前帖子详情的关注状态
      if (currentPost.value && currentPost.value.author.id === author.id) {
        currentPost.value.author.isFollowed = author.isFollowed;
      }

      ElMessage.success(author.isFollowed ? '关注成功' : '取消关注成功');
    } else {
      ElMessage.error(response.msg || '操作失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络');
    console.error('关注操作失败:', error);
  } finally {
    author.followLoading = false;
  }
};

// 收藏夹数据
const favoriteFolders = ref([])

// 新建收藏夹表单
const newFolder = reactive({
  name: '',
  isPrivate: false,
  description: ''
})

// 获取收藏夹列表
const fetchFavoriteFolders = async () => {
  try {
    const res = await request.get('/favorites/collections/list', {
      params: { userId: userId.value }
    })

    if (res.code === '200') {
      // 获取基础收藏夹列表
      const baseFolders = res.data;
      console.log('获取到的收藏夹列表:', baseFolders);

      // 并行请求每个收藏夹的帖子数量
      const foldersWithCount = await Promise.all(
          baseFolders.map(async item => {
            try {
              const countRes = await request.get(`/favorites/collection/count`, {
                params: {
                  collectionId: item.id,
                }
              });
              const collectionRes = await request.get('/favorites/collections', {
                params: {
                  collectionId: item.id
                }
              })
              console.log("collectionRes:", collectionRes)
              return {
                ...item,
                itemCount: countRes.data || 0,
                isPrivate: !collectionRes.data.public,
                lastUsedTime: collectionRes.data.updatedAt || 0
              };
            } catch (e) {
              console.error(`获取收藏夹${item.id}数量失败:`, e);
              return {
                ...item,
                itemCount: 0
              };
            }
          })
      );

      favoriteFolders.value = foldersWithCount.map(item => ({
        id: item.id,
        name: item.name,
        itemCount: item.itemCount,
        isPrivate: item.isPrivate,
        isRecentlyUsed: item.lastUsedTime > Date.now() - 7 * 86400000
      }));

      console.log('处理后的收藏夹列表:', favoriteFolders.value);
    }
  } catch (error) {
    ElMessage.error('获取收藏夹列表失败');
    console.error('API请求失败:', error);
  }
};

// 打开收藏夹模态框
const openFavoritesModal = async (post) => {
  currentPost.value = post;

  // 先检查收藏状态
  if (currentPost.value.isFavorited) {
    try {
      await ElMessageBox.confirm(
          '确定要取消收藏吗？',
          '提示',
          { type: 'warning' }
      );

      // 执行取消收藏逻辑
      const res = await request.delete('/favorites/remove', {
        params: {
          postId: currentPost.value.id,
          userId: userId.value
        }
      });

      if (res.code === '200') {
        currentPost.value.favorites--;
        currentPost.value.isFavorited = false;
        ElMessage.success('已取消收藏');
      }
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('取消收藏失败：' + error.message);
      }
    }
    return;
  }

  // 未收藏时加载收藏夹
  try {
    await fetchFavoriteFolders();
    showFavoritesModal.value = true;
  } catch (error) {
    ElMessage.error('无法加载收藏夹：' + error.message);
  }
}

// 显示创建收藏夹对话框
const showCreateFolderDialog = () => {
  showCreateDialog.value = true
}

// 添加到收藏夹
const addToFolder = async (folder) => {
  if (!currentPost.value) return

  // 原来没收藏，再次点击则收藏
  folder.loading = true
  try {
    const res = await request.post('/favorites/add', {
      collectionId: folder.id,
      postId: currentPost.value.id,
      userId: userId.value,
      createdAt: new Date().toISOString()
    });

    if (res.code === '200') {
      // 更新本地数据
      folder.itemCount++
      currentPost.value.favorites++
      currentPost.value.isFavorited = true

      ElMessage.success(`已收藏到"${folder.name}"`);
      showFavoritesModal.value = false;
    } else {
      ElMessage.error('收藏失败：' + res.msg);
    }
  } catch (error) {
    ElMessage.error('请求异常：' + error.message);
  } finally {
    folder.loading = false;
  }
}

// 创建收藏夹
const createFolder = async () => {
  if (!newFolder.name.trim()) {
    ElMessage.warning('请输入收藏夹名称')
    return
  }

  createLoading.value = true
  console.log('公开：', typeof(!newFolder.isPrivate))

  try {
    const res = await request.post('/favorites/collection', null, {
      params: {
        name: newFolder.name,
        description: newFolder.description,
        isPublic: !newFolder.isPrivate,
        userId: userId.value
      }
    })

    // 更新收藏夹列表
    if (res.code == '200') {
      favoriteFolders.value.unshift({
        id: res.data.id,
        name: res.data.name,
        itemCount: 0,
        isPrivate: !res.data.isPublic,
        isRecentlyUsed: true,
        loading: false
      })
      // 重置表单
      newFolder.name = ''
      newFolder.isPrivate = false
      newFolder.description = ''

      showCreateDialog.value = false
      ElMessage.success('收藏夹创建成功')
    }
  } catch (error) {
    console.error('创建失败:', error)
    ElMessage.error(error.response?.data?.message || '创建失败，请重试')
  } finally {
    createLoading.value = false
  }
}
</script>

<style scoped>
/* 全局容器 */
.community-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

/* 头部区域 */
.community-header {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.modern-tabs :deep(.el-tabs__header) {
  margin: 0;
  border: none;
}

.modern-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.modern-tabs :deep(.el-tabs__item) {
  padding: 12px 24px;
  font-weight: 500;
  border-radius: 12px;
  margin-right: 8px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.modern-tabs :deep(.el-tabs__item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: transparent;
}

.modern-tabs :deep(.el-tabs__active-bar) {
  display: none;
}

/* 标签区域 */
.hot-tags {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.tag-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tag-title {
  font-weight: 600;
  font-size: 16px;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.modern-tag {
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #e9ecef;
  background: #f8f9fa;
  font-weight: 500;
}

.modern-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.active-tag {
  background: linear-gradient(135deg, #ff6b6b, #ee5a24) !important;
  color: white !important;
  border-color: transparent !important;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
}

/* 帖子容器 */
.posts-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 现代化帖子卡片 */
.modern-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  position: relative;
  overflow: hidden;
}

.modern-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.modern-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.modern-card:hover::before {
  opacity: 1;
}

/* 帖子头部 */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-container {
  position: relative;
}

.user-avatar {
  border: 3px solid #f8f9fa;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.1);
}

.avatar-fallback {
  font-size: 20px;
}

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 12px;
  height: 12px;
  background: #52c41a;
  border: 2px solid white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 600;
  font-size: 15px;
  color: #2c3e50;
}

.post-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6c757d;
}

.time-icon {
  width: 14px;
  height: 14px;
}

.follow-btn {
  border-radius: 20px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.follow-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 帖子内容 */
.post-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 16px;
  cursor: pointer;
  color: #2c3e50;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.post-title:hover {
  color: #667eea;
}

.post-content {
  margin-bottom: 20px;
  font-size: 15px;
  line-height: 1.7;
  color: #495057;
  cursor: pointer;
  transition: color 0.3s ease;
}

.post-content:hover {
  color: #2c3e50;
}

/* 图片网格 */
.post-images {
  display: grid;
  gap: 12px;
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
}

.image-single {
  grid-template-columns: 1fr;
}

.image-grid {
  grid-template-columns: repeat(2, 1fr);
}

.image-wrapper {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 16/9;
}

.post-image {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.post-image:hover {
  transform: scale(1.05);
}

.more-images {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
}

/* 标签样式 */
.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.post-tag {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 16px;
  padding: 4px 12px;
  font-size: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.post-tag:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

/* 操作按钮区域 */
.post-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.actions-left {
  display: flex;
  gap: 24px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #6c757d;
  transition: all 0.3s ease;
  padding: 8px 12px;
  border-radius: 20px;
  user-select: none;
}

.action-item:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.action-item.active {
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
}

.action-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon svg {
  width: 18px;
  height: 18px;
  transition: transform 0.3s ease;
}

.action-item:hover .action-icon svg {
  transform: scale(1.2);
}

.action-text {
  font-weight: 500;
  font-size: 14px;
}

.share-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  color: #6c757d;
  transition: all 0.3s ease;
  cursor: pointer;
}

.share-action:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: scale(1.1);
}

.share-action svg {
  width: 18px;
  height: 18px;
}

/* 加载状态 */
.loading-container {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.loading-skeleton {
  padding: 16px;
}

/* 错误状态 */
.error-container {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

.error-card {
  background: white;
  border-radius: 16px;
  padding: 40px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-message {
  color: #6c757d;
  margin-bottom: 24px;
  line-height: 1.6;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

.empty-card {
  background: white;
  border-radius: 20px;
  padding: 60px 40px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.empty-subtitle {
  color: #6c757d;
  line-height: 1.6;
}

/* 加载更多按钮 */
.load-more {
  text-align: center;
  margin-top: 32px;
}

.load-more-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 25px;
  padding: 12px 32px;
  color: white;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.load-more-btn svg {
  width: 16px;
  height: 16px;
}

/* 对话框样式 */
.modern-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.modern-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 20px 24px;
  margin: 0;
}

.modern-dialog :deep(.el-dialog__title) {
  font-weight: 600;
  font-size: 18px;
}

.modern-dialog :deep(.el-dialog__body) {
  padding: 0;
}

/* 评论对话框样式 */
.comments-dialog :deep(.el-dialog) {
  max-width: 800px;
}

.comment-post-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: #f8f9fa;
  color: #2c3e50;
  border-bottom: 1px solid #e9ecef;
}

.comment-input-container {
  margin: 24px;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 16px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.comment-input-container:focus-within {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.comment-input-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.comment-avatar {
  border: 2px solid #667eea;
}

.comment-username {
  font-weight: 600;
  color: #2c3e50;
}

.comment-textarea :deep(.el-textarea__inner) {
  border: none;
  background: white;
  border-radius: 12px;
  padding: 16px;
  font-size: 15px;
  line-height: 1.6;
  resize: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.comment-textarea :deep(.el-textarea__inner):focus {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.2);
}

.comment-input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.submit-comment-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.submit-comment-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.comment-list-header {
  font-size: 16px;
  font-weight: 600;
  margin: 0 24px 20px;
  color: #2c3e50;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.comment-list {
  max-height: 500px;
  overflow-y: auto;
  padding: 0 24px 24px;
}

.comments-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  background: white;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.comment-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.comment-item-header {
  margin-bottom: 12px;
}

.comment-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.comment-user-avatar {
  border: 2px solid #f8f9fa;
}

.comment-user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-username {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: #6c757d;
}

.comment-item-content {
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 16px;
  color: #495057;
}

.comment-item-actions {
  display: flex;
  gap: 16px;
}

.comment-action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #6c757d;
  transition: all 0.3s ease;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

.comment-action-item:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.comment-action-item.active {
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
}

.comment-action-item svg {
  width: 16px;
  height: 16px;
}

.empty-comments {
  text-align: center;
  padding: 60px 20px;
}

.empty-comment-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-comment-text {
  color: #6c757d;
  font-size: 16px;
  line-height: 1.6;
}

.reply-target {
  background: #f0f7ff;
  border-left: 3px solid #409eff;
  padding: 8px 12px;
  margin-bottom: 10px;
  border-radius: 4px;
  font-size: 13px;
  display: flex;
  align-items: center;
}

.reply-target-name {
  font-weight: 600;
  color: #409eff;
  margin: 0 5px;
}

/* 回复信息 */
.reply-to-info {
  background: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
  margin-top: 8px;
  font-size: 13px;
  display: inline-block;
}

.reply-to-name {
  font-weight: 600;
  color: #409eff;
}

/* 回复容器 */
.replies-container {
  margin-top: 15px;
  padding-left: 40px;
  border-left: 2px solid #f0f2f5;
}

.reply-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
}

.reply-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.reply-avatar {
  margin-right: 10px;
}

.reply-user {
  display: flex;
  flex-direction: column;
}

.reply-username {
  font-weight: 600;
  font-size: 13px;
}

.reply-time {
  font-size: 11px;
  color: #909399;
}

.reply-content {
  font-size: 14px;
  line-height: 1.5;
}

.reply-to {
  color: #409eff;
  font-weight: 500;
  margin-right: 5px;
}

.reply-actions {
  display: flex;
  margin-top: 8px;
  font-size: 12px;
}

.reply-action {
  display: flex;
  align-items: center;
  margin-right: 15px;
  color: #606266;
  cursor: pointer;
  transition: color 0.2s;
}

.reply-action:hover {
  color: #409eff;
}

.reply-action.active {
  color: #ff6b6b;
}

.reply-action svg {
  margin-right: 4px;
}

/* 收藏夹模态框样式 */
.favorites-modal :deep(.el-dialog) {
  max-width: 500px;
}

.favorites-content {
  max-height: 400px;
  overflow-y: auto;
}

.create-folder-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.create-folder-item:hover {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
}

.create-folder-item .folder-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.create-icon {
  font-size: 20px;
  color: #667eea;
}

.folder-name {
  font-size: 15px;
  font-weight: 600;
  color: #667eea;
}

.favorites-list {
  padding: 0;
}

.folder-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.folder-item:hover {
  background: #f8f9fa;
}

.folder-item.recently-used {
  background: linear-gradient(135deg, #fff7e6, #fef3e2);
  border-left: 4px solid #faad14;
}

.folder-info {
  flex: 1;
}

.folder-main .folder-name {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 6px;
}

.folder-meta {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #6c757d;
  gap: 6px;
}

.separator {
  color: #dee2e6;
}

.recent-tag {
  margin-left: 8px;
}

.item-count, .privacy-status {
  color: #6c757d;
}

.collect-btn {
  border-radius: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.collect-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 创建收藏夹对话框 */
.create-dialog :deep(.el-dialog) {
  max-width: 450px;
}

.create-form {
  padding: 20px 0;
}

.form-input :deep(.el-input__inner),
.form-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #e9ecef;
  padding: 12px 16px;
  transition: all 0.3s ease;
}

.form-input :deep(.el-input__inner):focus,
.form-textarea :deep(.el-textarea__inner):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

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
}

.privacy-option:hover {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.privacy-option.is-checked {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
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
  gap: 12px;
}

.privacy-title {
  font-weight: 600;
  color: #2c3e50;
}

.privacy-desc {
  font-size: 13px;
  color: #6c757d;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 0 0;
}

.cancel-btn {
  border-radius: 20px;
  padding: 10px 24px;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  border-color: #6c757d;
  color: #6c757d;
}

.create-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 20px;
  padding: 10px 24px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .community-container {
    padding: 16px;
  }

  .modern-card {
    padding: 20px;
    border-radius: 16px;
  }

  .post-images.image-grid {
    grid-template-columns: 1fr;
  }

  .actions-left {
    gap: 16px;
  }

  .action-item {
    padding: 6px 8px;
  }

  .action-text {
    font-size: 13px;
  }

  .tags-container {
    gap: 8px;
  }

  .modern-dialog :deep(.el-dialog) {
    width: 95% !important;
    margin: 5vh auto;
  }

  .comment-input-container,
  .comment-list {
    margin: 16px;
    padding: 16px;
  }

  .folder-item,
  .create-folder-item {
    padding: 16px 20px;
  }
}

@media (max-width: 576px) {
  .user-info {
    gap: 8px;
  }

  .user-avatar {
    width: 36px !important;
    height: 36px !important;
  }

  .post-title {
    font-size: 18px;
  }

  .post-content {
    font-size: 14px;
  }

  .post-actions {
    flex-direction: column;
    gap: 12px;
  }

  .actions-left {
    justify-content: space-around;
    width: 100%;
  }

  .tag-section {
    gap: 12px;
  }

  .tags-container {
    gap: 6px;
  }

  .modern-tag {
    padding: 6px 12px;
    font-size: 12px;
  }
}

/* 滚动条样式 */
.comment-list::-webkit-scrollbar,
.favorites-content::-webkit-scrollbar {
  width: 6px;
}

.comment-list::-webkit-scrollbar-track,
.favorites-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.comment-list::-webkit-scrollbar-thumb,
.favorites-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.comment-list::-webkit-scrollbar-thumb:hover,
.favorites-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modern-card {
  animation: fadeInUp 0.6s ease-out;
}

.modern-card:nth-child(2) { animation-delay: 0.1s; }
.modern-card:nth-child(3) { animation-delay: 0.2s; }
.modern-card:nth-child(4) { animation-delay: 0.3s; }
.modern-card:nth-child(5) { animation-delay: 0.4s; }
</style>