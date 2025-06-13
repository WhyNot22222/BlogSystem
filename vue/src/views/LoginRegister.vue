<template>
  <div class="auth-container">
    <!-- 背景装饰元素 -->
    <div class="background-decoration">
      <div class="floating-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
        <div class="shape shape-6"></div>
      </div>
      <div class="gradient-orbs">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
      </div>
    </div>

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
              v-if="!showForgotPassword"
          >
            登录
          </button>
          <button
              class="tab-btn"
              :class="{ 'active': activeTab === 'register' }"
              @click="activeTab = 'register'"
              v-if="!showForgotPassword"
          >
            注册
          </button>
          <button
              class="tab-btn active"
              v-if="showForgotPassword"
          >
            找回密码
          </button>
        </div>
      </div>

      <div class="auth-body">
        <!-- Login Form -->
        <form v-if="activeTab === 'login' && !showForgotPassword" class="auth-form" @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="login-username">用户名</label>
            <div class="input-with-icon">
              <el-icon class="input-icon"><User /></el-icon>
              <input
                  id="login-username"
                  v-model="loginForm.username"
                  type="text"
                  placeholder="请输入用户名"
                  required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="login-password">密码</label>
            <div class="input-with-icon">
              <el-icon class="input-icon"><Lock /></el-icon>
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
                <el-icon>
                  <View v-if="!showLoginPassword" />
                  <Hide v-else />
                </el-icon>
              </button>
            </div>
          </div>

          <div class="form-options">
            <div class="remember-me">
              <input id="remember-me" type="checkbox" v-model="loginForm.rememberMe" />
              <label for="remember-me">记住我</label>
            </div>
            <a href="#" class="forgot-password" @click.prevent="showForgotPassword = true">忘记密码?</a>
          </div>

          <button type="submit" class="submit-btn">登录</button>
        </form>

        <!-- Register Form -->
        <form v-else-if="activeTab === 'register' && !showForgotPassword" class="auth-form" @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="register-username">用户名</label>
            <div class="input-with-icon">
              <el-icon class="input-icon"><User /></el-icon>
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
              <el-icon class="input-icon"><Message /></el-icon>
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
              <el-icon class="input-icon"><Lock /></el-icon>
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
                <el-icon>
                  <View v-if="!showRegisterPassword" />
                  <Hide v-else />
                </el-icon>
              </button>
            </div>
          </div>

          <div class="form-group">
            <label for="register-confirm-password">确认密码</label>
            <div class="input-with-icon">
              <el-icon class="input-icon"><Lock /></el-icon>
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
                <el-icon>
                  <View v-if="!showConfirmPassword" />
                  <Hide v-else />
                </el-icon>
              </button>
            </div>
          </div>

          <div class="form-options">
            <div class="agreement">
              <input id="agreement" type="checkbox" v-model="registerForm.agreement" required />
              <label for="agreement">
                我已阅读并同意
                <a href="#" @click.prevent="showTerms = true">服务条款</a>
                和
                <a href="#" @click.prevent="showPrivacy = true">隐私政策</a>
              </label>
            </div>
          </div>

          <button type="submit" class="submit-btn">注册</button>
        </form>

        <!-- Forgot Password Form -->
        <div v-else-if="showForgotPassword" class="forgot-password-container">
          <!-- Step 1: Enter Email -->
          <div v-if="forgotPasswordStep === 1" class="forgot-step">
            <div class="step-content">
              <div class="form-group">
                <label for="forgot-email">邮箱地址</label>
                <div class="input-with-icon">
                  <el-icon class="input-icon"><Message /></el-icon>
                  <input
                      id="forgot-email"
                      v-model="forgotPasswordForm.email"
                      type="email"
                      placeholder="请输入注册时使用的邮箱"
                      required
                  />
                </div>
              </div>
              <button
                  type="button"
                  class="submit-btn full-width"
                  @click="sendVerificationCode"
                  :disabled="sendingCode"
              >
                {{ sendingCode ? '发送中...' : '发送验证码' }}
              </button>
            </div>
          </div>

          <!-- Step 2: Enter Verification Code -->
          <div v-else-if="forgotPasswordStep === 2" class="forgot-step">
            <div class="verification-info">
              <div class="info-card">
                <el-icon class="info-icon"><SuccessFilled /></el-icon>
                <p class="info-text">验证码已发送至：{{ maskEmail(forgotPasswordForm.email) }}</p>
                <p class="countdown-text" v-if="countdown > 0">{{ countdown }}秒后可重新发送</p>
              </div>
            </div>

            <div class="step-content">
              <div class="form-group">
                <label for="verification-code">验证码</label>
                <div class="input-with-icon">
                  <el-icon class="input-icon"><Key /></el-icon>
                  <input
                      id="verification-code"
                      v-model="forgotPasswordForm.verificationCode"
                      type="text"
                      placeholder="请输入6位验证码"
                      maxlength="6"
                      required
                  />
                </div>
              </div>

              <div class="button-group">
                <button type="button" class="submit-btn primary" @click="verifyCode">验证</button>
                <button
                    type="button"
                    class="submit-btn secondary"
                    @click="sendVerificationCode"
                    :disabled="countdown > 0 || sendingCode"
                >
                  {{ sendingCode ? '发送中...' : '重新发送' }}
                </button>
              </div>
            </div>
          </div>

          <!-- Step 3: Reset Password -->
          <div v-else-if="forgotPasswordStep === 3" class="forgot-step">
            <div class="step-content">
              <div class="form-group">
                <label for="new-password">新密码</label>
                <div class="input-with-icon">
                  <el-icon class="input-icon"><Lock /></el-icon>
                  <input
                      id="new-password"
                      v-model="forgotPasswordForm.newPassword"
                      :type="showNewPassword ? 'text' : 'password'"
                      placeholder="请输入新密码"
                      required
                  />
                  <button
                      type="button"
                      class="toggle-password"
                      @click="showNewPassword = !showNewPassword"
                  >
                    <el-icon>
                      <View v-if="!showNewPassword" />
                      <Hide v-else />
                    </el-icon>
                  </button>
                </div>
              </div>

              <div class="form-group">
                <label for="confirm-new-password">确认新密码</label>
                <div class="input-with-icon">
                  <el-icon class="input-icon"><Lock /></el-icon>
                  <input
                      id="confirm-new-password"
                      v-model="forgotPasswordForm.confirmNewPassword"
                      :type="showConfirmNewPassword ? 'text' : 'password'"
                      placeholder="请再次输入新密码"
                      required
                  />
                  <button
                      type="button"
                      class="toggle-password"
                      @click="showConfirmNewPassword = !showConfirmNewPassword"
                  >
                    <el-icon>
                      <View v-if="!showConfirmNewPassword" />
                      <Hide v-else />
                    </el-icon>
                  </button>
                </div>
              </div>

              <button type="button" class="submit-btn full-width" @click="resetPassword">重置密码</button>
            </div>
          </div>

          <div class="back-to-login">
            <a href="#" @click.prevent="backToLogin">
              <el-icon><ArrowLeft /></el-icon>
              返回登录
            </a>
          </div>
        </div>
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

    <!-- Terms of Service Modal -->
    <div v-if="showTerms" class="modal-overlay" @click="showTerms = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>服务条款</h2>
          <button class="close-btn" @click="showTerms = false">
            <el-icon><Close /></el-icon>
          </button>
        </div>
        <div class="modal-body">
          <div class="terms-content">
            <h3>1. 服务条款的接受</h3>
            <p>欢迎使用MyBLOG服务。通过访问和使用本网站，您同意遵守并受以下服务条款的约束。如果您不同意这些条款，请不要使用本服务。</p>

            <h3>2. 服务描述</h3>
            <p>MyBLOG是一个博客平台，允许用户创建、发布和分享内容。我们保留随时修改或终止服务的权利，恕不另行通知。</p>

            <h3>3. 用户账户</h3>
            <p>为了使用某些功能，您需要创建一个账户。您有责任：</p>
            <ul>
              <li>提供准确、完整的注册信息</li>
              <li>维护账户信息的准确性</li>
              <li>保护您的密码和账户安全</li>
              <li>对您账户下的所有活动负责</li>
            </ul>

            <p class="last-updated">最后更新：{{ currentYear }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Privacy Policy Modal -->
    <div v-if="showPrivacy" class="modal-overlay" @click="showPrivacy = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>隐私政策</h2>
          <button class="close-btn" @click="showPrivacy = false">
            <el-icon><Close /></el-icon>
          </button>
        </div>
        <div class="modal-body">
          <div class="privacy-content">
            <h3>1. 信息收集</h3>
            <p>我们收集以下类型的信息：</p>
            <ul>
              <li><strong>个人信息：</strong>包括但不限于姓名、邮箱地址、用户名等您主动提供的信息</li>
              <li><strong>使用信息：</strong>包括您如何使用我们的服务、访问时间、页面浏览记录等</li>
              <li><strong>设备信息：</strong>包括IP地址、浏览器类型、操作系统等技术信息</li>
            </ul>

            <h3>2. 信息使用</h3>
            <p>我们使用收集的信息用于：</p>
            <ul>
              <li>提供、维护和改进我们的服务</li>
              <li>处理您的请求和交易</li>
              <li>发送重要通知和更新</li>
              <li>防止欺诈和滥用行为</li>
              <li>遵守法律要求</li>
            </ul>

            <h3>3. 信息分享</h3>
            <p>我们不会出售、交易或转让您的个人信息给第三方，除非：</p>
            <ul>
              <li>获得您的明确同意</li>
              <li>为提供服务所必需（如支付处理）</li>
              <li>遵守法律要求或法院命令</li>
              <li>保护我们的权利、财产或安全</li>
            </ul>

            <h3>4. 数据安全</h3>
            <p>我们采取适当的安全措施来保护您的个人信息：</p>
            <ul>
              <li>使用加密技术保护数据传输</li>
              <li>限制员工对个人信息的访问</li>
              <li>定期审查和更新安全措施</li>
              <li>使用安全的服务器和数据中心</li>
            </ul>

            <h3>5. Cookie使用</h3>
            <p>我们使用Cookie和类似技术来：</p>
            <ul>
              <li>记住您的登录状态和偏好设置</li>
              <li>分析网站使用情况</li>
              <li>提供个性化内容和广告</li>
              <li>改善用户体验</li>
            </ul>
            <p>您可以通过浏览器设置控制Cookie的使用。</p>

            <h3>6. 数据保留</h3>
            <p>我们只在必要的时间内保留您的个人信息：</p>
            <ul>
              <li>账户信息：直到您删除账户</li>
              <li>使用日志：通常保留12个月</li>
              <li>法律要求的信息：按照相关法律规定保留</li>
            </ul>

            <h3>7. 您的权利</h3>
            <p>您对您的个人信息享有以下权利：</p>
            <ul>
              <li>访问和查看您的个人信息</li>
              <li>更正不准确的信息</li>
              <li>删除您的个人信息</li>
              <li>限制信息处理</li>
              <li>数据可携带性</li>
            </ul>

            <h3>8. 儿童隐私</h3>
            <p>我们的服务不面向13岁以下的儿童。我们不会故意收集13岁以下儿童的个人信息。如果我们发现收集了此类信息，将立即删除。</p>

            <h3>9. 国际数据传输</h3>
            <p>您的信息可能会被传输到您所在国家/地区以外的地方进行处理。我们将确保此类传输符合适用的数据保护法律。</p>

            <h3>10. 政策更新</h3>
            <p>我们可能会不时更新此隐私政策。重大更改时，我们会通过网站通知或邮件通知您。</p>

            <h3>11. 联系我们</h3>
            <p>如果您对此隐私政策有任何疑问或需要行使您的权利，请联系我们：</p>
            <p>邮箱：yanNuo222@nuaa.edu.cn</p>
            <p>电话：123-456-7890</p>
            <p>地址：南京航空航天大学（将军路校区）</p>

            <p class="last-updated">最后更新：{{ currentYear }}年</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>import { ref, computed, onMounted, onUnmounted } from 'vue'
import {
  User,
  Lock,
  View,
  Hide,
  Message,
  Key,
  ArrowLeft,
  Close,
  SuccessFilled
} from '@element-plus/icons-vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

const router = useRouter()
const store = useStore()

// Active tab state
const activeTab = ref('login')
const showForgotPassword = ref(false)
const showTerms = ref(false)
const showPrivacy = ref(false)

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

// Forgot password form
const forgotPasswordForm = ref({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmNewPassword: ''
})

// Forgot password state
const forgotPasswordStep = ref(1) // 1: email, 2: verification, 3: new password
const sendingCode = ref(false)
const countdown = ref(0)
let countdownTimer = null

// Password visibility
const showLoginPassword = ref(false)
const showRegisterPassword = ref(false)
const showConfirmPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmNewPassword = ref(false)

// Current year for copyright
const currentYear = computed(() => new Date().getFullYear())

// Utility functions
const maskEmail = (email) => {
  if (!email) return ''
  const [username, domain] = email.split('@')
  if (username.length <= 2) return email
  return username.substring(0, 2) + '***@' + domain
}

const startCountdown = () => {
  countdown.value = 60
  countdownTimer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
    }
  }, 1000)
}

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

