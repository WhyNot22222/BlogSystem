<template>
  <div class="container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>

    <div class="content">
      <div class="content-grid">
        <!-- 左侧404数字 -->
        <div class="error-section">
          <h1 class="error-code">404</h1>
        </div>

        <!-- 右侧内容 -->
        <div class="info-section">
          <h2 class="message">页面未找到</h2>

          <p class="description">
            您访问的页面可能已丢失在数字宇宙中...
            <br>
            <span class="sub-description">让我们帮您找到回家的路</span>
          </p>

          <div class="actions">
            <router-link to="/home" class="home-link">
              <button class="return-btn primary">
                <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                  <polyline points="9,22 9,12 15,12 15,22"/>
                </svg>
                返回首页
              </button>
            </router-link>

            <button class="return-btn secondary" @click="goBack">
              <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="15,18 9,12 15,6"/>
              </svg>
              返回上页
            </button>
          </div>
        </div>
      </div>

      <!-- 底部提示 -->
      <div class="help-text">
        <p>如果您认为这是一个错误，请联系我们的技术支持: yanNuo222@nuaa.edu.cn</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFoundPage',
  methods: {
    goBack() {
      if (window.history.length > 1) {
        this.$router.go(-1);
      } else {
        this.$router.push('/home');
      }
    }
  }
}
</script>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  top: 10%;
  right: 30%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.content {
  text-align: center;
  width: 800px;
  max-width: 90%;
  padding: 2.5rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  position: relative;
  animation: fadeIn 0.8s ease-out;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 2rem;
  align-items: center;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.error-section {
  display: flex;
  justify-content: center;
  align-items: center;
  padding-right: 2rem;
  border-right: 1px solid #ecf0f1;
}

.error-code {
  font-size: 8rem;
  margin: 0;
  line-height: 1;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.info-section {
  text-align: left;
}

.message {
  font-size: 2.2rem;
  color: #2c3e50;
  margin: 0 0 1rem 0;
  font-weight: 600;
}

.description {
  color: #7f8c8d;
  margin-bottom: 2rem;
  font-size: 1.1rem;
  line-height: 1.6;
}

.sub-description {
  display: block;
  margin-top: 0.5rem;
  font-size: 0.9rem;
  color: #95a5a6;
  font-style: italic;
}

.actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.home-link {
  text-decoration: none;
}

.return-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  border: none;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  white-space: nowrap;
}

.return-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.return-btn:hover::before {
  left: 100%;
}

.primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  position: relative;
  overflow: hidden;
}

.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
}

.secondary {
  background: transparent;
  color: #667eea;
  border: 2px solid #667eea;
  position: relative;
  overflow: hidden;
}

.secondary:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-3px);
}

.btn-icon {
  width: 18px;
  height: 18px;
}

.help-text {
  padding-top: 1.5rem;
  border-top: 1px solid #ecf0f1;
  margin-top: 1.5rem;
}

.help-text p {
  color: #95a5a6;
  font-size: 0.9rem;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 1rem;
  }

  .content {
    padding: 2rem;
  }

  .content-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .error-section {
    padding-right: 0;
    border-right: none;
    border-bottom: 1px solid #ecf0f1;
    padding-bottom: 1.5rem;
  }

  .error-code {
    font-size: 6rem;
  }

  .info-section {
    text-align: center;
    padding-top: 1.5rem;
  }

  .message {
    font-size: 1.8rem;
  }

  .actions {
    justify-content: center;
    flex-wrap: wrap;
  }
}

@media (max-width: 480px) {
  .content {
    padding: 1.5rem;
  }

  .error-code {
    font-size: 5rem;
  }

  .message {
    font-size: 1.5rem;
  }

  .description {
    font-size: 1rem;
  }

  .actions {
    flex-direction: column;
    align-items: stretch;
  }

  .return-btn {
    justify-content: center;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .content {
    background: rgba(30, 30, 30, 0.95);
  }

  .message {
    color: #ecf0f1;
  }

  .error-section {
    border-right-color: #34495e;
  }

  .help-text {
    border-top-color: #34495e;
  }

  @media (max-width: 768px) {
    .error-section {
      border-bottom-color: #34495e;
    }
  }
}
</style>