<!-- FavoritesManager.vue -->
<template>
  <!-- 收藏夹选择模态框 -->
  <el-dialog
      v-model="showFavoritesModal"
      title="添加收藏夹"
      width="480px"
      :show-close="true"
      :close-on-click-modal="false"
      :close-on-press-escape="true"
      class="favorites-modal"
      @close="resetState"
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
</template>

<script setup>
import { ref, reactive, defineProps, defineEmits, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  visible: Boolean,
  postId: Number,
  userId: Number
})

const emit = defineEmits(['update:visible', 'favorited', 'folder-created', 'close'])

// 收藏夹数据
const favoriteFolders = ref([])
const showFavoritesModal = ref(props.visible)
const showCreateDialog = ref(false)
const createLoading = ref(false)

const newFolder = reactive({
  name: '',
  isPrivate: false,
  description: ''
})

// 获取收藏夹列表
const fetchFavoriteFolders = async () => {
  try {
    const res = await request.get('/favorites/collections/list', {
      params: { userId: props.userId }
    })

    if (res.code === '200') {
      const baseFolders = res.data;

      const foldersWithCount = await Promise.all(
          baseFolders.map(async item => {
            try {
              const countRes = await request.get(`/favorites/collection/count`, {
                params: { collectionId: item.id }
              });
              const collectionRes = await request.get('/favorites/collections', {
                params: { collectionId: item.id }
              })

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
        isRecentlyUsed: item.lastUsedTime > Date.now() - 7 * 86400000,
        loading: false
      }));
    }
  } catch (error) {
    ElMessage.error('获取收藏夹列表失败');
    console.error('API请求失败:', error);
  }
};

// 显示创建收藏夹对话框
const showCreateFolderDialog = () => {
  showCreateDialog.value = true
}

// 添加到收藏夹
const addToFolder = async (folder) => {
  if (!props.postId) return

  folder.loading = true
  try {
    const res = await request.post('/favorites/add', {
      collectionId: folder.id,
      postId: props.postId,
      userId: props.userId,
      createdAt: new Date().toISOString()
    });

    if (res.code === '200') {
      folder.itemCount++
      emit('favorited', {
        folderId: folder.id,
        folderName: folder.name
      })
      ElMessage.success(`已收藏到"${folder.name}"`);
      showFavoritesModal.value = false
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

  try {
    const res = await request.post('/favorites/collection', null, {
      params: {
        name: newFolder.name,
        description: newFolder.description,
        isPublic: !newFolder.isPrivate,
        userId: props.userId
      }
    })

    if (res.code == '200') {
      const newFolderData = {
        id: res.data.id,
        name: res.data.name,
        itemCount: 0,
        isPrivate: !res.data.isPublic,
        isRecentlyUsed: true,
        loading: false
      }
      favoriteFolders.value.unshift(newFolderData)
      newFolder.name = ''
      newFolder.isPrivate = false
      newFolder.description = ''

      showCreateDialog.value = false
      ElMessage.success('收藏夹创建成功')
      emit('folder-created', newFolderData)
    }
  } catch (error) {
    console.error('创建失败:', error)
    ElMessage.error(error.response?.data?.message || '创建失败，请重试')
  } finally {
    createLoading.value = false
  }
}

// 重置状态
const resetState = () => {
  newFolder.name = ''
  newFolder.isPrivate = false
  newFolder.description = ''
  showCreateDialog.value = false
  emit('close')
}

// 当props.visible变化时更新内部状态
watch(() => props.visible, (val) => {
  showFavoritesModal.value = val
  if (val) {
    fetchFavoriteFolders()
  }
})

// 当内部状态变化时通知父组件
watch(showFavoritesModal, (val) => {
  if (!val) {
    emit('update:visible', false)
  }
})
</script>

<style scoped>
/* 样式与Community.vue中收藏夹部分相同 */
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