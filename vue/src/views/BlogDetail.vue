<template>
  <div v-if="loading" class="loading-container">
    加载中...
  </div>
  <div class="blog-detail-container">
    <!-- 返回按钮 -->
    <div class="back-navigation">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="m12 19-7-7 7-7"/>
          <path d="m19 12H5"/>
        </svg>
        返回
      </button>
    </div>

    <!-- 博客主体内容 -->
    <article class="blog-article">
      <!-- 博客头部信息 -->
      <header class="blog-header">
        <h1 class="blog-title">{{ blog.title || '无标题' }}</h1>

        <div class="blog-meta">
          <div class="author-info">
            <img :src="blog.authorAvatar || '/placeholder.svg?height=40&width=40'"
                 :alt="blog.authorName"
                 class="author-avatar">
            <div class="author-details">
              <span class="author-name">{{ blog.authorName }}</span>
              <div class="publish-info">
                <span class="publish-date">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                    <line x1="16" y1="2" x2="16" y2="6"/>
                    <line x1="8" y1="2" x2="8" y2="6"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                  {{ formatDate(blog.publishedAt) }}
                </span>
                <span class="read-count">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                  </svg>
                  {{ blog.views }} 阅读
                </span>
              </div>
            </div>
          </div>

          <div class="blog-actions">
            <button class="action-btn follow-btn" :class="{ 'followed': isFollowed }" @click="toggleFollow(blog.author)">
              <svg v-if="!isFollowed" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <line x1="19" y1="8" x2="19" y2="14"/>
                <line x1="22" y1="11" x2="16" y2="11"/>
              </svg>
              <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <path d="m17 11 2 2 4-4"/>
              </svg>
              {{ isFollowed ? '已关注' : '关注' }}
            </button>
          </div>
        </div>

        <!-- 分类和标签 -->
        <div class="blog-taxonomy">
          <div class="category" v-if="blog.category">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
            </svg>
            {{ blog.category }}
          </div>
          <div class="tags" v-if="blog.tags && blog.tags.length > 0">
            <span v-for="tag in blog.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </div>
      </header>

      <!-- 封面图片 -->
      <div class="blog-cover" v-if="blog.coverUrl">
        <img :src="blog.coverUrl" :alt="blog.title" />
      </div>

      <!-- 博客摘要 -->
      <div class="blog-summary" v-if="blog.summary">
        <p>{{ blog.summary }}</p>
      </div>

      <!-- 博客正文 -->
      <div class="blog-content">
        <div v-if="blog.formattedContent" v-html="blog.formattedContent"></div>
        <p v-else class="empty-content">暂无内容</p>
      </div>

      <!-- 互动区域 -->
      <div class="blog-interactions">
        <div class="interaction-buttons">
          <button class="interaction-btn like-btn" :class="{ 'liked': isLiked }" @click="toggleLike">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
            <span>{{ blog.likes }}</span>
          </button>

          <button class="interaction-btn favorite-btn" :class="{ 'favorited': isFavorited }" @click="toggleFavorite">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="m19 21-7-4-7 4V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16z"/>
            </svg>
            <span>{{ blog.favorites }}</span>
          </button>

          <button class="interaction-btn share-btn" @click="shareBlog">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="18" cy="5" r="3"/>
              <circle cx="6" cy="12" r="3"/>
              <circle cx="18" cy="19" r="3"/>
              <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/>
              <line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>
            </svg>
            <span>分享</span>
          </button>
        </div>
      </div>
    </article>

    <!-- 评论区域 -->
    <section class="comments-section">
      <div class="comments-header">
        <h3>评论 ({{ comments.length }})</h3>
      </div>

      <!-- 评论输入框 -->
      <div class="comment-input-area">
        <div class="comment-form">
          <img :src="currentUser.avatar || ''"
               :alt="currentUser.name"
               class="user-avatar">
          <div class="input-container">
            <textarea
                v-model="newComment"
                placeholder="写下你的评论..."
                class="comment-textarea"
                rows="3"
            ></textarea>
            <div class="comment-actions">
              <button class="submit-btn" @click="submitComment" :disabled="!newComment.trim()">
                发布评论
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <img :src="comment.author?.avatar || '/placeholder.svg?height=36&width=36'"
               :alt="comment.author?.name"
               class="comment-avatar">
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.author?.name || '未知用户' }}</span>
              <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <p class="comment-text">{{ comment.content }}</p>
            <div class="comment-actions">
              <button class="comment-action-btn" @click="toggleCommentLike(comment)">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M7 10v12l4-4 4 4V10"/>
                  <path d="M5 6h14l-1 4H6z"/>
                </svg>
                {{ comment.likes }}
              </button>
            </div>
            <hr>
          </div>
        </div>
      </div>
    </section>

    <!-- 相关推荐 -->
    <section class="related-posts">
      <h3>相关推荐</h3>
      <div v-if="relatedPostsLoading" class="loading-related">
        <svg class="spinner" viewBox="0 0 50 50">
          <circle cx="25" cy="25" r="20" fill="none" stroke-width="5"></circle>
        </svg>
        加载中...
      </div>
      <div v-else-if="relatedPosts.length > 0" class="related-posts-grid">
        <div v-for="post in relatedPosts" :key="post.id" class="related-post-item" @click="navigateToPost(post.id)">
          <img :src="post.coverUrl"
               :alt="post.title"
               class="related-post-cover">
          <div class="related-post-info">
            <h4 class="related-post-title">{{ post.title }}</h4>
            <div class="related-post-meta">
              <span>{{ post.authorName }}</span>
              <span>{{ post.views }} 阅读</span>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-related-posts">
        <svg>...</svg>
        <p>暂无相关推荐</p>
      </div>
    </section>

    <FavoritesManager
        v-model:visible="showFavoritesManager"
        :post-id="blog.id"
        :user-id="userId"
        @favorited="handleFavorited"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from "vuex";
