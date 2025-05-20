<template>
  <!-- Category Navigation -->
  <div class="category-nav">
    <el-button type="primary" plain>全部</el-button>
    <el-button plain>资讯</el-button>
    <el-button plain>技术</el-button>
    <el-button plain>运维</el-button>
    <el-button plain>人工智能</el-button>
    <el-button plain>Java</el-button>
    <el-button plain>Python</el-button>
    <el-button plain>前端</el-button>
    <el-button plain>后端</el-button>
  </div>

  <!-- Banner Area -->
  <div class="banner-area">
    <el-carousel height="180px">
      <el-carousel-item>
        <div class="banner-item">
          <img src="../assets/svgs/placeholder.svg?height=180&width=800" alt="Banner" class="banner-image">
        </div>
      </el-carousel-item>
      <el-carousel-item>
        <div class="banner-item">
          <img src="../assets/svgs/placeholder.svg?height=180&width=800" alt="Banner" class="banner-image">
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>

  <!-- News Section -->
  <div class="section">
    <div class="section-header">
      <h2 class="section-title">资讯头条</h2>
      <a href="#" class="more-link">更多资讯 <el-icon><ArrowRight /></el-icon></a>
    </div>

    <div class="news-grid">
      <div class="news-card" v-for="i in 4" :key="i">
        <div class="news-image">
          <img src="../assets/svgs/placeholder.svg?height=120&width=220" alt="News Image">
        </div>
        <div class="news-title">技术文章标题示例</div>
      </div>
    </div>

    <div class="news-list">
      <div class="news-item" v-for="i in 4" :key="i">
        <el-icon><InfoFilled /></el-icon>
        <span class="news-text">这是一条技术新闻的标题，内容可能很长很长很长很长很长很长</span>
      </div>
    </div>
  </div>

  <!-- Projects Section -->
  <div class="section">
    <div class="section-header">
      <h2 class="section-title">开源项目</h2>
      <a href="#" class="more-link">更多开源项目 <el-icon><ArrowRight /></el-icon></a>
    </div>

    <div class="project-grid">
      <div class="project-card" v-for="i in 2" :key="i">
        <div class="project-logo">
          <img src="../assets/svgs/placeholder.svg?height=80&width=80" alt="Project Logo">
        </div>
        <div class="project-info">
          <h3 class="project-title">开源项目名称</h3>
          <p class="project-desc">项目简短描述信息</p>
          <div class="project-meta">
            <span class="project-tag">web</span>
            <span class="project-stars">8.5K</span>
          </div>
        </div>
        <div class="project-action">
          <el-button link>查看详情 <el-icon><ArrowRight /></el-icon></el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import request from '@/utils/request';
import { ElMessage } from 'element-plus';

// request.get('/user/selectAll').then(res => {
//   if (res.code === '200') {
//     console.log(res);
//   } else {
//     ElMessage.error(res.msg);
//   }
// })
//
// request.get('/user/selectPage').then(res => {
//   if (res.code === '200') {
//     console.log(res);
//   } else {
//     ElMessage.error(res.msg);
//   }
// })
</script>

<style scoped>
/* Content Styles */
.category-nav {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  overflow-x: auto;
  padding-bottom: 10px;
}

.banner-area {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* Section Styles */
.section {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.more-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  display: flex;
  align-items: center;
}

/* News Styles */
.news-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.news-card {
  border-radius: 6px;
  overflow: hidden;
  transition: transform 0.2s;
}

.news-card:hover {
  transform: translateY(-3px);
}

.news-image img {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.news-title {
  padding: 10px;
  font-size: 14px;
  font-weight: 500;
}

.news-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.news-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.news-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Project Styles */
.project-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.project-card {
  display: flex;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  transition: box-shadow 0.2s;
}

.project-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.project-logo {
  margin-right: 15px;
}

.project-logo img {
  width: 60px;
  height: 60px;
  border-radius: 8px;
}

.project-info {
  flex: 1;
}

.project-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 5px 0;
}

.project-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 10px 0;
}

.project-meta {
  display: flex;
  align-items: center;
  gap: 15px;
}

.project-tag {
  background-color: #f0f2f5;
  color: #666;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
}

.project-stars {
  font-size: 12px;
  color: #666;
}

.project-action {
  display: flex;
  align-items: center;
}
</style>