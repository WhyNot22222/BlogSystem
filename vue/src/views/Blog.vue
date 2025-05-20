<template>
  <div class="blog-page">
    <!-- Category Navigation -->
    <div class="category-nav">
      <div class="category-nav-left">
        <el-button type="primary" class="category-btn active">推荐</el-button>
        <el-button class="category-btn">付费专栏</el-button>
        <el-button class="category-btn">VIP文章</el-button>
      </div>
    </div>

    <!-- Main Content Area -->
    <div class="content-container">
      <!-- Blog List -->
      <div class="blog-list">
        <!-- Blog Item 1 -->
        <div class="blog-item">
          <div class="author-info">
            <el-avatar :size="36" src="/placeholder.svg?height=36&width=36" class="author-avatar"></el-avatar>
            <span class="author-name">源鸿星空</span>
          </div>

          <div class="blog-content">
            <h2 class="blog-title">PyTorch 入门与核心概念详解：从基础到实战问题解决</h2>
            <p class="blog-desc">用PyTorch 编写 Transformer 模型时遇到了多个错误，包括维度不匹配、NaN 损失、注意力权重未记录等问题...</p>

            <div class="blog-meta">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                阅读 760
              </span>
              <span class="meta-item">
                <el-icon><Thumb /></el-icon>
                38 赞
              </span>
              <span class="meta-item">
                <el-icon><Collection /></el-icon>
                收藏 33
              </span>
              <el-dropdown>
                <span class="more-actions">
                  <el-icon><MoreFilled /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>举报</el-dropdown-item>
                    <el-dropdown-item>不感兴趣</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>

        <!-- Blog Item 2 -->
        <div class="blog-item">
          <div class="author-info">
            <el-avatar :size="36" src="/placeholder.svg?height=36&width=36" class="author-avatar"></el-avatar>
            <span class="author-name">CS创新实验室</span>
          </div>

          <div class="blog-content">
            <h2 class="blog-title">408考研经题详解：2009年第10题</h2>
            <p class="blog-desc">所以，如果用二路归并排序算法，第二趟排序之后所得到的序列中，前 4 个关键字应该是有序的，但是...</p>

            <div class="blog-meta">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                阅读 448
              </span>
              <span class="meta-item">
                <el-icon><Thumb /></el-icon>
                9 赞
              </span>
              <span class="meta-item">
                <el-icon><Collection /></el-icon>
                收藏 5
              </span>
              <el-dropdown>
                <span class="more-actions">
                  <el-icon><MoreFilled /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>举报</el-dropdown-item>
                    <el-dropdown-item>不感兴趣</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>

        <!-- Blog Item 3 with Advertisement -->
        <div class="blog-item ad-item">
          <div class="blog-content">
            <h2 class="blog-title">码住了！一文教你玩好豆包AI编程，编程效率原地起飞</h2>
            <p class="blog-desc">看不懂代码？跟它！写不动代码？问豆包！AI助力，编程从此so easy!</p>

            <div class="ad-banner">
              <img src="../assets/svgs/placeholder.svg?height=80&width=80 " alt="Advertisement" class="ad-image">
            </div>
          </div>
        </div>

        <!-- Blog Item 4 -->
        <div class="blog-item">
          <div class="author-info">
            <el-avatar :size="36" src="/placeholder.svg?height=36&width=36" class="author-avatar"></el-avatar>
            <span class="author-name">warm...</span>
          </div>

          <div class="blog-content">
            <h2 class="blog-title">Python基础总结(十)之函数</h2>
            <p class="blog-desc">函数的定义要使用def关键字，def后面紧跟函数名，给出的为函数的代码块，上述即为一个简单的函数，定义了一个名为test的函数...</p>

            <div class="blog-meta">
              <span class="meta-item">
                <el-icon><View /></el-icon>
                阅读 443
              </span>
              <span class="meta-item">
                <el-icon><Thumb /></el-icon>
                4 赞
              </span>
              <span class="meta-item">
                <el-icon><Collection /></el-icon>
                收藏 11
              </span>
              <el-dropdown>
                <span class="more-actions">
                  <el-icon><MoreFilled /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item>举报</el-dropdown-item>
                    <el-dropdown-item>不感兴趣</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  ArrowRight,
  View,
  Collection,
  MoreFilled
} from '@element-plus/icons-vue'
</script>

<style scoped>
.blog-page {
  width: 100%;
}

/* Category Navigation */
.category-nav {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.category-nav-left {
  display: flex;
  gap: 10px;
}

.category-btn {
  font-weight: 500;
}

.category-btn.active {
  background-color: #e83929;
  border-color: #e83929;
  color: white;
}


/* Content Container */
.content-container {
  display: flex;
  gap: 20px;
}

/* Blog List */
.blog-list {
  flex: 1;
}

.blog-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.author-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 10px;
}

.author-name {
  font-weight: 500;
  color: #333;
}

.blog-content {
  flex: 1;
}

.blog-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 10px 0;
  color: #333;
  line-height: 1.4;
}

.blog-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 15px 0;
  line-height: 1.6;
}

.blog-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #999;
  font-size: 13px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.more-actions {
  cursor: pointer;
  color: #999;
}

/* Ad Item */
.ad-item {
  border: 1px solid #eee;
  background-color: #fafafa;
}

.ad-banner {
  margin-top: 10px;
  border-radius: 4px;
  overflow: hidden;
}

.ad-image {
  width: 5%;
  height: auto;
  object-fit: cover;
}

/* Responsive Adjustments */
@media (max-width: 1200px) {
  .content-container {
    flex-direction: column;
  }

  .sidebar-right {
    width: 100%;
  }

  .author-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .author-card {
    border-bottom: none;
  }
}

@media (max-width: 768px) {
  .author-list {
    grid-template-columns: 1fr;
  }
}
</style>