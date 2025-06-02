<template>
  <div class="comment-management">
    <!-- 搜索和筛选区域 -->
    <el-card shadow="never" class="search-card">
      <div class="filter-container">
        <el-input
            v-model="searchParams.keyword"
            placeholder="搜索评论内容"
            class="filter-item"
            clearable
            @clear="loadComments"
            @keyup.enter="loadComments"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
            v-model="searchParams.status"
            placeholder="状态筛选"
            class="filter-item"
            clearable
            @change="loadComments"
        >
          <el-option label="待审核" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="未通过" value="2" />
        </el-select>

        <el-date-picker
            v-model="searchParams.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            class="filter-item"
            @change="loadComments"
        />

        <el-button
            type="primary"
            class="filter-item"
            @click="loadComments"
        >
          搜索
        </el-button>
      </div>
    </el-card>

    <!-- 评论数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table
          :data="comments"
          v-loading="loading"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="评论内容" min-width="250">
          <template #default="{ row }">
            <div class="comment-content">
              {{ row.content }}
            </div>
            <div v-if="row.replyTo" class="reply-info">
              回复: @{{ row.replyTo }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="articleTitle" label="所属文章" width="200" />
        <el-table-column prop="author" label="评论者" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评论时间" width="180" />
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button
                v-if="row.status === 0"
                type="success"
                size="small"
                @click="handleAudit(row, 1)"
            >
              通过
            </el-button>
            <el-button
                v-if="row.status === 0"
                type="warning"
                size="small"
                @click="handleAudit(row, 2)"
            >
              拒绝
            </el-button>
            <el-button
                type="danger"
                size="small"
                @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div v-if="selectedComments.length > 0" class="batch-actions">
        <el-popconfirm
            title="确定批量通过选中的评论吗？"
            @confirm="batchAudit(1)"
        >
          <template #reference>
            <el-button type="success" size="small">批量通过</el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm
            title="确定批量拒绝选中的评论吗？"
            @confirm="batchAudit(2)"
        >
          <template #reference>
            <el-button type="warning" size="small">批量拒绝</el-button>
          </template>
        </el-popconfirm>
        <el-popconfirm
            title="确定批量删除选中的评论吗？"
            @confirm="batchDelete"
        >
          <template #reference>
            <el-button type="danger" size="small">批量删除</el-button>
          </template>
        </el-popconfirm>
        <span class="batch-count">已选择 {{ selectedComments.length }} 项</span>
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import request from '@/utils/request';

// 搜索参数
const searchParams = ref({
  keyword: '',
  status: '',
  dateRange: []
});

// 评论数据
const comments = ref([]);
const selectedComments = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 加载评论数据
const loadComments = async () => {
  try {
    loading.value = true;

    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchParams.value.keyword,
      status: searchParams.value.status
    };

    // 添加日期范围参数
    if (searchParams.value.dateRange && searchParams.value.dateRange.length === 2) {
      params.startTime = searchParams.value.dateRange[0].getTime();
      params.endTime = searchParams.value.dateRange[1].getTime();
    }

    const response = await request.get('/manage/comments', { params });

    if (response.code === '200') {
      comments.value = response.data.records;
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

// 状态显示转换
const statusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '未通过'
  };
  return statusMap[status] || '未知状态';
};

const statusTagType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  };
  return typeMap[status] || '';
};

// 处理审核操作
const handleAudit = async (comment, status) => {
  try {
    const response = await request.post('/manage/comments/audit', {
      commentId: comment.id,
      status
    });

    if (response.code === '200') {
      ElMessage.success('操作成功');
      // 更新本地状态
      comment.status = status;
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

    const response = await request.delete(`/manage/comments/${comment.id}`);

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
    const response = await request.post('/manage/comments/batch-audit', {
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
    const response = await request.post('/manage/comments/batch-delete', { ids });

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

// 初始化加载
onMounted(() => {
  loadComments();
});
</script>

<style scoped>
.comment-management {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-item {
  width: 200px;
}

.table-card {
  margin-bottom: 20px;
}

.comment-content {
  line-height: 1.5;
  word-break: break-word;
}

.reply-info {
  margin-top: 5px;
  padding: 4px 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  color: #606266;
  font-size: 12px;
}

.batch-actions {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.batch-count {
  margin-left: 10px;
  color: #606266;
  font-size: 14px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>