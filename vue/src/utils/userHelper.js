import request from '@/utils/request'

import defaultAvatar from '@/imgs/default.jpg'
import { reactive } from 'vue'
import {ElMessage} from "element-plus";

export const fetchUserAvatar = async (userId) => {
  try {
    const res = await request.get('/file/getAvatar', {
      params: { userId },
      responseType: 'json'
    })
    if (res.code === '200') {
      const binaryString = window.atob(res.data)
      const bytes = new Uint8Array(binaryString.length)
      for (let i = 0; i < binaryString.length; i++) {
        bytes[i] = binaryString.charCodeAt(i)
      }
      const blob = new Blob([bytes], { type: 'image/jpeg' })
      return URL.createObjectURL(blob)
    }
    return defaultAvatar
  } catch (error) {
    console.error('获取头像失败:', error)
    return defaultAvatar
  }
}

// 获取用户名
export const getUserName = async (userId) => {
  try {
    const res = await request.post('/user/getUser', null, { 
      params: { userId } 
    });
    return res.data?.username || '未知用户';
  } catch (e) {
    return '未知用户';
  }
}

export const fetchCurrentUserDetails = async (currentUser) => {
  if (!currentUser.id) {
    currentUser.name = '访客';
    return currentUser;
  }
  try {
    // 获取用户名
    const userRes = await request.post(`/user/getUser`, null, {
      params: { userId: currentUser.id }
    });
    if (userRes.code === '200' && userRes.data) {
      currentUser.name = userRes.data.username || '未知用户';
    } else {
      ElMessage.error('获取当前用户信息失败');
    }

    // 获取头像
    const avatarUrl = await fetchUserAvatar(currentUser.id);
    currentUser.avatar = avatarUrl || '';
  } catch (error) {
    console.error(`获取当前用户(id: ${currentUser.id})信息失败:`, error);
    currentUser.name = '信息加载失败';
    currentUser.avatar = '/placeholder.svg?height=40&width=40'; // 出错时显示默认头像
  } finally {
    return currentUser;
  }
};