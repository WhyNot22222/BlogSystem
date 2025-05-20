import axios from "axios"
import { ElMessage } from "element-plus"

const request = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 30000   // 后台接口超时时间
})

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    // 如果请求数据是 FormData 类型，则删除 Content-Type，让浏览器自动设置
    if (config.data instanceof FormData) {
        delete config.headers['Content-Type'];
    } else {
        config.headers['Content-Type'] = 'application/json;charset=UTF-8';
    }
    return config;
}, error => {
    return Promise.reject(error);
})

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        return res;
    },
    error => {
        if (error.response) { // 确保 response 存在
            if (error.response.status === 404) {
                ElMessage.error('未找到请求接口');
            } else if (error.response.status === 500) {
                ElMessage.error('系统异常，请查看后端控制台报错');
            }
        } else {
            ElMessage.error('网络请求失败，请检查网络连接'); // 处理无响应的情况
        }
        console.log(error.message);
        return Promise.reject(error);
    }
)

export default request;