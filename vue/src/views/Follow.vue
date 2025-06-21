<template>
  <div class="follow-container">
    <!-- 左侧：关注的人发布的博客列表 -->
    <div class="post-feed">
      <div class="feed-header">
        <h2 class="section-title">关注动态</h2>
      </div>

      <div class="post-list">
        <div v-for="post in followedBlogs" :key="post.id" class="post-card">
          <div class="post-header">
            <div class="author-info">
              <el-avatar :size="46" :src="post.authorAvatar" class="author-avatar"></el-avatar>
              <div class="author-details">
                <div class="author-name">{{ post.authorName }}</div>
                <div class="post-meta">
                  <span class="post-time">{{ formatTime(post.publishedAt) }}</span>
                  <span class="post-category" v-if="post.category">{{ post.category }}</span>
                </div>
              </div>
            </div>
            <div class="follow-status" v-if="post.isNew">
              <span class="new-post-badge">新内容</span>
            </div>
          </div>

          <div class="post-content" @click="viewPostDetail(post)">
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-summary">{{ post.summary }}</p>

            <div v-if="post.coverImage" class="post-image" :class="{'wide-image': post.wideImage}">
              <img :src="post.coverImage" :alt="post.title" />
            </div>
          </div>

          <div class="post-footer">
            <div class="post-tags" v-if="post.tags && post.tags.length">
              <span v-for="tag in post.tags" :key="tag" class="post-tag"># {{ tag }}</span>
            </div>
          </div>
        </div>

        <div v-if="followedBlogs.length === 0" class="empty-state">
          <el-empty description="暂无关注动态">
            <template #image>
              <img src="https://cdn.jsdelivr.net/gh/lucide-icons/lucide/icons/rss.svg" class="empty-icon" />
            </template>
            <el-button type="primary" class="discover-btn">发现更多博主</el-button>
          </el-empty>
        </div>
      
        <div class="load-more" v-if="hasMorePosts">
          <el-button plain @click="handleLoadMore">加载更多</el-button>
        </div>
      </div>
    </div>

    <!-- 右侧：关注列表 -->
    <div class="following-list">
      <div class="following-header">
        <h2 class="section-title">我的关注 <span class="count">({{ followingUsers.length }})</span></h2>
      </div>

      <div class="search-following">
        <el-input
            v-model="searchQuery"
            placeholder="搜索关注的人"
            clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="users-list">
        <div v-for="user in filteredFollowingUsers" :key="user.id" class="user-item" @click="viewUserProfile(user.id)">
          <div class="user-info">
            <el-avatar :size="50" :src="user.avatar" class="user-avatar"></el-avatar>
            <div class="user-details">
              <div class="user-name-row">
                <span class="user-name">{{ user.name }}</span>
                <span v-if="user.isVerified" class="verified-badge">
                  <el-icon><Check /></el-icon>
                </span>
              </div>
              <div class="user-bio">{{ user.bio }}</div>
            </div>
          </div>
          <div class="follow-button-container">
            <el-button
                size="small"
                :type="user.isFollowing ? 'default' : 'primary'"
                :class="{ 'following-btn': user.isFollowing }"
                @click.stop="toggleFollow(user)"
            >
              <el-icon v-if="user.isFollowing"><Check /></el-icon>
              <span>{{ user.isFollowing ? '已关注' : '关注' }}</span>
            </el-button>
          </div>
        </div>

        <div v-if="filteredFollowingUsers.length === 0" class="empty-state">
          <el-empty description="未找到匹配的用户" />
        </div>
      </div>

    </div>

    <!-- 帖子详情对话框 -->
    <BlogPreviewDialog
      :visible="showPostDetail"
      :post="currentPost"
      title="动态详情"
      @close="showPostDetail = false"
    />

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  Search,
  Check
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useStore } from "vuex";
import defaultAvatar from '@/assets/imgs/default.jpg'
import defaultCover from '@/assets/imgs/default-cover.jpg'
import { getUserName, fetchUserAvatar } from '@/utils/userHelper';
import BlogPreviewDialog from '@/components/BlogPreviewDialog.vue';
import { formatTime } from '@/utils/dateUtils';
import * as postHelper from "@/utils/postHelper.js";

const store = useStore();
const userId = computed(() => store.getters.userId)

const searchQuery = ref('')       // 搜索关注用户
const followedBlogs = ref([]);    // 用于存储关注的博客
const followingUsers = ref([]);   // 用于存储关注的用户
const showPostDetail = ref(false);
const currentPost = ref(null);

