<template>
  <div class="community-container">
    <!-- 顶部导航和筛选区 -->
    <div class="community-header">
      <div class="filter-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="热门" name="hot"></el-tab-pane>
          <el-tab-pane label="最新" name="latest"></el-tab-pane>
          <el-tab-pane label="点赞最多" name="mostLiked"></el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 热门标签区 -->
    <div class="hot-tags">
      <span class="tag-title">热门话题：</span>
      <el-tag
          type=""
          effect="plain"
          class="tag-item"
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
          class="tag-item"
          @click="filterByTag(tag.name)"
          :class="{ 'active-tag': selectedTag === tag.name }"
      >
        #{{ tag.name }}
      </el-tag>
    </div>

    <!-- 内容区域 -->
    <div class="community-content">
      <!-- 帖子列表 -->
      <div class="posts-container">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>
        <!-- 新增：错误状态显示 -->
        <div v-else-if="error" class="error-container">
          <el-alert type="error" :closable="false">
            {{ error }}
          </el-alert>
        </div>
        <template v-else>
          <div v-for="post in filteredPosts" :key="post.id" class="post-card">
            <div class="post-header">
              <div class="user-info">
                <el-avatar :size="40" :src="post.author.avatar"></el-avatar>
                <div class="user-details">
                  <div class="username">{{ post.author.name }}</div>
                  <div class="post-time">{{ formatTime(post.createdAt) }}</div>
                </div>
              </div>
              <!-- 新增关注按钮 -->
              <div class="follow-section" v-if="post.author.id !== currentUser.id">
                <el-button
                    :type="post.author.isFollowed ? 'default' : 'primary'"
                    size="small"
                    @click="toggleFollow(post.author)"
                    :loading="post.author.followLoading"
                >
                  {{ post.author.isFollowed ? '已关注' : '关注' }}
                </el-button>
              </div>
            </div>

            <div class="post-title" @click="viewPostDetail(post)">{{ post.title }}</div>

            <div class="post-content" @click="viewPostDetail(post)">
              {{ post.content }}
            </div>

            <div v-if="post.images && post.images.length > 0" class="post-images">
              <el-image
                  v-for="(image, index) in post.images.slice(0, 4)"
                  :key="index"
                  :src="image.url"
                  fit="cover"
                  class="post-image"
              ></el-image>
            </div>

            <div class="post-tags">
              <el-tag
                  v-for="tag in post.tags"
                  :key="tag"
                  size="small"
                  effect="plain"
                  @click.stop="filterByTag(tag)"
              >
                #{{ tag }}
              </el-tag>
            </div>

            <div class="post-actions">
              <div class="action-item" @click="toggleLike(post)">
                <el-icon :class="{ 'active': post.isLiked }">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
                  </svg>
                </el-icon>
                <span>{{ post.likes }}</span>
              </div>
              <div class="action-item" @click="openComments(post)">
                <el-icon>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                  </svg>
                </el-icon>
                <span>{{ post.comments.length }}</span>
              </div>
              <div class="action-item" @click="openFavoritesModal(post)">
                <el-icon :class="{ 'active': post.isFavorited }">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                  </svg>
                </el-icon>
                <span>{{ post.favorites }}</span>
              </div>
            </div>
          </div>

          <div v-if="filteredPosts.length === 0" class="empty-state">
            <el-empty description="暂无帖子" />
          </div>

          <div v-if="hasMorePosts && !loading" class="load-more">
            <el-button @click="loadMorePosts">加载更多</el-button>
          </div>
        </template>
      </div>
    </div>

    <!-- 帖子详情对话框 -->
    <BlogPreviewDialog
        :visible="showPostDetail"
        :post="previewPost"
        title="博客预览"
        @close="showPostDetail = false"
    />

    <!-- 评论模态框 -->
    <el-dialog
        v-model="showComments"
        title="评论"
        width="50%"
        destroy-on-close
        :append-to-body="true"
        :lock-scroll="false"
        class="comments-dialog"
    >
      <template v-if="commentPost">
        <!-- 帖子标题 -->
        <div class="comment-post-title">{{ commentPost.title }}</div>

        <!-- 评论输入框 -->
        <div class="comment-input-container">
          <div class="comment-input-header">
            <el-avatar :size="32" :src="currentUser.avatar"></el-avatar>
            <span class="username">{{ currentUser.name }}</span>
          </div>
          <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="发表评论..."
              resize="none"
          ></el-input>
          <div class="comment-input-actions">
            <el-button type="primary" @click="submitComment" :loading="commentSubmitting">发表评论</el-button>
          </div>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list-header">
          <span>全部评论 ({{ commentPost.comments.length }})</span>
        </div>

        <div class="comment-list">
          <div v-if="commentPost.comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论，快来发表第一条评论吧！" />
          </div>

          <div v-else>
            <div v-for="comment in commentPost.comments" :key="comment.id" class="comment-item">
              <div class="comment-item-header">
                <div class="user-info">
                  <el-avatar :size="32" :src="comment.author.avatar"></el-avatar>
                  <div class="user-details">
                    <div class="username">{{ comment.author.name }}</div>
                    <div class="comment-time">{{ formatTime(comment.createdAt) }}</div>
                  </div>
                </div>
              </div>

              <div class="comment-item-content">
                {{ comment.content }}
              </div>

              <div class="comment-item-actions">
                <div class="action-item" @click="toggleCommentLike(comment)">
                  <el-icon :class="{ 'active': comment.isLiked }">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                      <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
                    </svg>
                  </el-icon>
                  <span>{{ comment.likes }}</span>
                </div>
                <div class="action-item" @click="toggleReplyInput(comment)">
                  <el-icon>
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                      <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                    </svg>
                  </el-icon>
                  <span>{{ comment.replyCount || '' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 收藏夹选择模态框 -->
    <el-dialog
        v-model="showFavoritesModal"
        title="添加收藏夹"
        width="480px"
        :show-close="true"
        :close-on-click-modal="false"
        :close-on-press-escape="true"
        class="favorites-modal"
    >
      <div class="favorites-content">
        <!-- 创建收藏夹选项 -->
        <div class="create-folder-item" @click="showCreateFolderDialog">
          <div class="folder-info">
            <el-icon class="create-icon">
              <Plus />
            </el-icon>
            <span class="folder-name">创建收藏夹</span>
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
                  <span class="privacy-status">{{ folder.isPrivate ? '私密' : '公开' }}</span>
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
        title="创建收藏夹"
        width="400px"
        :show-close="true"
        :close-on-click-modal="false"
    >
      <el-form :model="newFolder" label-position="top">
        <el-form-item label="收藏夹名称" required>
          <el-input
              v-model="newFolder.name"
              placeholder="请输入收藏夹名称"
              maxlength="20"
              show-word-limit
          />
        </el-form-item>
        <el-form-item label="隐私设置">
          <el-radio-group v-model="newFolder.isPrivate">
            <el-radio :label="false">公开 - 所有人可见</el-radio>
            <el-radio :label="true">私密 - 仅自己可见</el-radio>
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
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button
              type="primary"
              @click="createFolder"
              :loading="createLoading"
              :disabled="!newFolder.name.trim()"
          >
            创建
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import request from '@/utils/request';
import { useStore } from "vuex";
import { fetchUserAvatar } from '@/utils/userHelper';
import * as userHelper from "@/utils/userHelper.js";
import BlogPreviewDialog from "@/components/BlogPreviewDialog.vue";
import defaultCover from "@/imgs/default-cover.jpg";

const store = useStore();
const userId = computed(() => store.getters.userId)

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
const hotTags = ref([]);    // 热门标签

// 模拟数据 - 帖子列表
const posts = ref([]);

// 计算属性 - 根据筛选条件过滤帖子
const filteredPosts = computed(() => {
  let result = [...posts.value];

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

// 生命周期钩子
onMounted(async () => {
  if (currentUser.id) {
    userHelper.fetchCurrentUserDetails(currentUser);
    userHelper.fetchCurrentUserDetails(currentUser)
  }
  fetchAndProcessPosts();
  fetchHotTags();
});

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

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }

  commentSubmitting.value = true;

  try {
    const res = await request.post('/comment/add', {
      postId: commentPost.value.id,
      userId: currentUser.id,
      content: newComment.value.trim()
    });

    if (res.code === '200') {
      // 添加真实返回的评论数据
      const newCommentObj = {
        ...res.data,
        author: {
          name: currentUser.username,
          avatar: currentUser.avatar
        },
        likes: 0,
        isLiked: false
      };

      // 更新当前帖子评论列表
      commentPost.value.comments.unshift(newCommentObj);
      
      // 同步到主帖子列表
      const originalPost = posts.value.find(p => p.id === commentPost.value.id);
      if (originalPost) {
        originalPost.comments.unshift(newCommentObj);
      }

      newComment.value = '';
      ElMessage.success('评论成功');
    } else {
      ElMessage.error(`评论失败：${res.msg}`);
    }
  } catch (error) {
    console.error('评论提交失败:', error);
    ElMessage.error(`请求异常：${error.message}`);
  } finally {
    commentSubmitting.value = false;
  }
};

// 方法 - 点赞评论
const toggleCommentLike = (comment) => {
  comment.isLiked = !comment.isLiked;
  comment.likes += comment.isLiked ? 1 : -1;

  // 实际项目中会发送API请求
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

// 方法 - 根据标签筛选
const filterByTag = (tag) => {
  selectedTag.value = tag;
};

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
                      const userRes = await request.post(`/user/getUser`, null, {
                        params: {
                          userId: comment.userId
                        }
                      });
                      if (userRes.code === '200') {
                        return {
                          ...comment,
                          author: {
                            name: userRes.data.username || '未知用户',
                            avatar: await fetchUserAvatar(comment.userId)
                          }
                        };
                      }
                    } catch (e) {
                      console.error(`获取用户${comment.userId}信息失败:`, e);
                    }
                    return {
                      ...comment,
                      author: {
                        name: '未知用户',
                        avatar: '/placeholder.svg?height=32&width=32'
                      }
                    };
                  })
                );
              }
            } catch (userError) {
              console.error(`获取用户 ${postData.userId} 信息失败:`, userError);
            }
          }

          // 确保 images 和 comments 字段存在且为数组
          const images = Array.isArray(postData.images) ? postData.images : [];
          const comments = Array.isArray(postData.comments) ? postData.comments.map(comment => ({
            ...comment,
            author: comment.author || { name: '未知用户', avatar: '/placeholder.svg?height=32&width=32' }, // 确保评论作者信息存在
            createdAt: comment.createdAt || new Date().toISOString(), // 确保评论时间存在
            likes: comment.data || 0,
            isLiked: comment.isLiked || false,
          })) : [];
          const tagRes = await request.get('/post-tags/name', {
            params: { postId: postData.id }
          })


          return {
            id: postData.id,
            title: postData.title,
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
            createdAt: postData.createdAt || new Date().toISOString(), // 后端应提供标准日期字符串
            tags: tagRes.data || [],
            likes: postData.likes || 0,
            isLiked: postData.isLiked || false,
            favorites: postData.favorites || 0,
            isFavorited: postData.isFavorited || false,
            comments: comments, // 假设后端直接返回评论数组
          };
        })
      );
      console.log('处理后的帖子数据:', posts.value); // 调试输出
      // 如果后端支持分页，这里可能需要更新 hasMorePosts 和 page
      // 暂时假设一次性加载所有帖子
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
.community-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.community-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-tabs {
  flex: 1;
}

