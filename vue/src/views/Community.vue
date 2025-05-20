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
      <div class="action-buttons">
        <el-button type="primary" @click="showPostEditor = true">
          <el-icon><Plus /></el-icon>发布帖子
        </el-button>
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
              <div class="action-item" @click="toggleFavorite(post)">
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

    <!-- 发帖编辑器对话框 -->
    <el-dialog
        v-model="showPostEditor"
        title="发布帖子"
        width="70%"
        destroy-on-close
    >
      <div class="post-editor">
        <el-form :model="newPost" label-position="top">
          <el-form-item label="标题">
            <el-input v-model="newPost.title" placeholder="请输入标题（最多50字）" maxlength="50" show-word-limit></el-input>
          </el-form-item>

          <el-form-item label="内容">
            <el-input
                v-model="newPost.content"
                type="textarea"
                :rows="6"
                placeholder="分享你的想法..."
                resize="vertical"
            ></el-input>
          </el-form-item>

          <el-form-item label="添加图片">
            <el-upload
                action="#"
                list-type="picture-card"
                :auto-upload="false"
                :limit="4"
                :on-change="handleImageChange"
                :on-remove="handleImageRemove"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>

          <el-form-item label="话题标签">
            <el-select
                v-model="newPost.tags"
                multiple
                filterable
                allow-create
                default-first-option
                placeholder="选择或创建标签（最多3个）"
                :max="3"
            >
              <el-option
                  v-for="tag in availableTags"
                  :key="tag"
                  :label="tag"
                  :value="tag"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPostEditor = false">取消</el-button>
          <el-button type="primary" @click="submitPost" :loading="submitting">
            发布
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 帖子详情对话框 -->
    <el-dialog
        v-model="showPostDetail"
        :title="currentPost?.title || '帖子详情'"
        width="70%"
        destroy-on-close
        :append-to-body="true"
        :lock-scroll="false"
    >
      <div v-if="currentPost" class="post-detail">
        <div class="post-detail-header">
          <div class="user-info">
            <el-avatar :size="40" :src="currentPost.author.avatar"></el-avatar>
            <div class="user-details">
              <div class="username">{{ currentPost.author.name }}</div>
              <div class="post-time">{{ formatTime(currentPost.createdAt) }}</div>
            </div>
          </div>
          <div class="post-actions">
            <el-button-group>
              <el-button :type="currentPost.isLiked ? 'primary' : 'default'" @click="toggleLike(currentPost)">
                <el-icon>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <path d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9a2 2 0 0 0-2-2.3zM7 22H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3"></path>
                  </svg>
                </el-icon> {{ currentPost.likes }}
              </el-button>
              <el-button :type="currentPost.isFavorited ? 'primary' : 'default'" @click="toggleFavorite(currentPost)">
                <el-icon>
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                  </svg>
                </el-icon> {{ currentPost.favorites }}
              </el-button>
            </el-button-group>
          </div>
        </div>

        <div class="post-detail-content">
          {{ currentPost.content }}
        </div>

        <div v-if="currentPost.images && currentPost.images.length > 0" class="post-detail-images">
          <el-image
              v-for="(image, index) in currentPost.images"
              :key="index"
              :src="image.url"
              :preview-src-list="currentPost.images.map(img => img.url)"
              fit="cover"
              class="detail-image"
          ></el-image>
        </div>

        <div class="post-detail-tags">
          <el-tag
              v-for="tag in currentPost.tags"
              :key="tag"
              size="small"
              effect="plain"
          >
            #{{ tag }}
          </el-tag>
        </div>
      </div>
    </el-dialog>

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
              </div>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 模拟数据
const currentUser = reactive({
  id: 1,
  name: '当前用户',
  avatar: '/placeholder.svg?height=40&width=40'
});

// 状态变量
const loading = ref(false);
const submitting = ref(false);
const commentSubmitting = ref(false);
const activeTab = ref('hot');
const showPostEditor = ref(false);
const showPostDetail = ref(false);
const showComments = ref(false);
const currentPost = ref(null);
const commentPost = ref(null);
const newComment = ref('');
const selectedTag = ref('');
const hasMorePosts = ref(true);
const page = ref(1);

// 新帖子表单
const newPost = reactive({
  title: '',
  content: '',
  images: [],
  tags: []
});

// 模拟数据 - 热门标签
const hotTags = ref([
  { id: 1, name: '技术讨论', type: '' },
  { id: 2, name: '资源分享', type: 'success' },
  { id: 3, name: '问题求助', type: 'warning' },
  { id: 4, name: '经验分享', type: 'info' },
  { id: 5, name: '职场交流', type: 'danger' }
]);

