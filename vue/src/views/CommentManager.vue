<template>
  <div v-if="hasPermission" class="comment-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">
            <MessageSquare class="icon-primary" :size="28" />
            评论管理
          </h1>
          <p class="page-subtitle">管理和审核您博客的所有评论</p>
        </div>
        <div class="header-right">
          <el-button type="primary" @click="loadComments">
            <RefreshCw :size="16" class="icon-button" />
            刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon pending">
          <Clock :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ pendingCount }}</div>
          <div class="stat-label">待审核</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon approved">
          <CheckCircle :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ approvedCount }}</div>
          <div class="stat-label">已通过</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rejected">
          <XCircle :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ rejectedCount }}</div>
          <div class="stat-label">已拒绝</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon total">
          <MessageCircle :size="24" />
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ total }}</div>
          <div class="stat-label">总评论</div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card shadow="hover" class="search-card">
      <div class="filter-container">
        <div class="filter-group">
          <label class="filter-label">关键词</label>
          <el-input
              v-model="searchParams.keyword"
              placeholder="搜索评论内容"
              class="filter-item"
              clearable
              @clear="loadComments"
              @keyup.enter="loadComments"
          >
            <template #prefix>
              <Search :size="16" />
            </template>
          </el-input>
        </div>

        <div class="filter-group">
          <label class="filter-label">状态</label>
          <el-select
              v-model="searchParams.status"
              placeholder="状态筛选"
              class="filter-item"
              clearable
              @change="loadComments"
          >
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="未通过" value="rejected" />
          </el-select>
        </div>

        <div class="filter-group">
          <label class="filter-label">日期范围</label>
          <el-date-picker
              v-model="searchParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              class="filter-item date-picker"
              @change="loadComments"
          />
        </div>

        <div class="filter-group filter-buttons">
          <el-button
              type="primary"
              class="search-button"
              @click="loadComments"
          >
            <SearchIcon :size="16" class="icon-button" />
            搜索
          </el-button>
          <el-button
              class="reset-button"
              @click="resetFilters"
          >
            <RotateCcw :size="16" class="icon-button" />
            重置
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 评论数据表格 -->
    <el-card shadow="hover" class="table-card">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="comments.length === 0" class="empty-state">
        <div class="empty-icon">
          <MessageX :size="64" />
        </div>
        <h3>暂无评论</h3>
        <p>{{ getEmptyStateMessage() }}</p>
      </div>

      <template v-else>
        <el-table
            :data="comments"
            stripe
            style="width: 100%"
            @selection-change="handleSelectionChange"
            :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="评论内容" min-width="250">
            <template #default="{ row }">
              <div class="comment-card">
                <div class="comment-header">
                  <div class="comment-author">
                    <UserCircle :size="16" class="icon-inline" />
                    {{ row.author }}
                  </div>
                  <div class="comment-time">
                    <Clock :size="14" class="icon-inline" />
                    {{ row.createdAt }}
                  </div>
                </div>
                <div class="comment-content">
                  {{ row.content }}
                </div>
                <div v-if="row.replyTo" class="reply-info">
                  <CornerDownRight :size="14" class="icon-inline" />
                  回复: @{{ row.replyTo }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="articleTitle" label="所属文章" width="250">
            <template #default="{ row }">
              <router-link
                  :to="{ path: '/blog-detail', query: { postId: row.postId } }"
                  target="_blank"
                  class="post-link"
              >
                <FileText :size="14" class="icon-inline" />
                {{ row.articleTitle }}
              </router-link>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)" size="small" effect="light" class="status-tag">
                <template v-if="row.status === 'pending' || row.status === '0'">
                  <Clock :size="14" class="icon-inline" />
                </template>
                <template v-else-if="row.status === 'approved' || row.status === '1'">
                  <CheckCircle :size="14" class="icon-inline" />
                </template>
                <template v-else>
                  <XCircle :size="14" class="icon-inline" />
                </template>
                {{ statusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="100">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                    v-if="row.status === 'pending' || row.status === '0'"
                    type="success"
                    size="small"
                    @click="handleAudit(row, 'approved')"
                >
                  <Check :size="14" class="icon-button" />
                  通过
                </el-button>
                <el-button
                    v-if="row.status === 'pending' || row.status === '0'"
                    type="warning"
                    size="small"
                    @click="handleAudit(row, 'rejected')"
                >
                  <X :size="14" class="icon-button" />
                  拒绝
                </el-button>
                <el-button
                    type="danger"
                    size="small"
                    @click="handleDelete(row)"
                >
                  <Trash2 :size="14" class="icon-button" />
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 批量操作 -->
        <div v-if="selectedComments.length > 0" class="batch-actions">
          <div class="batch-count">
            <CheckSquare :size="16" class="icon-inline" />
            已选择 <span class="count-number">{{ selectedComments.length }}</span> 项
          </div>
          <div class="batch-buttons">
            <el-popconfirm
                title="确定批量通过选中的评论吗？"
                @confirm="batchAudit('approved')"
                confirm-button-type="success"
            >
              <template #reference>
                <el-button type="success" size="small">
                  <CheckCircle :size="14" class="icon-button" />
                  批量通过
                </el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm
                title="确定批量拒绝选中的评论吗？"
                @confirm="batchAudit('rejected')"
                confirm-button-type="warning"
            >
              <template #reference>
                <el-button type="warning" size="small">
                  <XCircle :size="14" class="icon-button" />
                  批量拒绝
                </el-button>
              </template>
            </el-popconfirm>
            <el-popconfirm
                title="确定批量删除选中的评论吗？"
                @confirm="batchDelete"
                confirm-button-type="danger"
            >
              <template #reference>
                <el-button type="danger" size="small">
                  <Trash :size="14" class="icon-button" />
                  批量删除
                </el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
              background
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              @size-change="loadComments"
              @current-change="loadComments"
          />
        </div>
      </template>
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog
        v-model="replyDialogVisible"
        title="回复评论"
        width="500px"
        destroy-on-close
    >
      <div v-if="currentComment" class="reply-dialog-content">
        <div class="original-comment">
          <div class="comment-author">
            <UserCircle :size="16" class="icon-inline" />
            {{ currentComment.author }}:
          </div>
          <div class="comment-content">{{ currentComment.content }}</div>
        </div>

        <el-form :model="replyForm" ref="replyFormRef" label-position="top">
          <el-form-item label="回复内容" prop="content" :rules="[{ required: true, message: '请输入回复内容', trigger: 'blur' }]">
            <el-input
                v-model="replyForm.content"
                type="textarea"
                rows="4"
                placeholder="请输入回复内容..."
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReply" :loading="submitting">
            <Send :size="14" class="icon-button" />
            提交回复
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>

  <div v-else class="permission-error">
    <el-result
        icon="error"
        title="权限不足"
        sub-title="您没有权限访问评论管理页面"
    >
      <template #extra>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import request from '@/utils/request';
import { useStore } from 'vuex';
import { formatDate } from "@/utils/dateUtils.js";
import {
  MessageSquare,
  RefreshCw,
  Clock,
  CheckCircle,
  XCircle,
  MessageCircle,
  Search as SearchIcon,
  RotateCcw,
  UserCircle,
  FileText,
  Check,
  X,
  Reply,
  Trash2,
  CheckSquare,
  Trash,
  CornerDownRight,
  Send
} from 'lucide-vue-next';
import { checkPermission } from "@/utils/userHelper.js";

const store = useStore();
const userId = computed(() => store.getters.userId);

// 搜索参数
const searchParams = ref({
  keyword: '',
  status: '',
  dateRange: []
});

const goHome = () => {
  router.push('/home')
}

// 评论数据
const comments = ref([]);
const selectedComments = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const hasPermission = ref(false);

// 回复对话框
const replyDialogVisible = ref(false);
const currentComment = ref(null);
const replyForm = ref({
  content: '',
  commentId: null,
  postId: null
});
const replyFormRef = ref(null);
const submitting = ref(false);

// 统计数据
const pendingCount = computed(() =>
    comments.value.filter(c => c.status === 'pending' || c.status === '0').length
);
const approvedCount = computed(() =>
    comments.value.filter(c => c.status === 'approved' || c.status === '1').length
);
const rejectedCount = computed(() =>
    comments.value.filter(c => c.status === 'rejected' || c.status === '2').length
);

// 重置筛选条件
const resetFilters = () => {
  searchParams.value = {
    keyword: '',
    status: '',
    dateRange: []
  };
  loadComments();
};

// 加载评论数据
const loadComments = async () => {
  try {
    loading.value = true;

    // 构建请求参数
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      userId: userId.value
    };

    // 添加搜索关键词（如果存在）
    if (searchParams.value.keyword) {
      params.keyword = searchParams.value.keyword;
    }

    // 添加状态筛选（如果存在）
    if (searchParams.value.status) {
      params.status = searchParams.value.status;
    }

    // 添加日期范围（如果存在）
    if (searchParams.value.dateRange && searchParams.value.dateRange.length === 2) {
      const [start, end] = searchParams.value.dateRange;
      params.startTime = formatDate(start, 'YYYY-MM-DD 00:00:00');
      params.endTime = formatDate(end, 'YYYY-MM-DD 23:59:59');
    }

    console.log('params:', params);

    const response = await request.get(`/comment/user`, { params });

    if (response.code === '200') {
      // 并行请求文章和用户信息
      const enhancedComments = await Promise.all(
          response.data.list.map(async comment => {
            // 获取文章标题
            const postRes = await request.get(`/posts/${comment.postId}`);
            // 获取用户信息
            const userRes = await request.post('/user/getUser', null, {
              params: { userId: comment.userId }
            });

            return {
              ...comment,
              createdAt: formatDate(comment.createdAt) || '未知时间',
              articleTitle: postRes.data?.title || '未知文章',
              author: userRes.data?.username || '匿名用户',
            };
          })
      );

      comments.value = enhancedComments;
      total.value = response.data.total;
    } else {
      ElMessage.error('加载评论失败: ' + response.msg);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  } finally {
    loading.value = false;
  }
};

// 更新状态显示方法
const statusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '未通过',
    pending: '待审核',
    approved: '已通过',
    rejected: '未通过'
  };
  return statusMap[status] || '未知状态';
};

