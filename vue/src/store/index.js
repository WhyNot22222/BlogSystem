import { createStore } from 'vuex'

export default createStore({
  state: {
    user: {
      id: null,
      username: '',
      email: '',
      avatar: '',
    },
    searchQuery: '',
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
    },
    SET_SEARCH_QUERY(state, query) {
      state.searchQuery = query
    },
  },
  actions: {
    // 初始化用户状态
    initUserState({ commit }) {
      const userId = localStorage.getItem('userId')
      const token = localStorage.getItem('token')
      
      if (userId && token) {
        commit('SET_USER', { 
          id: userId,
          // 添加从本地存储获取的草稿ID
          draftId: localStorage.getItem('draftId') || null
        })
      }
    },
    // 登出操作
    logout({ commit }) {
      localStorage.removeItem('userId')
      localStorage.removeItem('token')
      // 登出时也清除草稿ID
      localStorage.removeItem('draftId')
      commit('CLEAR_USER')
    }
  },
  getters: {
    userId: state => state.user.id || null,
    isAuthenticated: state => !!state.user.id,
    // 新增获取草稿ID的getter
    draftId: state => state.user.draftId || null
  }
})