// 模拟数据 - 可用标签
const availableTags = ref([
  '技术讨论', '资源分享', '问题求助', '经验分享', '职场交流',
  'JavaScript', 'Vue', 'React', 'Node.js', 'Python',
  '前端', '后端', '全栈', '设计', 'UI/UX'
]);

// 模拟数据 - 帖子列表
const posts = ref([
  {
    id: 1,
    title: '如何高效学习前端技术？',
    content: '作为一名前端开发者，我发现学习新技术的速度非常重要。以下是我的一些经验分享...\n\n1. 建立知识体系\n2. 多动手实践\n3. 参与开源项目\n4. 阅读优质博客\n5. 定期复习巩固',
    author: {
      id: 2,
      name: '技术达人',
      avatar: '/placeholder.svg?height=40&width=40'
    },
    createdAt: new Date(Date.now() - 3600000 * 2),
    images: [
      { url: '/placeholder.svg?height=300&width=400' },
      { url: '/placeholder.svg?height=300&width=400' }
    ],
    tags: ['前端', '学习方法', '经验分享'],
    likes: 128,
    isLiked: false,
    favorites: 56,
    isFavorited: false,
    comments: [
      {
        id: 101,
        content: '非常实用的建议，特别是参与开源项目这点我很认同！',
        author: {
          id: 3,
          name: '开源爱好者',
          avatar: '/placeholder.svg?height=32&width=32'
        },
        createdAt: new Date(Date.now() - 3600000),
        likes: 24,
        isLiked: false
      },
      {
        id: 102,
        content: '我觉得定期复习很重要，不然学了就忘。',
        author: {
          id: 4,
          name: '学习者',
          avatar: '/placeholder.svg?height=32&width=32'
        },
        createdAt: new Date(Date.now() - 2700000),
        likes: 16,
        isLiked: false
      }
    ]
  },
  {
    id: 2,
    title: '分享一个实用的Vue 3组件库',
    content: '最近在项目中使用了一个非常不错的Vue 3组件库，界面美观、功能强大，推荐给大家！这个库的特点是：完全支持Vue 3和TypeScript，提供丰富的组件和主题定制，文档详尽，示例丰富，性能优秀，包体积小。',
    author: {
      id: 5,
      name: 'Vue爱好者',
      avatar: '/placeholder.svg?height=40&width=40'
    },
    createdAt: new Date(Date.now() - 3600000 * 5),
    images: [],
    tags: ['Vue', '组件库', '资源分享'],
    likes: 95,
    isLiked: true,
    favorites: 42,
    isFavorited: true,
    comments: [
      {
        id: 103,
        content: '感谢分享！正好我最近在找Vue 3的组件库。',
        author: {
          id: 6,
          name: '前端新手',
          avatar: '/placeholder.svg?height=32&width=32'
        },
        createdAt: new Date(Date.now() - 3000000),
        likes: 12,
        isLiked: false
      }
    ]
  },
  {
    id: 3,
    title: '请教一个关于React Hooks的问题',
    content: '我在使用useEffect时遇到了一个问题：当依赖数组包含对象或数组时，即使内容没变，组件也会重新渲染。有没有好的解决方案？',
    author: {
      id: 7,
      name: 'React学习者',
      avatar: '/placeholder.svg?height=40&width=40'
    },
    createdAt: new Date(Date.now() - 3600000 * 8),
    images: [],
    tags: ['React', 'Hooks', '问题求助'],
    likes: 42,
    isLiked: false,
    favorites: 18,
    isFavorited: false,
    comments: [
      {
        id: 104,
        content: '你可以使用useMemo来记忆对象或数组，或者使用useRef来存储它们。',
        author: {
          id: 8,
          name: 'React专家',
          avatar: '/placeholder.svg?height=32&width=32'
        },
        createdAt: new Date(Date.now() - 7200000),
        likes: 36,
        isLiked: true
      }
    ]
  },
  {
    id: 4,
    title: '我的前端学习路线图分享',
    content: '前端学习路线图\n\n基础阶段\n- HTML/CSS基础\n- JavaScript基础\n- 浏览器工作原理\n\n进阶阶段\n- 现代JavaScript (ES6+)\n- 框架学习 (React/Vue/Angular)\n- 状态管理\n\n高级阶段\n- 性能优化\n- 安全性\n- 服务端渲染\n- TypeScript\n\n希望对大家有所帮助！',
    author: {
      id: 9,
      name: '前端导师',
      avatar: '/placeholder.svg?height=40&width=40'
    },
    createdAt: new Date(Date.now() - 3600000 * 24),
    images: [
      { url: '/placeholder.svg?height=300&width=600' }
    ],
    tags: ['前端', '学习路线', '经验分享'],
    likes: 215,
    isLiked: false,
    favorites: 143,
    isFavorited: false,
    comments: [
      {
        id: 105,
        content: '非常全面的学习路线，建议加上一些工程化的内容，比如Webpack、Vite等构建工具。',
        author: {
          id: 10,
          name: '工程化专家',
          avatar: '/placeholder.svg?height=32&width=32'
        },
        createdAt: new Date(Date.now() - 18000000),
        likes: 28,
        isLiked: false
      }
    ]
  }
]);

