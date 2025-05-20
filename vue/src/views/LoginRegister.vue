<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <div class="logo-container">
          <span class="logo-icon">📚</span>
          <span class="logo-text">MyBLOG</span>
        </div>

        <div class="auth-tabs">
          <button
              class="tab-btn"
              :class="{ 'active': activeTab === 'login' }"
              @click="activeTab = 'login'"
          >
            登录
          </button>
          <button
              class="tab-btn"
              :class="{ 'active': activeTab === 'register' }"
              @click="activeTab = 'register'"
          >
            注册
          </button>
        </div>
      </div>

      <div class="auth-body">
        <!-- Login Form -->
        <form v-if="activeTab === 'login'" class="auth-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="login-username">用户名/邮箱</label>
            <div class="input-with-icon">
              <el-icon><User /></el-icon>
              <input
                  id="login-username"
                  v-model="loginForm.username"
                  type="text"
                  placeholder="请输入用户名或邮箱"
                  required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="login-password">密码</label>
            <div class="input-with-icon">
              <el-icon><Lock /></el-icon>
              <input
                  id="login-password"
                  v-model="loginForm.password"
                  :type="showLoginPassword ? 'text' : 'password'"
                  placeholder="请输入密码"
                  required
              />
              <button
                  type="button"
                  class="toggle-password"
                  @click="showLoginPassword = !showLoginPassword"
              >
                <el-icon v-if="showLoginPassword"><Hide /></el-icon>
              </button>
            </div>
          </div>

          <div class="form-options">
            <div class="remember-me">
              <input id="remember-me" type="checkbox" v-model="loginForm.rememberMe" />
              <label for="remember-me">记住我</label>
            </div>
            <a href="#" class="forgot-password">忘记密码?</a>
          </div>

          <button type="submit" class="submit-btn">登录</button>
        </form>

        <!-- Register Form -->
        <form v-else class="auth-form" @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="register-username">用户名</label>
            <div class="input-with-icon">
              <el-icon><User /></el-icon>
              <input
                  id="register-username"
                  v-model="registerForm.username"
                  type="text"
                  placeholder="请设置用户名"
                  required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="register-email">邮箱</label>
            <div class="input-with-icon">
              <el-icon><Message /></el-icon>
              <input
                  id="register-email"
                  v-model="registerForm.email"
                  type="email"
                  placeholder="请输入邮箱"
                  required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="register-password">密码</label>
            <div class="input-with-icon">
              <el-icon><Lock /></el-icon>
              <input
                  id="register-password"
                  v-model="registerForm.password"
                  :type="showRegisterPassword ? 'text' : 'password'"
                  placeholder="请设置密码"
                  required
              />
              <button
                  type="button"
                  class="toggle-password"
                  @click="showRegisterPassword = !showRegisterPassword"
              >
                <el-icon v-if="showRegisterPassword"><Hide /></el-icon>
              </button>
            </div>
          </div>

          <div class="form-group">
            <label for="register-confirm-password">确认密码</label>
            <div class="input-with-icon">
              <el-icon><Lock /></el-icon>
              <input
                  id="register-confirm-password"
                  v-model="registerForm.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  placeholder="请再次输入密码"
                  required
              />
              <button
                  type="button"
                  class="toggle-password"
                  @click="showConfirmPassword = !showConfirmPassword"
              >
                <el-icon v-if="showConfirmPassword"><Hide /></el-icon>
              </button>
            </div>
          </div>

          <div class="form-options">
            <div class="agreement">
              <input id="agreement" type="checkbox" v-model="registerForm.agreement" required />
              <label for="agreement">我已阅读并同意 <a href="#">服务条款</a> 和 <a href="#">隐私政策</a></label>
            </div>
          </div>

          <button type="submit" class="submit-btn">注册</button>
        </form>
      </div>

      <div class="auth-footer">
        <div class="brand-logo">
          <div class="logo-triangle"></div>
          <div class="logo-text">
            <span class="brand-name">NUAA_WhyNot</span>
            <span class="trademark">™</span>
          </div>
        </div>
        <div class="copyright">
          © {{ currentYear }} MyBLOG Inc. All rights reserved.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import {
  User,
  Lock,
  View,
  Hide,
  Message,
  ChatDotRound,
  Connection,
  Share
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

// Active tab state
const activeTab = ref('login')

// Login form
const loginForm = ref({
  username: '',
  password: '',
  rememberMe: false
})

// Register form
const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

// Password visibility
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)
const showConfirmPassword = ref(false)