const statusTagType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  };
  return typeMap[status] || '';
};

// 处理审核操作
const handleAudit = async (comment, status) => {
  try {
    const response = await request.post('/comment/audit', null, {
      params: {
        id: comment.id,
        status: status
      }
    });

    if (response.code === '200') {
      ElMessage.success('操作成功');
      // 更新本地状态
      comment.status = status === 'approved' ? '1' : '2';
    } else {
      ElMessage.error('操作失败: ' + response.msg);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  }
};

// 处理删除操作
const handleDelete = async (comment) => {
  try {
    await ElMessageBox.confirm(`确定删除这条评论吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    const response = await request.delete(`/comment/${comment.id}`);

    if (response.code === '200') {
      ElMessage.success('删除成功');
      loadComments();
    } else {
      ElMessage.error('删除失败: ' + response.msg);
    }
  } catch (error) {
    // 用户取消删除
  }
};

// 批量审核
const batchAudit = async (status) => {
  try {
    const ids = selectedComments.value.map(c => c.id);
    const response = await request.post('/comment/batch-audit', {
      ids,
      status
    });

    if (response.code === '200') {
      ElMessage.success('批量操作成功');
      selectedComments.value = [];
      loadComments();
    } else {
      ElMessage.error('操作失败: ' + response.msg);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  }
};

// 批量删除
const batchDelete = async () => {
  try {
    const ids = selectedComments.value.map(c => c.id);
    const response = await request.post('/comment/batch-delete', { ids });

    if (response.code === '200') {
      ElMessage.success('批量删除成功');
      selectedComments.value = [];
      loadComments();
    } else {
      ElMessage.error('删除失败: ' + response.msg);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  }
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedComments.value = selection;
};

// 处理回复评论
const handleReply = (comment) => {
  currentComment.value = comment;
  replyForm.value = {
    content: '',
    commentId: comment.id,
    postId: comment.postId
  };
  replyDialogVisible.value = true;
};

// 提交回复
const submitReply = async () => {
  if (!replyForm.value.content.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }

  try {
    submitting.value = true;

    const response = await request.post('/comment/reply', {
      content: replyForm.value.content,
      parentId: replyForm.value.commentId,
      postId: replyForm.value.postId,
      userId: userId.value
    });

    if (response.code === '200') {
      ElMessage.success('回复成功');
      replyDialogVisible.value = false;
      loadComments();
    } else {
      ElMessage.error('回复失败: ' + response.msg);
    }
  } catch (error) {
    ElMessage.error('请求失败: ' + error.message);
  } finally {
    submitting.value = false;
  }
};

// 获取空状态消息
const getEmptyStateMessage = () => {
  if (searchParams.value.keyword) {
    return '没有找到匹配的评论';
  } else if (searchParams.value.status) {
    return `没有${statusText(searchParams.value.status)}的评论`;
  } else if (searchParams.value.dateRange && searchParams.value.dateRange.length === 2) {
    return '所选日期范围内没有评论';
  } else {
    return '暂无评论数据';
  }
};

// 初始化加载
onMounted(async () => {
  hasPermission.value = await checkPermission(userId.value, 'admin');
  if (hasPermission.value) {
    loadComments();
  }
});
</script>

<style scoped>
.comment-management {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.icon-primary {
  color: #3b82f6;
}

.page-subtitle {
  color: #6b7280;
  margin: 0;
  font-size: 16px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.stat-icon.approved {
  background: linear-gradient(135deg, #10b981, #059669);
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #ef4444, #b91c1c);
}

.stat-icon.total {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1;
}

.stat-label {
  color: #6b7280;
  font-size: 14px;
  margin-top: 4px;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.filter-item {
  width: 220px;
}

.date-picker {
  width: 400px;
}

.filter-buttons {
  display: flex;
  flex-direction: row;
  gap: 0;
}

.search-button, .reset-button {
  display: flex;
  align-items: center;
  gap: 6px;
}

.icon-button {
  margin-right: 4px;
}

/* 表格卡片 */
.table-card {
  border-radius: 12px;
  overflow: hidden;
}

.loading-container {
  padding: 24px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 24px;
  color: #6b7280;
}

.empty-icon {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-state p {
  margin: 0;
}

/* 评论卡片 */
.comment-card {
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  border-left: 4px solid #3b82f6;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 500;
  color: #374151;
  display: flex;
  align-items: center;
  gap: 6px;
}

.comment-time {
  font-size: 12px;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 4px;
}

.comment-content {
  line-height: 1.6;
  word-break: break-word;
  color: #1f2937;
  margin-bottom: 8px;
}

.reply-info {
  padding: 8px;
  background-color: #f3f4f6;
  border-radius: 6px;
  color: #4b5563;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.icon-inline {
  display: inline;
  vertical-align: text-bottom;
}

/* 状态标签 */
.status-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 10px;
  border-radius: 20px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-wrap: wrap;
  flex-direction: column;
  gap: 8px;
}

.action-buttons .el-button {
  margin-left: 0;
}

/* 批量操作 */
.batch-actions {
  margin-top: 16px;
  padding: 16px;
  background-color: #f0f9ff;
  border-radius: 8px;
  border: 1px solid #bae6fd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-count {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #0369a1;
  font-size: 14px;
}

.count-number {
  font-weight: 600;
  font-size: 16px;
}

.batch-buttons {
  display: flex;
  gap: 12px;
}

/* 分页 */
.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

/* 链接样式 */
.post-link {
  color: #3b82f6;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: color 0.2s;
}

.post-link:hover {
  color: #2563eb;
  text-decoration: underline;
}

/* 回复对话框 */
.reply-dialog-content {
  padding: 0 20px;
}

.original-comment {
  padding: 16px;
  background: #f3f4f6;
  border-radius: 8px;
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .comment-management {
    padding: 16px;
  }

  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .filter-container {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .filter-item, .date-picker {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }

  .batch-actions {
    flex-direction: column;
    gap: 16px;
  }

  .batch-buttons {
    flex-wrap: wrap;
  }
}
</style>