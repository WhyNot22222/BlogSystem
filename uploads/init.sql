SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS comment_likes;
DROP TABLE IF EXISTS post_likes;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS post_tags;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS user;

SET FOREIGN_KEY_CHECKS = 1;

-- 用户表
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    avatar VARCHAR(255) COMMENT '用户头像URL',
    bio TEXT COMMENT '个人简介',
    phone VARCHAR(20) COMMENT '手机号码'
);

INSERT INTO user (username, password, email) VALUES
    ('admin', 'admin', '2993946158@qq.com'),
    ('promise', '123456', 'promise@nuaa.edu.cn'),
    ('yn', '222', 'yn@nju.edu.cn');

-- 分类表
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述'
);

INSERT INTO categories (name, description) VALUES
    ('资讯', ''),
    ('技术', ''),
    ('运维', ''),
    ('人工智能', ''),
    ('Java', ''),
    ('Python', ''),
    ('前端', ''),
    ('后端', '');

-- 文章表
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    summary TEXT COMMENT '摘要',
    cover_url VARCHAR(255) COMMENT '封面图片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    published_at TIMESTAMP COMMENT '发布时间',
    status ENUM('draft', 'published') DEFAULT 'draft' COMMENT '状态',
    user_id BIGINT NOT NULL COMMENT '作者ID',
    category_id BIGINT COMMENT '分类ID',
    allow_comments BOOLEAN DEFAULT TRUE COMMENT '是否允许评论',
    is_public BOOLEAN DEFAULT TRUE COMMENT '是否公开/仅粉丝可见',
    is_pinned BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 标签表
CREATE TABLE tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    slug VARCHAR(50) UNIQUE COMMENT '标签URL别名'
);

-- 文章-标签连接表
CREATE TABLE post_tags (
    post_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

-- 评论表
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '文章ID',
    parent_id BIGINT COMMENT '父评论ID',
    user_id BIGINT COMMENT '用户ID',
    name VARCHAR(50) COMMENT '匿名评论者姓名',
    email VARCHAR(100) COMMENT '匿名评论者邮箱',
    content TEXT NOT NULL COMMENT '评论内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status ENUM('pending', 'approved', 'spam') DEFAULT 'pending' COMMENT '评论状态',
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 文章点赞表
CREATE TABLE post_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    post_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 评论点赞表
CREATE TABLE comment_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY (comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES comments(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 角色表
CREATE TABLE roles (
   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
   name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称'
);

-- 用户-角色连接表
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);