// Current year for copyright
const currentYear = computed(() => new Date().getFullYear())

// Form submission handlers
const handleLogin = () => {
  console.log('Login form submitted:', loginForm.value)
  const loginRequest = {
    username: loginForm.value.username,
    password: loginForm.value.password
  };

  request.post('/user/login', loginRequest).then(res => {
    if (res.code === '200') {
      console.log('Backend response data:', res.data); // Log the backend response
      // 使用 res.data.id (根据截图，后端返回的 ID 键名为 'id')
      localStorage.setItem('userId', res.data.id) 
      localStorage.setItem('token', res.data.token) // 假设 token 也在 res.data 中
      
      // res.data 本身包含 'id' 键，可以直接用于提交
      store.commit('SET_USER', res.data) 
      
      ElMessage.success('登录成功')
      router.push('/home')
    } else {
        ElMessage.error(res.msg);
    }
})
}

const handleRegister = () => {
  console.log('Register form submitted:', registerForm.value)
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致');
    return;
  }
  const user = {
    username: registerForm.value.username,
    email: registerForm.value.email,
    password: registerForm.value.password
  };
  request.post('/user/register', user).then(res => {
    if (res.code === '200') {
      console.log(res.data);
      ElMessage.success('注册成功，请登录');
      // 重置注册表单
      registerForm.value = {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        agreement: false
      };
    } else {
      // ElMessage.error('注册失败: ' + error.message); // 旧代码，error 未定义
      ElMessage.error('注册失败: ' + res.msg); // 修改：使用 res.msg
    }
  });
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f7f8fa;
  padding: 20px;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
}

.auth-header {
  padding: 24px;
  text-align: center;
  position: relative;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.logo-icon {
  font-size: 24px;
  margin-right: 8px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #2C3E50;
}

.auth-tabs {
  display: flex;
  border-bottom: 1px solid #eee;
}

.tab-btn {
  flex: 1;
  padding: 12px;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.tab-btn.active {
  color: #3CB371;
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #3CB371;
}

.auth-body {
  padding: 24px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.input-with-icon {
  position: relative;
  display: flex;
  align-items: center;
}

.input-with-icon .el-icon {
  position: absolute;
  left: 12px;
  color: #999;
}

.input-with-icon input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.input-with-icon input:focus {
  outline: none;
  border-color: #3CB371;
}

.toggle-password {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 0;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.remember-me, .agreement {
  display: flex;
  align-items: center;
  gap: 6px;
}

.forgot-password {
  color: #3CB371;
  text-decoration: none;
}

.submit-btn {
  padding: 12px;
  background-color: #3CB371;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #329a61;
}

.divider span {
  padding: 0 10px;
  color: #999;
  font-size: 14px;
}


/* New Footer Styles */
.auth-footer {
  padding: 20px;
  background: linear-gradient(90deg, #f7f8fa 0%, #f0f2f5 100%);
  border-top: 1px solid #eee;
  text-align: center;
}

.brand-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.logo-triangle {
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 12px 20px 12px;
  border-color: transparent transparent #3CB371 transparent;
  margin-right: 6px;
  position: relative;
}

.logo-triangle::after {
  content: '';
  position: absolute;
  top: 4px;
  left: -8px;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 8px 14px 8px;
  border-color: transparent transparent #2C3E50 transparent;
}

.logo-text {
  display: flex;
  align-items: baseline;
}

.brand-name {
  font-weight: bold;
  font-size: 18px;
  color: #2C3E50;
  letter-spacing: 0.5px;
}

.trademark {
  font-size: 10px;
  vertical-align: super;
  margin-left: 2px;
  color: #3CB371;
}

.copyright {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

/* Responsive adjustments */
@media (max-width: 576px) {
  .auth-card {
    max-width: 100%;
  }

  .form-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>