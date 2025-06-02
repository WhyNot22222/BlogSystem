// 日期格式化工具函数
export const formatDate = (date) => {
    if (!date) return '未设置';
    if (typeof date === 'string') {
        date = new Date(date);
    }
    return new Intl.DateTimeFormat('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    }).format(date);
};

// 相对时间格式化工具函数
export const formatTime = (date) => {
    if (!date) return '';
    const now = new Date();
    const diff = Math.floor((now - date) / 1000);

    if (diff < 60) {
        return '刚刚';
    } else if (diff < 3600) {
        return `${Math.floor(diff / 60)}分钟前`;
    } else if (diff < 86400) {
        return `${Math.floor(diff / 3600)}小时前`;
    } else {
        return formatDate(date);
    }
};