const viewPostDetail = (post) => {
  currentPost.value = {
    id: post.id,
    title: post.title,
    formattedContent: post.formattedContent,
    coverUrl: post.coverUrl,
    author: {
      name: post.authorName,
      avatar: post.authorAvatar
    },
    createdAt: post.publishedAt,
    category: post.category,
    tags: post.tags,
    content: post.content,
    images: post.images?.map(img => ({ url: img })) || []
  };
  showPostDetail.value = true;
};

// 过滤关注用户列表
const filteredFollowingUsers = computed(() => {
  if (!searchQuery.value) return followingUsers.value
  return followingUsers.value.filter(user =>
      user.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.bio.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// 切换关注状态
const toggleFollow = async (user) => {
  try {
    // 取消关注确认对话框
    if (user.isFollowing) {
      try {
        await ElMessageBox.confirm('确定要取消关注该用户吗？', '取消关注确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        });
      } catch {
        return; // 用户取消操作直接返回
      }
    }

    const method = user.isFollowing ? 'delete' : 'post';
    const endpoint = user.isFollowing ? '/follow/unfollow' : '/follow/add';
    
    await request[method](endpoint, null, {
      params: {
        followerId: store.getters.userId,
        followedId: user.id
      }
    });
    
    user.isFollowing = !user.isFollowing;
    ElMessage({
      message: user.isFollowing? '已关注' : '已取消关注',
      type: user.isFollowing? 'success' : 'success',
      offset: 70
    })
  } catch (error) {
    console.error('操作失败:', error);
    ElMessage.error(`操作失败: ${error.response?.data?.msg || error.message}`);
  }
};

// 查看用户资料
const viewUserProfile = (userId) => {
  console.log('查看用户资料:', userId)
  // 实际应用中这里会跳转到用户资料页
  ElMessage({
    message: `正在查看用户 ID: ${userId}`,
    type: 'info',
    offset: 70
  })
}

// 在setup部分添加分页状态变量
const page = ref(1);
const hasMorePosts = ref(false);

// 获取关注动态
const fetchFollowedPosts = async () => {
  try {
    const followingRes = await request.get('/follow/list-following', {
      params: { followerId: userId.value }
    });
    // console.log("获取关注列表：", followingRes.data)
    if (followingRes.code === '200') {
      const followedIds = followingRes.data.map(user => user.id);
      
      const blogRes = await request.get('/posts/followed', {
        params: { userIds: followedIds.join(',') }
      });

      if (blogRes.code === '200') {
        followedBlogs.value = await Promise.all(
          blogRes.data.map(async blog => ({
            ...blog,
            authorName: await getUserName(blog.userId),
            authorAvatar: await fetchUserAvatar(blog.userId),
            coverUrl: await postHelper.fetchCover(blog.userId, blog.id) || defaultCover,
            isNew: new Date().getTime() - new Date(blog.createdAt).getTime() < 86400000,
          }))
        );
        console.log("获取关注动态：", followedBlogs.value)
        hasMorePosts.value = blogRes.data.length >= 10;
      }
    }
  } catch (error) {
    console.error('获取数据失败:', error);
    ElMessage.error('数据加载失败');
  }
};

// 获取关注列表
const fetchFollowingUsers = async () => {
  try {
    const res = await request.get('/follow/list-following', {
      params: { followerId: userId.value }
    });
    if (res.code === '200') {
      // 使用Promise.all处理异步头像请求
      followingUsers.value = await Promise.all(
        res.data.map(async user => {
          try {
            const avatarUrl = await fetchUserAvatar(user.id);
            return {
              id: user.id,
              name: user.username || '匿名用户',
              avatar: avatarUrl || defaultAvatar,
              bio: user.bio || '暂无个人介绍',
              isFollowing: true,
              isVerified: user.certified || false
            };
          } catch (error) {
            console.error('获取用户头像失败:', error);
            return {
              ...user,
              avatar: defaultAvatar
            };
          }
        })
      );
    }
  } catch (error) {
    console.error('获取关注列表失败:', error);
  }
};

// 在setup部分添加分页逻辑
const handleLoadMore = () => {
  page.value++;
  fetchFollowedPosts();
};

// 在onMounted中修改调用方式
onMounted(() => {
  fetchFollowedPosts();
  fetchFollowingUsers();
});
</script>

<style scoped>
.follow-container {
  display: flex;
  gap: 24px;
  height: 100%;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

/* 左侧博客列表样式 */
.post-feed {
  flex: 1;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  height: calc(100vh - 100px);
  overflow-y: auto;
}

.feed-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background-color: #fff;
  z-index: 10;
  border-radius: 12px 12px 0 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  margin: 0;
}

.count {
  font-size: 14px;
  color: #999;
  margin-left: 5px;
  font-weight: normal;
}

.post-list {
  padding: 0 24px 24px;
}

.post-card {
  padding: 24px 0;
  border-bottom: 1px solid #f0f0f0;
}

.post-card:last-child {
  border-bottom: none;
}

.post-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  font-size: 15px;
  color: #333;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}

.post-time {
  font-size: 13px;
  color: #999;
}

.post-category {
  font-size: 12px;
  color: #e83929;
  background-color: #fff2f0;
  padding: 2px 8px;
  border-radius: 10px;
}

.new-post-badge {
  font-size: 12px;
  color: #fff;
  background-color: #e83929;
  padding: 2px 8px;
  border-radius: 10px;
}

.post-content {
  margin-bottom: 16px;
  cursor: pointer;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
  line-height: 1.4;
}

.post-summary {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
  line-height: 1.6;
}

.post-image {
  width: 100%;
  margin-top: 12px;
  border-radius: 10px;
  overflow: hidden;
  max-height: 240px;
}

.post-image.wide-image {
  max-height: 300px;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.post-content:hover .post-image img {
  transform: scale(1.02);
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.post-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.post-tag {
  font-size: 13px;
  color: #666;
  background-color: #f5f5f5;
  padding: 4px 10px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.post-tag:hover {
  background-color: #eee;
  color: #333;
}

.blog-actions {
  display: flex;
  gap: 20px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 16px;
  transition: all 0.2s;
}

.action-button:hover {
  color: #e83929;
  background-color: #fff2f0;
}

.action-button.active {
  color: #e83929;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 右侧关注列表样式 */
.following-list {
  width: 320px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  height: calc(100vh - 100px);
  overflow-y: auto;
}

.following-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background-color: #fff;
  z-index: 10;
  border-radius: 12px 12px 0 0;
}

.manage-btn {
  font-size: 13px;
  height: 32px;
  padding: 0 12px;
  background-color: #e83929;
  border: none;
}

.search-following {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}


.users-list {
  padding: 16px 24px;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s;
}

.user-item:hover {
  background-color: #f9f9f9;
}

.user-item:last-child {
  border-bottom: none;
}

.user-info {
  display: flex;
  gap: 12px;
  align-items: center;
  flex: 1;
  max-width: calc(100% - 80px);
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.user-details {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  width: 100%;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-name {
  font-weight: 600;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
}

.verified-badge {
  color: #fff;
  background-color: #1da1f2;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.verified-badge .el-icon {
  font-size: 10px;
}

.user-bio {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 2px;
}

/* 添加按钮容器修复对齐问题 */
.follow-button-container {
  width: 80px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  flex-shrink: 0;
}

.follow-button-container .el-button {
  width: 100%;
  padding: 0;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.following-btn {
  background-color: #f5f5f5;
  color: #666;
  border: 1px solid #ddd;
}

.empty-state {
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-icon {
  width: 60px;
  height: 60px;
  opacity: 0.5;
}

.discover-btn {
  margin-top: 16px;
  background-color: #e83929;
  border: none;
}

.recommended-user .user-info {
  flex: 1;
}


/* 确保两个区域独立滚动 */
.post-feed, .following-list {
  overflow-y: auto;
  scrollbar-width: thin;
}

.post-feed::-webkit-scrollbar, .following-list::-webkit-scrollbar {
  width: 6px;
}

.post-feed::-webkit-scrollbar-thumb, .following-list::-webkit-scrollbar-thumb {
  background-color: #ddd;
  border-radius: 3px;
}

.post-feed::-webkit-scrollbar-track, .following-list::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}

/* 帖子详情对话框样式 */
.post-detail {
  padding: 20px;
}

.post-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  line-height: 1.4;
}

.username {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.post-time {
  font-size: 13px;
  color: #999;
}

.post-detail-content {
  font-size: 15px;
  line-height: 1.8;
  color: #444;
  margin-bottom: 24px;
  white-space: pre-wrap;
}

.post-detail-images {
  display: grid;
  gap: 12px;
  margin-bottom: 24px;
}

.detail-image {
  width: 100%;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.detail-image:hover {
  transform: scale(1.02);
}

.post-detail-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 添加动画效果 */
.el-dialog {
  transition: all 0.3s ease-out;
}

/* 调整对话框标题样式 */
:deep(.el-dialog__header) {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-right: 0;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .follow-container {
    flex-direction: column;
  }

  .following-list {
    width: 100%;
    height: auto;
    max-height: 500px;
  }

  .post-feed {
    height: auto;
    max-height: calc(100vh - 600px);
  }
}

@media (max-width: 768px) {
  .blog-actions {
    gap: 12px;
  }

  .action-button {
    font-size: 13px;
  }

  .post-title {
    font-size: 16px;
  }

  .follow-button-container {
    width: 70px;
  }
}
</style>