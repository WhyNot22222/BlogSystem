import request from '@/utils/request'
import { fetchUserAvatar } from '@/utils/userHelper'

export const getInteractionData = async (postId, userId) => {
  try {
    const [likes, isLiked, favorites, isFavorited, comments] = await Promise.all([
      // 获取点赞数
      request.get(`/likes/count/${postId}`).then(res => res.data),
      // 检查点赞状态
      request.get(`/likes/check`, { params: { postId, userId } }).then(res => res.data),
      // 获取收藏数
      request.get(`/favorites/count`, { params: { postId } }).then(res => res.data),
      // 检查收藏状态
      request.get('/favorites/check', { params: { postId, userId } }).then(res => res.data),
      // 获取评论数据
      request.get('/comment/post', { params: { postId } }).then(res => res.data)
    ])

    return {
      likes: likes || 0,
      isLiked: !!isLiked,
      favorites: favorites || 0,
      isFavorited: !!isFavorited,
      comments: comments || []
    }
  } catch (error) {
    console.error('获取互动数据失败:', error)
    return {
      likes: 0,
      isLiked: false,
      favorites: 0,
      isFavorited: false,
      comments: []
    }
  }
}

export const fetchCommentsWithAuthors = async (comments) => {
    return Promise.all(
      comments.map(async comment => {
        try {
          const userRes = await request.post('/user/getUser', null, { 
            params: { userId: comment.userId }
          });
          return {
            ...comment,
            author: {  // 保持与Community.vue一致的结构
              name: userRes.data?.username || '未知用户',
              avatar: await fetchUserAvatar(comment.userId),
              id: comment.userId  // 新增用户ID字段
            },
            likes: comment.likes || 0,  // 确保点赞数存在
            createdAt: comment.createdAt // 统一时间字段
          };
        } catch (e) {
          console.error(`获取用户${comment.userId}信息失败:`, e);
          return {
            ...comment,
            author: {
              name: '未知用户',
              avatar: '/placeholder.svg?height=36&width=36',
              id: null
            },
            likes: comment.likes || 0,
            createdAt: comment.createTime
          };
        }
      })
    );
  };

// 通用检查方法
const checkStatus = async (url, params) => {
  try {
    const res = await request.get(url, { params })
    return res.code === '200' ? res.data : false
  } catch (error) {
    console.error(`检查状态失败: ${url}`, error)
    return false
  }
}

// 通用计数方法
const getCount = async (url, params) => {
  try {
    const res = await request.get(url, { params })
    return res.code === '200' ? res.data : 0
  } catch (error) {
    console.error(`获取计数失败: ${url}`, error)
    return 0
  }
}

// 处理评论作者信息
export const processComments = async (comments) => {
  return Promise.all(
    comments.map(async comment => {
      try {
        const [userRes, avatar] = await Promise.all([
          request.post('/user/getUser', null, { params: { userId: comment.userId } }),
          fetchUserAvatar(comment.userId)
        ])
        
        return {
          ...comment,
          author: {
            name: userRes.data?.username || '未知用户',
            avatar: avatar || '/placeholder.svg?height=32&width=32'
          }
        }
      } catch (error) {
        console.error('处理评论失败:', error)
        return {
          ...comment,
          author: {
            name: '未知用户',
            avatar: '/placeholder.svg?height=32&width=32'
          }
        }
      }
    })
  )
}
