<template>
  <div class="account-settings-container">
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <h2>个人设置</h2>
          <span>管理您的账号信息和安全设置</span>
        </div>
      </template>

      <!-- 设置导航 -->
      <el-tabs v-model="activeTab" tab-position="left" class="settings-tabs">
        <el-tab-pane label="个人资料" name="profile" style="margin-left: 20px;">
          <h3>个人资料</h3>
          <p class="tab-description">更新您的个人信息和头像</p>

          <el-form
              :model="profileForm"
              :rules="profileRules"
              ref="profileFormRef"
              label-width="100px"
              class="settings-form"
          >
            <!-- 头像上传 -->
            <div class="avatar-container">
              <el-avatar :size="100">
                <img :src="avatarUrl" class="avatar-preview" alt="头像预览">
              </el-avatar>
              <div class="avatar-upload" style="margin-left: 30px;">
                <el-upload
                    class="avatar-uploader"
                    action="#"
                    :auto-upload="false"
                    :on-change="handleAvatarChange"
                >
                  <el-button type="primary">更换头像</el-button>
                </el-upload>
                <p class="upload-tip">支持 JPG、PNG 格式，文件大小不超过 2MB</p>
              </div>
            </div>

            <!-- 用户名 -->
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" placeholder="请输入用户名" />
            </el-form-item>

            <!-- 个人简介 -->
            <el-form-item label="个人简介" prop="bio">
              <el-input
                  v-model="profileForm.bio"
                  type="textarea"
                  :rows="3"
                  placeholder="介绍一下自己吧（最多200字）"
                  maxlength="200"
                  show-word-limit
              />
            </el-form-item>

            <!-- 保存按钮 -->
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="profileSaving">保存更改</el-button>
              <el-button @click="resetProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="账号安全" name="security" style="margin-left: 20px;">
          <h3>账号安全</h3>
          <p class="tab-description">更新您的密码和安全设置</p>

          <el-form
              :model="securityForm"
              :rules="securityRules"
              ref="securityFormRef"
              label-width="120px"
              class="settings-form"
          >
            <!-- 当前密码 -->
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input
                  v-model="securityForm.currentPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
              />
            </el-form-item>

            <!-- 新密码 -->
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                  v-model="securityForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
              />
              <div class="password-strength">
                <div class="strength-label">密码强度：</div>
                <div class="strength-meter">
                  <div
                      class="strength-value"
                      :style="{ width: passwordStrength.percent + '%' }"
                      :class="passwordStrength.level"
                  ></div>
                </div>
                <div class="strength-text" :class="passwordStrength.level">{{ passwordStrength.text }}</div>
              </div>
              <div class="password-tips">
                密码长度至少8位，包含字母、数字和特殊字符
              </div>
            </el-form-item>

            <!-- 确认新密码 -->
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                  v-model="securityForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
              />
            </el-form-item>

            <!-- 保存按钮 -->
            <el-form-item>
              <el-button type="primary" @click="savePassword" :loading="securitySaving">更新密码</el-button>
              <el-button @click="resetSecurity">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="联系方式" name="contact" style="margin-left: 20px;">
          <h3>联系方式</h3>
          <p class="tab-description">管理您的邮箱和其他联系方式</p>

          <el-form
              :model="contactForm"
              :rules="contactRules"
              ref="contactFormRef"
              label-width="100px"
              class="settings-form"
          >
            <!-- 邮箱 -->
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="contactForm.email" placeholder="请输入邮箱">
                <template #append>
                  <el-button @click="sendVerificationEmail" :disabled="emailVerified || verificationSending">
                    {{ emailVerified ? '已验证' : verificationSending ? `重新发送(${countDown}s)` : '发送验证码' }}
                  </el-button>
                </template>
              </el-input>
            </el-form-item>

            <!-- 验证码 -->
            <el-form-item v-if="showVerificationCode" label="验证码" prop="verificationCode">
              <el-input
                  v-model="contactForm.verificationCode"
                  placeholder="请输入验证码"
                  maxlength="6"
              />
            </el-form-item>

            <!-- 手机号码 -->
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="contactForm.phone" placeholder="请输入手机号码" />
            </el-form-item>

            <!-- 保存按钮 -->
            <el-form-item>
              <el-button type="primary" @click="saveContact" :loading="contactSaving">保存更改</el-button>
              <el-button @click="resetContact">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';
import { useStore } from 'vuex';
import defaultAvatar from '@/imgs/default.jpg';

const store = useStore();
const userId = computed(() => store.getters.userId)

const activeTab = ref('profile');
const avatarUrl = ref(defaultAvatar); // For display
const newAvatarFile = ref(null); // To store the selected File object for new avatar

const profileFormRef = ref(null);
const securityFormRef = ref(null);
const contactFormRef = ref(null);

const profileSaving = ref(false);
const securitySaving = ref(false);
const contactSaving = ref(false);
const verificationSending = ref(false);

const emailVerified = ref(false);
const showVerificationCode = ref(false);
const countDown = ref(0);

const profileForm = reactive({
  username: '',
  bio: '',
  avatar: '', // To store current avatar path from server
});

const profileRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { max: 30, message: '长度不能超过 30 个字符', trigger: 'blur' }
  ]
};

const securityForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const securityRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度至少为 8 个字符', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,}$/,
      message: '密码必须包含字母、数字和特殊字符',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== securityForm.newPassword) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

const contactForm = reactive({
  email: '',
  verificationCode: '',
  phone: ''
});

const contactRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
};

const passwordStrength = computed(() => {
  const password = securityForm.newPassword;

  if (!password) {
    return { level: 'none', text: '无', percent: 0 };
  }

  let strength = 0;

  // 长度检查
  if (password.length >= 8) strength += 1;
  if (password.length >= 12) strength += 1;

  // 包含数字
  if (/\d/.test(password)) strength += 1;

  // 包含小写字母
  if (/[a-z]/.test(password)) strength += 1;

  // 包含大写字母
  if (/[A-Z]/.test(password)) strength += 1;

  // 包含特殊字符
  if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) strength += 1;

  // 计算强度级别
  let level, text, percent;

  if (strength <= 2) {
    level = 'weak';
    text = '弱';
    percent = 33;
  } else if (strength <= 4) {
    level = 'medium';
    text = '中';
    percent = 66;
  } else {
    level = 'strong';
    text = '强';
    percent = 100;
  }

  return { level, text, percent };
});

onMounted(async () => {
  const userRes = await request.post('user/getUser', null, {
    params: { userId: userId.value }
  })
  const user = userRes.data
  if (user && user.id) {
    profileForm.username = user.username || '';
    profileForm.bio = user.bio || '';
    profileForm.avatar = user.avatar || defaultAvatar;
    avatarUrl.value = user.avatar || defaultAvatar;

    contactForm.email = user.email || '';
    contactForm.phone = user.phone || '';
  } else {
    console.warn("User data not fully loaded in onMounted for AccountSettings");
    // Fallback to prevent errors
    avatarUrl.value = defaultAvatar;
    profileForm.avatar = defaultAvatar;
  }
  fetchAvatar()
});

const handleAvatarChange = (file) => {
  const isImage = ['image/jpeg', 'image/png'].includes(file.raw.type);
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片');
    return;
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB');
    return;
  }

  newAvatarFile.value = file.raw;
  avatarUrl.value = URL.createObjectURL(file.raw); // Local preview
  ElMessage.info('头像已选择，点击"保存更改"以上传并更新。');
};

const saveProfile = async () => {
  try {
    await profileFormRef.value.validate();
    profileSaving.value = true;

    let finalAvatarPath = profileForm.avatar; // Start with the current avatar path

    if (newAvatarFile.value) {
      // If a new avatar was selected, upload it first
      const formData = new FormData();
      formData.append('file', newAvatarFile.value);

      const uploadResponse = await request.post('/file/uploadAvatar', formData, {
        params: { userId: store.getters.userId },
        headers: { 'Content-Type': 'multipart/form-data' },
      });

      if (uploadResponse.code === '200') { // Check your backend's success indicator
        finalAvatarPath = uploadResponse.data; // Get the new path from backend
        avatarUrl.value = finalAvatarPath;     // Update display URL to the new server path
        profileForm.avatar = finalAvatarPath;  // Update form model's persistent path
        newAvatarFile.value = null;            // Clear the pending file
      } else {
        ElMessage.error('头像上传失败: ' + (uploadResponse.msg || '未知错误'));
        profileSaving.value = false;
        return;
      }
    }

    // Now update the profile with username, bio, and the finalAvatarPath
    const profileUpdatePayload = {
      id: store.getters.userId,
      username: profileForm.username,
      bio: profileForm.bio,
      avatar: finalAvatarPath,
    };

    const updateResponse = await request.put('/user/updateProfile', profileUpdatePayload);
    if (updateResponse.code === '200') {
      ElMessage.success(updateResponse.msg || '资料更新成功');
    } else {
      ElMessage.error(updateResponse.msg || '资料更新失败');
    }
    // Refresh user state in Vuex to reflect changes globally
    await store.dispatch('initUserState'); 
    // Or, if initUserState doesn't fetch full user data, commit directly:
    // store.commit('SET_USER', { ...store.state.user, ...profileUpdatePayload, id: store.getters.userId });


  } catch (error) {
    if (error && error.name !== 'ValidateError') {
      ElMessage.error('更新失败: ' + (error.response?.data?.msg || error.message || '未知错误'));
    }
    console.error('更新个人资料失败:', error);
  } finally {
    profileSaving.value = false;
  }
};

const resetProfile = () => {
  const user = store.state.user;
  if (user && user.id) {
    profileForm.username = user.username || '';
    profileForm.bio = user.bio || '';
    profileForm.avatar = user.avatar || defaultAvatar;
    avatarUrl.value = user.avatar || defaultAvatar;
  } else {
    profileForm.username = '';
    profileForm.bio = '';
    profileForm.avatar = defaultAvatar;
    avatarUrl.value = defaultAvatar;
  }
  newAvatarFile.value = null; // Clear any selected new avatar
  if (profileFormRef.value) {
    profileFormRef.value.clearValidate(); // Clear validation states
  }
};