import {ElMessage, ElMessageBox} from "element-plus";
import request from "@/utils/request";
import * as postHelper from "@/utils/postHelper";
import * as userHelper from '@/utils/userHelper'
import * as interactionHelper from '@/utils/interactionHelper'
import FavoritesManager from '@/components/FavoritesManager.vue'
import defaultCover from '@/assets/imgs/default-cover.jpg'

const showFavoritesManager = ref(false)

const store = useStore();
const userId = computed(() => store.getters.userId)

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const categories = ref([])
const blog = ref({
  id: null,
  title: '',
  content: '',
  formattedContent: '',
  summary: '',
  categoryId: '',
  coverUrl: defaultCover,
  allowComments: true,
  isPublic: true,
  views: 0,
  likes: 0,
  collections: 0,
  authorName: '',
  authorAvatar: '',
  createdAt: null,
  updatedAt: null,
  publishedAt: null,
  tags: [],
  category: '',
  author: {
    id: null,
    name: '',
    avatar: '',
    isFollowed: false,
  }
})
const isLiked = ref(false)
const isFavorited = ref(false)
const isFollowed = ref(false)
const currentUser = reactive({
  id: userId.value,
  name: '',
  avatar: ''
});
const newComment = ref('')
const comments = ref([])

const fetchRelatedPosts = async (postId, limit = 3) => {
  try {
    const response = await request.get(`/posts/related/${postId}?limit=${limit}`);
    if (response.code === '200') {
      relatedPosts.value = response.data.map(post => ({
        id: post.id,
        title: post.title,
        coverUrl: post.coverUrl || defaultCover,
        authorName: post.authorName,
        views: post.views
      }));
      console.log('相关推荐文章:', relatedPosts.value);
    }
  } catch (error) {
    console.error('获取相关文章失败:', error);
    ElMessage.error('相关推荐加载失败');
  }
};