.hot-tags {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-title {
  font-weight: 500;
  margin-right: 8px;
}

.tag-item {
  cursor: pointer;
}

.active-tag {
  background-color: #e83929;
  color: white;
}

.community-content {
  display: flex;
  gap: 20px;
}

.posts-container {
  flex: 1;
}

.post-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.post-header .el-button--primary {
  background-color: #e83929; /* 与创作按钮保持一致的主题色 */
  border-color: #e83929;
}

/* 已关注状态样式 */
.post-header .el-button--default.is-checked {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-details {
  margin-left: 12px;
}

.username {
  font-weight: 500;
  font-size: 14px;
}

.post-time {
  font-size: 12px;
  color: #666;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 12px;
  cursor: pointer;
}

.post-content {
  margin-bottom: 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.post-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.post-image {
  width: calc(25% - 6px);
  height: 120px;
  border-radius: 4px;
  overflow: hidden;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.post-actions {
  display: flex;
  border-top: 1px solid #eee;
  padding-top: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  margin-right: 24px;
  cursor: pointer;
  color: #666;
}

.action-item .el-icon {
  margin-right: 4px;
}

.icon {
  width: 16px;
  height: 16px;
}

.action-item .active {
  color: #e83929;
}

.action-item:hover {
  color: #e83929;
}

.action-item .el-icon {
  margin-right: 4px;
  font-size: 16px;
}

.action-item .active {
  color: #f56c6c;
}

/* 收藏夹模态框样式 */
.favorites-modal :deep(.el-dialog__header) {
  padding: 20px 20px 0 20px;
  border-bottom: 1px solid #eee;
}

.favorites-modal :deep(.el-dialog__body) {
  padding: 0;
}

.favorites-content {
  max-height: 400px;
  overflow-y: auto;
}

.create-folder-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.create-folder-item:hover {
  background-color: #f8f9fa;
}

.create-folder-item .folder-info {
  display: flex;
  align-items: center;
}

.create-icon {
  color: #f56c6c;
  font-size: 18px;
  margin-right: 12px;
}

.folder-name {
  font-size: 14px;
  font-weight: 500;
  color: #f56c6c;
}

.favorites-list {
  padding: 0;
}

.folder-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.2s;
}

.folder-item:hover {
  background-color: #f8f9fa;
}

.folder-item.recently-used {
  background-color: #fff7e6;
}

.folder-info {
  flex: 1;
}

.folder-main .folder-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.folder-meta {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.separator {
  margin: 0 6px;
}

.recent-tag {
  margin-left: 8px;
}

.item-count {
  color: #666;
}

.privacy-status {
  color: #999;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.load-more {
  text-align: center;
  margin-top: 20px;
  margin-bottom: 40px;
}

.loading-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
}

/* 帖子详情样式 */
.post-detail {
  padding: 20px;
}

.post-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.post-detail-content {
  margin-bottom: 20px;
  font-size: 15px;
  line-height: 1.7;
  white-space: pre-line;
}

.post-detail-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
}

.detail-image {
  width: calc(33.33% - 8px);
  height: 200px;
  border-radius: 4px;
  overflow: hidden;
}

.post-detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

/* 评论模态框样式 */
.comment-post-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.comment-input-container {
  margin-bottom: 20px;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
}

.comment-input-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.comment-input-header .username {
  margin-left: 10px;
}

.comment-input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.comment-list-header {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 15px;
  color: #333;
}

.comment-list {
  max-height: 400px;
  overflow-y: auto;
}

.comment-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

.comment-item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.comment-item-content {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
  color: #333;
}

.comment-item-actions {
  display: flex;
  justify-content: flex-start;
}

.empty-comments {
  padding: 30px 0;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-image {
    width: calc(50% - 4px);
  }

  .detail-image {
    width: 100%;
  }
}

@media (max-width: 576px) {
  .community-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-buttons {
    margin-top: 12px;
    width: 100%;
  }

  .action-buttons .el-button {
    width: 100%;
  }

  .post-image {
    width: 100%;
  }
}

/* 创建收藏夹对话框样式 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .favorites-modal {
    width: 90% !important;
  }

  .folder-item {
    padding: 12px 16px;
  }

  .create-folder-item {
    padding: 12px 16px;
  }
}
</style>