const sendVerificationCode = async () => {
  if (!forgotPasswordForm.value.email) {
    ElMessage.error('请输入邮箱地址')
    return
  }

  sendingCode.value = true
  try {
    const response = await request.post('/user/sendVerificationCode', null, {
      params: { email: forgotPasswordForm.value.email }
    })

    if (response.code === '200') {
      ElMessage.success('验证码已发送到您的邮箱')
      forgotPasswordStep.value = 2
      startCountdown()
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    console.log('Failed to send verification code:', error)
    ElMessage.error('发送验证码失败：' + error.message)
  } finally {
    sendingCode.value = false
  }
}

const verifyCode = async () => {
  if (!forgotPasswordForm.value.verificationCode) {
    ElMessage.error('请输入验证码')
    return
  }

  try {
    const response = await request.post('/user/verifyCode', null, {
      params: {
        email: forgotPasswordForm.value.email,
        code: forgotPasswordForm.value.verificationCode
      }
    })

    if (response.code === '200') {
      ElMessage.success('验证码验证成功')
      forgotPasswordStep.value = 3
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    console.log('Failed to verify code:', error)
    ElMessage.error('验证码错误：' + error.message)
  }
}

const resetPassword = async () => {
  if (!forgotPasswordForm.value.newPassword || !forgotPasswordForm.value.confirmNewPassword) {
    ElMessage.error('请输入新密码')
    return
  }

  if (forgotPasswordForm.value.newPassword !== forgotPasswordForm.value.confirmNewPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  try {
    const response = await request.post('/user/resetPassword', {
      email: forgotPasswordForm.value.email,
      newPassword: forgotPasswordForm.value.newPassword,
      confirmPassword: forgotPasswordForm.value.confirmNewPassword
    })

    if (response.code === '200') {
      ElMessage.success('密码重置成功，请使用新密码登录')
      backToLogin()
    } else {
      ElMessage.error(response.msg)
    }
  } catch (error) {
    console.log('Failed to reset password:', error)
    ElMessage.error('密码重置失败：' + error.message)
  }
}

const backToLogin = () => {
  showForgotPassword.value = false
  forgotPasswordStep.value = 1
  forgotPasswordForm.value = {
    email: '',
    verificationCode: '',
    newPassword: '',
    confirmNewPassword: ''
  }
  countdown.value = 0
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
}

// Cleanup
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, rgb(102, 126, 234, 0.9) 0%, rgb(118, 75, 162, 0.9) 100%);
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 20%;
  right: 15%;
  animation-delay: 1s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 30%;
  left: 20%;
  animation-delay: 2s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  right: 10%;
  animation-delay: 3s;
}

.shape-5 {
  width: 40px;
  height: 40px;
  top: 60%;
  left: 5%;
  animation-delay: 4s;
}

.shape-6 {
  width: 90px;
  height: 90px;
  top: 40%;
  right: 5%;
  animation-delay: 5s;
}

.gradient-orbs {
  position: absolute;
  width: 100%;
  height: 100%;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  animation: pulse 4s ease-in-out infinite;
}

.orb-1 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.4) 0%, transparent 70%);
  top: -150px;
  left: -150px;
  animation-delay: 0s;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(118, 75, 162, 0.3) 0%, transparent 70%);
  bottom: -200px;
  right: -200px;
  animation-delay: 2s;
}