const fetchBlogDetail = async (postId) => {
  try {
    loading.value = true
    const [blogRes, interactionData] = await Promise.all([
      request.get(`/posts/${postId}`),
      interactionHelper.getInteractionData(postId, userId.value),
    ])
    await fetchRelatedPosts(postId);
    if (blogRes.code === '200') {
      const blogData = blogRes.data
      console.log('获取博客详情成功:', blogData)
      console.log('交互数据：', interactionData)

      // 并行获取标签信息
      const [tagIds, cover, fetchedComments] = await Promise.all([
        postHelper.fetchTags(blogData.id),
        postHelper.fetchCover(blogData.userId, blogData.id),
        interactionHelper.fetchCommentsWithAuthors(interactionData.comments, currentUser.id)
      ])

      // 批量获取标签名称
      const tagRequests = tagIds.map(tagId => 
        postHelper.fetchTagName(Number(tagId)).catch(() => '未知标签')
      )
      const tagNames = await Promise.all(tagRequests)

      const fetchedAuthorName = await userHelper.getUserName(blogData.userId)
      const fetchedAuthorAvatar = await userHelper.fetchUserAvatar(blogData.userId)
      const followCheckRes = await request.get('/follow/check', {
        params: {
          followerId: currentUser.id,
          followedId: blogData.userId,
        }
      })
      // 统一更新响应式对象
      blog.value = {
        ...blogData,
        coverUrl: cover || defaultCover,
        tags: tagNames.filter(name => name !== '未知标签'),
        category: blogData.categoryId ? categories.value.find(c => c.value === blogData.categoryId)?.label : '',
        authorName: fetchedAuthorName,
        authorAvatar: fetchedAuthorAvatar,
        likes: interactionData.likes,
        isLiked: interactionData.isLiked,
        favorites: interactionData.favorites,
        isFavorited: interactionData.isFavorited,
        author: {
          id: blogData.userId,
          name: fetchedAuthorName,
          avatar: fetchedAuthorAvatar,
          isFollowed: followCheckRes.data
        }
      }

      console.log('更新后的博客对象:', blog.value)
      console.log("评论列表：", comments)
      isLiked.value = interactionData.isLiked
      isFavorited.value = interactionData.isFavorited
      isFollowed.value = followCheckRes.data
      comments.value = fetchedComments
    }
  } catch (error) {
    console.log('获取博客详情失败:', error)
    ElMessage.error(`加载失败: ${error.response?.data?.msg || '服务器错误'}`)
  } finally {
    loading.value = false
  }
}

const relatedPosts = ref([])

const goBack = () => {
  router.go(-1)
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const toggleLike = async () => {
  const originalIsLiked = isLiked.value
  const originalLikes = blog.likes

  try {
    isLiked.value = !originalIsLiked
    blog.value.likes += isLiked.value ? 1 : -1

    const response = originalIsLiked 
      ? await request.delete('/likes', {
          params: {
            userId: currentUser.id,
            postId: blog.value.id
          }
        })
      : await request.post('/likes', null, {
          params: {
            userId: currentUser.id,
            postId: blog.value.id
          }
        })

    if (response.code === '200') {
      ElMessage.success(originalIsLiked ? '取消点赞成功' : '点赞成功')
    } else {
      // 回滚状态
      isLiked.value = originalIsLiked
      blog.value.likes = originalLikes
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    // 回滚状态
    isLiked.value = originalIsLiked
    blog.value.likes = originalLikes
    ElMessage.error('请求失败，请检查网络')
    console.error('API请求异常:', error)
  }
}

// 处理收藏成功事件
const handleFavorited = () => {
  isFavorited.value = true
  blog.value.favorites++
  showFavoritesManager.value = false
  ElMessage.success('收藏成功')
}

// 修改收藏按钮的点击处理函数
const toggleFavorite = async () => {
  // 添加状态回滚保护
  const originalIsFavorited = isFavorited.value
  const originalFavorites = blog.value.favorites

  try {
    if (originalIsFavorited) {
      // 取消收藏
      try {
        await ElMessageBox.confirm(
          '确定要取消收藏吗？',
          '提示',
          { type: 'warning' }
        );
      } catch (cancelError) {
        return
      }
      const response = await request.delete('/favorites/remove', {
        params: {
          userId: userId.value,
          postId: blog.value.id
        }
      });

      if (response.code === '200') {
        isFavorited.value = false;
        blog.value.favorites--;
        ElMessage.success('取消收藏成功');
      } else {
        // 操作失败时回滚状态
        isFavorited.value = originalIsFavorited;
        blog.value.favorites = originalFavorites;
        ElMessage.error(response.msg || '操作失败');
      }
    } else {
      // 打开收藏管理器
      showFavoritesManager.value = true;
    }
  } catch (error) {
    // 请求失败时回滚状态
    isFavorited.value = originalIsFavorited;
    blog.value.favorites = originalFavorites;
    ElMessage.error('请求失败，请检查网络');
    console.error('API请求异常:', error);
  }
}

const toggleFollow = async (author) => {
  if (!currentUser.id) {
    ElMessage.warning('请先登录');
    return;
  }

  const originalIsFollowed = author.isFollowed;

  try {
    if (originalIsFollowed) {
      try {
        await ElMessageBox.confirm(
          '确定要取消关注吗？',
          '提示',
          { type: 'warning' }
        );
      } catch (cancelError) {
        return;
      }
    }

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
      isFollowed.value = author.isFollowed;
      // 同步更新当前博客详情的关注状态
      if (blog.value.author.id === author.id) {
        blog.value.author.isFollowed = author.isFollowed;
      }
      ElMessage.success(author.isFollowed ? '关注成功' : '取消关注成功');
    } else {
      ElMessage.error(response.msg || '操作失败');
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络');
    console.error('关注操作失败:', error);
  }
};

const shareBlog = () => {
  // 分享功能
  if (navigator.share) {
    navigator.share({
      title: blog.value.title,
      text: blog.value.summary,
      url: window.location.href
    })
  } else {
    // 复制链接到剪贴板
    navigator.clipboard.writeText(window.location.href)
    alert('链接已复制到剪贴板')
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }

  try {
    const res = await request.post('/comment/add', {
      postId: blog.value.id,
      userId: currentUser.id,
      content: newComment.value.trim(),
      createdAt: new Date().toISOString()
    });
    console.log('提交评论成功:', res)

    if (res.code === '200') {
      const newCommentObj = {
        ...res.data,
        author: {
          name: currentUser.name,
          avatar: currentUser.avatar
        },
        likes: 0,
        isLiked: false
      };

      // 更新评论列表
      comments.value.unshift(newCommentObj);

      newComment.value = '';
      ElMessage.success('评论成功');
    } else {
      ElMessage.error(`评论失败：${res.msg}`);
    }
  } catch (error) {
    console.error('评论提交失败:', error);
    ElMessage.error(`请求异常：${error.message}`);
  }
};

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

const navigateToPost = (postId) => {
  router.push({
    path: '/blog-detail',
    query: { 
      postId,
      t: Date.now()
    }
  }).then(() => {
    // 强制重新加载数据
    fetchBlogDetail(postId)
    window.scrollTo(0, 0)
  })
}

// 生命周期
onMounted(async () => {
  try {
    loading.value = true
    const postId = route.query.postId
    console.log('加载博客详情:', postId)
    
    if (!postId) {
      ElMessage.error('无效的文章ID')
      return
    }

    try {
      const res = await request.put(`/posts/${postId}/views`)
      if (res.code === '200') {
        console.log('浏览量统计成功:', res)
      }
    } catch (error) {
      ElMessage.log('浏览量统计失败')
      console.error('views update error:', error)
    }

    if (currentUser.id) {
      await userHelper.fetchCurrentUserDetails(currentUser)
      console.log('已登录，获取当前用户信息:', currentUser)
    }

    categories.value = await postHelper.fetchCategories(categories)
    await fetchBlogDetail(postId)
    await request.put(`/posts/${postId}/views`)
  } catch (error) {
    console.error('加载博客失败:', error)
    ElMessage.error('文章加载失败: ' + error.message)
  } finally {
    loading.value = false
  }
})

watch(
  () => route.query.postId,
  async (newPostId) => {
    if (newPostId) {
      try {
        loading.value = true
        await fetchBlogDetail(newPostId)
      } catch (error) {
        ElMessage.error('文章加载失败')
      } finally {
        loading.value = false
      }
    }
  }
)
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 1.5rem;
  color: #666;
}

.blog-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: #ffffff;
  min-height: 100vh;
}

