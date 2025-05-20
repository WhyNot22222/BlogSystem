import { createStore } from 'vuex'

export default createStore({
  state: {
    user: {
      id: null,
      username: '',
      email: '',
      avatar: ''
    }
  },
  mutations: {
    SET_USER(state, payload) {
      state.user = { ...state.user, ...payload }
    },
    CLEAR_USER(state) {
      state.user = {
        id: null,
        username: '',
        email: '',
        avatar: ''
      }
    }
  },
  actions: {
    // 初始化用户状态
    initUserState({ commit }) {
      const userId = localStorage.getItem('userId')
      const token = localStorage.getItem('token')
      
      if (userId && token) {
        // 确保使用正确的属性名设置用户ID
        commit('SET_USER', { 
          id: userId
        })
        
        // 可以选择性地从后端获取完整的用户信息
        // 这里可以添加一个API调用来获取用户详细信息
      }
    },
    // 登出操作
    logout({ commit }) {
      localStorage.removeItem('userId')
      localStorage.removeItem('token')
      commit('CLEAR_USER')
    }
  },
  getters: {
    // 确保返回正确的用户ID
    userId: state => state.user.id || null,
    isAuthenticated: state => !!state.user.id
  }
})