.orb-3 {
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
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

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

.auth-card {
  width: 100%;
  max-width: 480px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.auth-header {
  padding: 24px;
  text-align: center;
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
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
  border-bottom: 1px solid rgba(238, 238, 238, 0.5);
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
  background: linear-gradient(90deg, #3CB371, #2E8B57);
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

.input-icon {
  position: absolute;
  left: 12px;
  color: #999;
  z-index: 1;
}

.input-with-icon input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid rgba(221, 221, 221, 0.6);
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}

.input-with-icon input:focus {
  outline: none;
  border-color: #3CB371;
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 0 3px rgba(60, 179, 113, 0.1);
}

.toggle-password {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 4px;
  z-index: 1;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.toggle-password:hover {
  background-color: rgba(245, 245, 245, 0.8);
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

.agreement {
  flex-direction: row;
  align-items: flex-start;
}

.forgot-password {
  color: #3CB371;
  text-decoration: none;
  transition: color 0.3s;
}

.forgot-password:hover {
  color: #2E8B57;
  text-decoration: underline;
}

.submit-btn {
  padding: 12px;
  background: linear-gradient(135deg, #3CB371 0%, #2E8B57 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(60, 179, 113, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(60, 179, 113, 0.4);
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.submit-btn.full-width {
  width: 100%;
}

.submit-btn.secondary {
  background: rgba(245, 245, 245, 0.8);
  color: #666;
  border: 1px solid rgba(221, 221, 221, 0.6);
  box-shadow: none;
}

.submit-btn.secondary:hover:not(:disabled) {
  background: rgba(233, 233, 233, 0.9);
}

/* Forgot Password Specific Styles */
.forgot-password-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.forgot-step {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.step-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.verification-info {
  margin-bottom: 8px;
}

.info-card {
  background: linear-gradient(135deg, rgba(240, 249, 255, 0.9) 0%, rgba(224, 242, 254, 0.9) 100%);
  border: 1px solid rgba(186, 230, 253, 0.6);
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  position: relative;
  backdrop-filter: blur(10px);
}

.info-icon {
  color: #3CB371;
  font-size: 20px;
  margin-bottom: 8px;
}

.info-text {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.countdown-text {
  margin: 0;
  font-size: 13px;
  color: #3CB371;
  font-weight: 600;
}

.button-group {
  display: flex;
  gap: 12px;
}

.button-group .submit-btn {
  flex: 1;
}

.back-to-login {
  text-align: center;
  margin-top: 8px;
}

.back-to-login a {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #666;
  text-decoration: none;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;
  background: rgba(248, 249, 250, 0.8);
  backdrop-filter: blur(10px);
}

.back-to-login a:hover {
  color: #3CB371;
  background: rgba(248, 249, 250, 0.95);
}

/* Footer Styles */
.auth-footer {
  padding: 20px;
  background: linear-gradient(135deg, rgba(247, 248, 250, 0.9) 0%, rgba(240, 242, 245, 0.9) 100%);
  border-top: 1px solid rgba(238, 238, 238, 0.5);
  text-align: center;
  backdrop-filter: blur(10px);
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

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(238, 238, 238, 0.5);
  background: linear-gradient(135deg, rgba(248, 249, 250, 0.9) 0%, rgba(240, 242, 245, 0.9) 100%);
}

.modal-header h2 {
  margin: 0;
  color: #2C3E50;
  font-size: 20px;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.close-btn:hover {
  background-color: rgba(240, 240, 240, 0.8);
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  max-height: calc(90vh - 80px);
}

.terms-content,
.privacy-content {
  line-height: 1.6;
  color: #333;
}

.terms-content h3,
.privacy-content h3 {
  color: #2C3E50;
  margin-top: 24px;
  margin-bottom: 12px;
  font-size: 16px;
}

.terms-content p,
.privacy-content p {
  margin-bottom: 12px;
}

.terms-content ul,
.privacy-content ul {
  margin: 12px 0;
  padding-left: 20px;
}

.terms-content li,
.privacy-content li {
  margin-bottom: 6px;
}

.last-updated {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid rgba(238, 238, 238, 0.5);
  font-style: italic;
  color: #666;
  text-align: center;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    max-height: 95vh;
  }

  .modal-header {
    padding: 16px 20px;
  }

  .modal-body {
    padding: 20px;
  }

  .shape {
    display: none;
  }
}

@media (max-width: 576px) {
  .auth-card {
    max-width: 100%;
    border-radius: 16px;
  }

  .form-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .button-group {
    flex-direction: column;
  }

  .orb {
    display: none;
  }
}
</style>