// 计算属性 - 根据筛选条件过滤帖子
const filteredPosts = computed(() => {
  let result = [...posts.value];

  // 根据标签筛选
  if (selectedTag.value) {
    result = result.filter(post => post.tags.includes(selectedTag.value));
  }

  // 根据选项卡排序
  if (activeTab.value === 'hot') {
    result.sort((a, b) => (b.likes + b.comments.length * 2) - (a.likes + a.comments.length * 2));
  } else if (activeTab.value === 'latest') {
    result.sort((a, b) => b.createdAt - a.createdAt);
  } else if (activeTab.value === 'mostLiked') {
    result.sort((a, b) => b.likes - a.likes);
  }

  return result;
});

// 生命周期钩子
onMounted(() => {
  loadPosts();
});

// 方法 - 加载帖子
const loadPosts = () => {
  loading.value = true;

  // 模拟API请求延迟
  setTimeout(() => {
    // 这里实际项目中会从API获取数据
    loading.value = false;
  }, 1000);
};

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

// 方法 - 处理图片上传
const handleImageChange = (file) => {
  // 实际项目中会上传图片到服务器
  const reader = new FileReader();
  reader.onload = (e) => {
    newPost.images.push({
      url: e.target.result,
      name: file.name
    });
  };
  reader.readAsDataURL(file.raw);
};

// 方法 - 处理图片移除
const handleImageRemove = (file) => {
  const index = newPost.images.findIndex(img => img.name === file.name);
  if (index !== -1) {
    newPost.images.splice(index, 1);
  }
};

// 方法 - 提交帖子
const submitPost = () => {
  if (!newPost.title.trim()) {
    ElMessage.warning('请输入标题');
    return;
  }

  if (!newPost.content.trim()) {
    ElMessage.warning('请输入内容');
    return;
  }

  submitting.value = true;

  // 模拟API请求延迟
  setTimeout(() => {
    // 模拟添加新帖子
    const newPostObj = {
      id: Date.now(),
      title: newPost.title,
      content: newPost.content,
      author: currentUser,
      createdAt: new Date(),
      images: [...newPost.images],
      tags: [...newPost.tags],
      likes: 0,
      isLiked: false,
      favorites: 0,
      isFavorited: false,
      comments: []
    };

    posts.value.unshift(newPostObj);

    // 重置表单
    newPost.title = '';
    newPost.content = '';
    newPost.images = [];
    newPost.tags = [];

    showPostEditor.value = false;
    submitting.value = false;

    ElMessage.success('发布成功');
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
const toggleLike = (post) => {
  post.isLiked = !post.isLiked;
  post.likes += post.isLiked ? 1 : -1;

  // 实际项目中会发送API请求
};

// 方法 - 收藏帖子
const toggleFavorite = (post) => {
  post.isFavorited = !post.isFavorited;
  post.favorites += post.isFavorited ? 1 : -1;

  // 实际项目中会发送API请求
};

// 方法 - 提交评论
const submitComment = () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }

  commentSubmitting.value = true;

  // 模拟API请求延迟
  setTimeout(() => {
    // 模拟添加新评论
    const newCommentObj = {
      id: Date.now(),
      content: newComment.value,
      author: currentUser,
      createdAt: new Date(),
      likes: 0,
      isLiked: false
    };

    // 添加到当前评论帖子
    commentPost.value.comments.unshift(newCommentObj);

    // 同步到原始帖子
    const originalPost = posts.value.find(p => p.id === commentPost.value.id);
    if (originalPost) {
      originalPost.comments.unshift(newCommentObj);
    }

    newComment.value = '';
    commentSubmitting.value = false;

    ElMessage.success('评论成功');
  }, 500);
};

// 方法 - 点赞评论
const toggleCommentLike = (comment) => {
  comment.isLiked = !comment.isLiked;
  comment.likes += comment.isLiked ? 1 : -1;

  // 实际项目中会发送API请求
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
</style>