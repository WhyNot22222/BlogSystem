<template>
  <div>
    <h1>Test Page</h1>
    <p>This is a test page.</p>
  </div>
</template>

<script>
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

// request.get('/file/getCover', {
//   params: {
//     userId: 1,
//     postId: 8
//   },
//   responseType: 'json'
// }).then(res => {
//   if (res.code === '200') {
//     console.log("请求封面成功")
//   } else {
//     ElMessage.error('封面加载失败：' + res.msg);
//   }
// })

// request.post('/tags', {
//   params: {
//     name: 'python'
//   }
// }).then(res => {
//   if (res.code === '200') {
//     console.log('添加标签成功！', res.data)
//   } else {
//     ElMessage.error('添加标签失败：' + res.msg)
//   }
// })

// request.post('/user/getUser', null,{
//   params: {
//     userId: 1
//   }
// }).then(res => {
//   if (res.code === '200') {
//     console.log('获取用户信息成功！', res.data)
//   } else {
//     ElMessage.error('获取用户信息失败：' + res.msg)
//   }
// })
</script>
