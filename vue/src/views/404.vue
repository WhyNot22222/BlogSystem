<template>
  <div class="container">
    <div class="content">
      <h1 class="error-code">404</h1>
      <h2 class="message">页面未找到</h2>
      <p class="description">您访问的页面可能已丢失在数字宇宙中...</p>
      <router-link to="/home" class="home-link">
        <button class="return-btn">返回首页</button>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 2rem;
}

.content {
  text-align: center;
  max-width: 600px;
  padding: 2rem;
  border-radius: 12px;
  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.error-code {
  font-size: 8rem;
  color: #e74c3c;
  margin: 0;
  line-height: 1;
  font-weight: 700;
}

.message {
  font-size: 2rem;
  color: #2c3e50;
  margin: 1rem 0;
}

.description {
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.return-btn {
  background-color: #3498db;
  color: white;
  border: none;
  padding: 1rem 2rem;
  font-size: 1.1rem;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.return-btn:hover {
  background-color: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

@media (max-width: 768px) {
  .error-code {
    font-size: 6rem;
  }

  .message {
    font-size: 1.5rem;
  }
}
</style>