.back-navigation {
  margin-bottom: 24px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  color: #374151;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
}

.back-btn svg {
  width: 16px;
  height: 16px;
}

.blog-article {
  background: white;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.blog-header {
  margin-bottom: 32px;
}

.blog-title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.3;
  margin: 0 0 24px 0;
}

.blog-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.publish-info {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #6b7280;
  font-size: 14px;
}

.publish-date,
.read-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.publish-date svg,
.read-count svg {
  width: 14px;
  height: 14px;
}

.blog-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  color: #374151;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}

.action-btn svg {
  width: 16px;
  height: 16px;
}

.follow-btn.followed {
  background: #e83929;
  color: white;
  border-color: #e83929;
}

.blog-taxonomy {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.category {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #f3f4f6;
  color: #374151;
  border-radius: 6px;
  font-size: 14px;
}

.category svg {
  width: 14px;
  height: 14px;
}

.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 8px;
  background: #fef3f2;
  color: #dc2626;
  border: 1px solid #fecaca;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.blog-cover {
  margin-bottom: 32px;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

.blog-cover img {
  width: 100%;
  height: auto;
  display: block;
  border-radius: 8px;
}

.blog-summary {
  margin-bottom: 32px;
  padding: 20px;
  background: #f9fafb;
  border-left: 4px solid #e83929;
  border-radius: 0 8px 8px 0;
}

.blog-summary p {
  margin: 0;
  color: #374151;
  font-size: 16px;
  line-height: 1.6;
  font-style: italic;
}

.blog-content {
  margin-bottom: 32px;
  line-height: 1.8;
  color: #374151;
}

.blog-content :deep(h2) {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 32px 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #f3f4f6;
}

.blog-content :deep(h3) {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 24px 0 12px 0;
}

.blog-content :deep(p) {
  margin: 16px 0;
  font-size: 16px;
}

.blog-content :deep(ul) {
  margin: 16px 0;
  padding-left: 24px;
}

.blog-content :deep(li) {
  margin: 8px 0;
}

.empty-content {
  text-align: center;
  color: #9ca3af;
  font-style: italic;
  padding: 40px 0;
}

.blog-interactions {
  padding: 24px 0;
  border-top: 1px solid #f3f4f6;
}

.interaction-buttons {
  display: flex;
  gap: 16px;
}

.interaction-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  color: #374151;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.interaction-btn:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}

.interaction-btn svg {
  width: 18px;
  height: 18px;
}

.like-btn.liked {
  background: #fef2f2;
  color: #dc2626;
  border-color: #fecaca;
}

.like-btn.liked svg {
  fill: #dc2626;
}

.favorite-btn.favorited {
  background: #fffbeb;
  color: #d97706;
  border-color: #fed7aa;
}

.favorite-btn.favorited svg {
  fill: #d97706;
}

.comments-section {
  background: white;
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.comments-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 24px 0;
}

.comment-input-area {
  margin-bottom: 32px;
}

.comment-form {
  display: flex;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.input-container {
  flex: 1;
}

.comment-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  min-height: 80px;
}

.comment-textarea:focus {
  outline: none;
  border-color: #e83929;
  box-shadow: 0 0 0 3px rgba(232, 57, 41, 0.1);
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.submit-btn {
  padding: 8px 16px;
  background: #e83929;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.submit-btn:hover:not(:disabled) {
  background: #dc2626;
}

.submit-btn:disabled {
  background: #d1d5db;
  cursor: not-allowed;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.comment-date {
  color: #6b7280;
  font-size: 12px;
}

.comment-text {
  margin: 0 0 12px 0;
  color: #374151;
  font-size: 14px;
  line-height: 1.6;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.comment-action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: none;
  border: none;
  color: #6b7280;
  font-size: 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.comment-action-btn:hover {
  background: #f9fafb;
  color: #374151;
}

.comment-action-btn svg {
  width: 14px;
  height: 14px;
}

.related-posts {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.related-posts h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 24px 0;
}

.related-posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.related-post-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border: 1px solid #f3f4f6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.related-post-item:hover {
  background: #f9fafb;
  border-color: #e5e7eb;
  transform: translateY(-2px);
}

.related-post-cover {
  width: 80px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
  flex-shrink: 0;
}

.related-post-info {
  flex: 1;
}

.related-post-title {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-post-meta {
  display: flex;
  gap: 8px;
  color: #6b7280;
  font-size: 12px;
}

.comment-item hr {
  border: 0;
  height: 1px;
  background: #e5e7eb;
  margin: 1rem 0;
  opacity: 0.5;
}

.loading-related {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #666;
}

.spinner {
  animation: rotate 1.5s linear infinite;
  height: 20px;
  width: 20px;
  margin-right: 10px;
}

@keyframes rotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.spinner circle {
  stroke: #e83929;
  stroke-linecap: round;
  animation: dash 1.5s ease-in-out infinite;
}

@keyframes dash {
  0% { stroke-dasharray: 1, 150; stroke-dashoffset: 0; }
  50% { stroke-dasharray: 90, 150; stroke-dashoffset: -35; }
  100% { stroke-dasharray: 90, 150; stroke-dashoffset: -124; }
}

.no-related-posts {
  text-align: center;
  padding: 30px;
  color: #9ca3af;
}

.no-related-posts svg {
  width: 60px;
  height: 60px;
  margin-bottom: 10px;
  opacity: 0.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .blog-detail-container {
    padding: 16px;
  }

  .blog-article {
    padding: 20px;
  }

  .blog-title {
    font-size: 24px;
  }

  .blog-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .interaction-buttons {
    flex-wrap: wrap;
  }

  .comment-form {
    flex-direction: column;
  }

  .related-posts-grid {
    grid-template-columns: 1fr;
  }

  .related-post-item {
    flex-direction: column;
  }

  .related-post-cover {
    width: 100%;
    height: 120px;
  }
}
</style>