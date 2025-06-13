import request from '@/utils/request'
import { ElMessage } from "element-plus";
import {fetchUserAvatar} from "@/utils/userHelper.js";

// 获取封面
export const fetchCover = async (userId, postId) => {
    try {
      const response = await request.get('/file/getCover', {
        params: { 
          userId: userId,
          postId: postId
        }
      });
      
      if (response.code === '200') {
        const binaryString = window.atob(response.data);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
          bytes[i] = binaryString.charCodeAt(i);
        }
        const blob = new Blob([bytes], { type: 'image/jpeg' });
        return URL.createObjectURL(blob);
      } else {
        console.log('封面加载失败:', response.msg);
        ElMessage.info('封面加载失败：' + response.msg + '，请检查是否有封面');
        return ''; // 清空封面显示
      }
    } catch (error) {
      console.log('封面加载失败:', error);
      return ''; // 清空封面显示
    }
}

// 获取标签
export const fetchTags = async (postId) => {
  try {
    const response = await request.get('/post-tags', {
      params: {
        postId: postId
      }
    });

    if (response.code === '200') {
      return response.data;
    } else {
      console.log('标签加载失败:', response.msg);
      ElMessage.error('标签加载失败：' + response.msg);
      return []; // 清空标签显示
    }
  } catch (error) {
    console.log('标签加载失败:', error);
    return []; // 清空标签显示
  }
}

// 获取标签名称
export const fetchTagName = async (tagId) => {
  try {
    const response = await request.get(`/tags/searchById`, {
      params: {
        tagId: tagId
      }
    });
    if (response.code === '200') {
      return response.data.name;
    } else {
      console.log('标签名称获取失败:', response.msg);
      ElMessage.error(`标签#${tagId}名称获取失败`);
      return '';
    }
  } catch (error) {
    console.log('标签名称获取失败:', error);
    return '';
  }
}

// 获取所有分类
export const fetchCategories = async () => {
  try {
    const response = await request.get('/categories')
    if (response.code === '200') {
      return response.data.map(item => ({
        value: item.id,
        label: item.name
      }))
    }
  } catch (error) {
    console.log('获取分类失败:', error)
    ElMessage.info('获取分类失败')
  }
}

export const fetchProcessedPosts = async (currentUser) => {
  try {
    const res = await request.get('/posts/selectAll');
    if (res.code === '200' && Array.isArray(res.data)) {
      return await Promise.all(
        res.data.map(async (postData) => {
          // 获取作者信息
          const authorRes = await request.get('/user/getUser', {
            params: { userId: postData.userId }
          });
          const authorInfo = authorRes.code === '200' ? authorRes.data : {};
          
          // 获取评论数据
          const commentsRes = await request.get('/comments', {
            params: { postId: postData.id }
          });
          const processedComments = commentsRes.code === '200' ? 
            await Promise.all(commentsRes.data.map(async comment => ({
              ...comment,
              author: {
                id: comment.userId,
                name: (await request.get(`/user/getUser`, {
                  params: { userId: comment.userId }
                }).data?.username) || '未知用户',
                avatar: await fetchUserAvatar(comment.userId)
              }
            }))) : [];

          return {
            id: postData.id,
            title: postData.title,
            summary: postData.summary,
            content: postData.content,
            formattedContent: postData.formattedContent || formatContent(postData.content),
            views: postData.views || 0,
            author: {
              id: postData.userId,
              name: authorInfo.username || '未知作者',
              avatar: await fetchUserAvatar(postData.userId),
              isFollowed: currentUser?.following?.includes(postData.userId) || false,
              followLoading: false
            },
            coverUrl: await fetchCover(postData.userId, postData.id),
            createdAt: postData.createdAt,
            tags: Array.isArray(postData.tags) ? 
              await Promise.all(postData.tags.map(fetchTagName)) : [],
            likes: postData.likes || 0,
            isLiked: postData.likesUsers?.includes(currentUser?.id) || false,
            favorites: postData.favorites || 0,
            isFavorited: postData.favoritedUsers?.includes(currentUser?.id) || false,
            comments: processedComments
          };
        })
      );
    }
    return [];
  } catch (error) {
    console.error('获取处理帖子失败:', error);
    throw error;
  }
};