const savePassword = async () => {
  try {
    await securityFormRef.value.validate();
    securitySaving.value = true;

    const response = await request.post('/user/updatePassword', null, {
      params: {
        userId: store.getters.userId,
        currentPassword: securityForm.currentPassword,
        newPassword: securityForm.newPassword
      }
    });

    ElMessage.success(response.message || '密码修改成功');
    // 清空表单
    securityForm.currentPassword = '';
    securityForm.newPassword = '';
    securityForm.confirmPassword = '';
  } catch (error) {
    console.error('密码修改失败:', error);
  } finally {
    securitySaving.value = false;
  }
};

const resetSecurity = () => {
  securityFormRef.value.resetFields();
};

const sendVerificationEmail = () => {
  contactFormRef.value.validateField('email', (valid) => {
    if (valid) {
      verificationSending.value = true;
      showVerificationCode.value = true;
      countDown.value = 60;

      // 倒计时
      const timer = setInterval(() => {
        countDown.value--;
        if (countDown.value <= 0) {
          clearInterval(timer);
          verificationSending.value = false;
        }
      }, 1000);

      // 模拟API请求
      setTimeout(() => {
        ElMessage.success('验证码已发送到您的邮箱');
      }, 1000);
    }
  });
};

const saveContact = async () => {
  try {
    await contactFormRef.value.validate();
    contactSaving.value = true;

    const response = await request.post('/user/updateEmail', null, {
      params: {
        userId: store.getters.userId,
        newEmail: contactForm.email
      }
    });

    ElMessage.success(response.message || '邮箱更新成功');
    emailVerified.value = true;
    showVerificationCode.value = false;
  } catch (error) {
    console.error('邮箱更新失败:', error);
  } finally {
    contactSaving.value = false;
  }
};

const resetContact = () => {
  contactFormRef.value.resetFields();
  showVerificationCode.value = false;
};

// 获取头像方法
const fetchAvatar = async () => {
  try {
    request.get('/file/getAvatar', {
      params: { userId: userId.value },
      responseType: 'json'
    }).then(res => {
      if (res.code === '200') {
        // 将 Base64 字符串转换为二进制数据
        const binaryString = window.atob(res.data);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
          bytes[i] = binaryString.charCodeAt(i);
        }
        // 创建 Blob 对象
        const blob = new Blob([bytes], { type: 'image/jpeg' });
        // 创建临时 URL
        avatarUrl.value = URL.createObjectURL(blob);
      } else {
        ElMessage.error('头像加载失败：' + res.msg);
        avatarUrl.value = defaultAvatar;  // 使用导入的默认头像
      }
    })
  } catch (error) {
    console.error('头像加载失败:', error);
    avatarUrl.value = defaultAvatar;  // 使用导入的默认头像
  }
}

// 组件卸载时清理 URL
onUnmounted(() => {
  if (avatarUrl.value && avatarUrl.value.startsWith('blob:')) {
    URL.revokeObjectURL(avatarUrl.value);
  }
})
</script>


<style scoped>
.account-settings-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.settings-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  flex-direction: column;
}

.card-header h2 {
  margin: 0 0 8px 0;
  font-size: 20px;
}

.card-header span {
  color: #666;
  font-size: 14px;
}

.settings-tabs {
  min-height: 500px;
}

.settings-form {
  max-width: 600px;
  margin-top: 20px;
}

.tab-description {
  color: #666;
  margin-bottom: 20px;
}

.avatar-container {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.upload-tip {
  font-size: 12px;
  color: #666;
  margin-top: 8px;
}

.password-strength {
  margin-top: 8px;
  display: flex;
  align-items: center;
}

.strength-label {
  font-size: 12px;
  color: #666;
  margin-right: 8px;
}

.strength-meter {
  width: 100px;
  height: 6px;
  background-color: #eee;
  border-radius: 3px;
  overflow: hidden;
  margin-right: 8px;
}

.strength-value {
  height: 100%;
  transition: width 0.3s;
}

.strength-value.weak {
  background-color: #f56c6c;
}

.strength-value.medium {
  background-color: #e6a23c;
}

.strength-value.strong {
  background-color: #67c23a;
}

.strength-text {
  font-size: 12px;
}

.strength-text.weak {
  color: #f56c6c;
}

.strength-text.medium {
  color: #e6a23c;
}

.strength-text.strong {
  color: #67c23a;
}

.password-tips {
  font-size: 12px;
  color: #666;
  margin-top: 8px;
}


.notification-section h4 {
  margin-top: 0;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-tabs :deep(.el-tabs__header) {
    width: 100%;
    margin-bottom: 20px;
  }

  .settings-tabs :deep(.el-tabs__nav) {
    width: 100%;
  }

  .settings-tabs :deep(.el-tabs__item) {
    text-align: center;
  }

  .avatar-container {
    flex-direction: column;
    align-items: flex-start;
  }

  .avatar-preview {
    margin-right: 0;
    margin-bottom: 15px;
  }
}
</style>