<template>
  <RouterView />
</template>

<script setup>
import { onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()

onMounted(() => {
  // 初始化用户状态
  store.dispatch('initUserState')
  
  // 调试信息
  const userId = localStorage.getItem('userId')
  const token = localStorage.getItem('token')
  console.log('App mounted - localStorage userId:', userId)
  console.log('App mounted - localStorage token:', token)
  console.log('App mounted - Vuex userId:', store.getters.userId)
})
</script>