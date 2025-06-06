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
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role ENUM('admin','user') NOT NULL DEFAULT 'user' COMMENT '角色',
    avatar VARCHAR(255) COMMENT '用户头像URL',
    bio TEXT COMMENT '个人简介',
    phone VARCHAR(20) COMMENT '手机号码'
);

INSERT INTO user (username, password, email) VALUES
    ('admin', 'admin', '2993946158@qq.com'),
    ('promise', '123456', 'promise@nuaa.edu.cn'),
    ('yn', '222', 'yn@nju.edu.cn');

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
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
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    title VARCHAR(255) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    formatted_content TEXT NOT NULL COMMENT '格式化内容',
    views INT DEFAULT 0 COMMENT '阅读量',
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

INSERT INTO posts (title, content, formatted_content, views, summary, status, user_id, category_id, created_at, published_at) VALUES
    (
    'PyTorch入门与核心概念详解：从基础到实战问题解决',
    '用户/forch 编写 Transformer 模型时遇到了多个错误，包括维度不匹配、NaN 损失、注意力权重未记录等问题...\n- 阅读 760\n- 38 赞\n- 收藏 33',
    '<h2>什么是设计模式</h2>
    <p>设计模式是在软件设计中常见问题的典型解决方案。它们就像能根据需求进行调整的预制蓝图，可用于解决代码中反复出现的设计问题。</p>

    <h2>设计模式的分类</h2>
    <p>设计模式通常分为三大类：</p>
    <ul>
      <li><strong>创建型模式</strong>：提供创建对象的机制，增加已有代码的灵活性和可复用性</li>
      <li><strong>结构型模式</strong>：介绍如何将对象和类组装成较大的结构，并同时保持结构的灵活和高效</li>
      <li><strong>行为型模式</strong>：负责对象间的高效沟通和职责委派</li>
    </ul>

    <h2>常用的创建型模式</h2>
    <h3>1. 单例模式 (Singleton)</h3>
    <p>单例模式确保一个类只有一个实例，并提供一个全局访问点。</p>

    <h3>2. 工厂方法模式 (Factory Method)</h3>
    <p>工厂方法模式提供了一种创建对象的接口，但由子类决定要实例化哪个类。</p>

    <p>本文将继续深入探讨其他设计模式的实现和应用场景...</p>',
     0,
    '用户/forch 编写 Transformer 模型时遇到了多个错误，包括维度不匹配、NaN 损失、注意力权重未记录等问题...',
    'published',
    1,
    1,
    NOW(),
    NOW()
    ),
    (
    '408考研经典详解：2009年第10题',
    '所以，如果用二路归并排序算法，第二趟排序之后所得到的序列中，前4个关键字应该是有序的，但是...\n- 阅读 448\n- 9 赞\n- 收藏 5',
    '所以，如果用二路归并排序算法，第二趟排序之后所得到的序列中，前4个关键字应该是有序的，但是...\n- 阅读 448\n- 9 赞\n- 收藏 5',
    0,
    '二路归并排序算法第二趟排序后的序列分析',
    'published',
    1,
    1,
    NOW(),
    NOW()
    ),
    (
    '码住了！一文教你玩好豆包AI编程，编程效率原地起飞',
    '看不懂代码？跟它！写不动代码？问豆包！Al助力，编程从此so easy！',
    '看不懂代码？跟它！写不动代码？问豆包！Al助力，编程从此so easy！',
    0,
    '豆包AI编程工具使用指南',
    'published',
    1,
    1,
    NOW(),
    NOW()
    ),
    (
    'Python基础总结(十)之函数',
    '函数的定义要使用def关键字，def后面紧跟函数名，给出的为函数的代码块，上述即为一个简单的函数，定义了一个名为test的函数...\n- 阅读 443\n- 4 赞\n- 收藏 11',
    '函数的定义要使用def关键字，def后面紧跟函数名，给出的为函数的代码块，上述即为一个简单的函数，定义了一个名为test的函数...\n- 阅读 443\n- 4 赞\n- 收藏 11',
    0,
    'Python函数定义与基础用法详解',
    'published',
    1,
    1,
    NOW(),
    NOW()
    );

DELIMITER //

CREATE TRIGGER delete_post_related_data
    AFTER DELETE ON posts
    FOR EACH ROW
BEGIN
    DELETE FROM post_tags WHERE post_id = OLD.id;
    DELETE FROM post_likes WHERE post_id = OLD.id;
    DELETE FROM comments WHERE post_id = OLD.id;
    DELETE FROM comment_likes WHERE comment_id IN (SELECT id FROM comments WHERE post_id = OLD.id);
END //

DELIMITER ;

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称'
);

-- 文章-标签连接表
CREATE TABLE IF NOT EXISTS post_tags (
    post_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

DELIMITER //

CREATE TRIGGER delete_tag_related_post_tags
    AFTER DELETE ON tags
    FOR EACH ROW
BEGIN
    DELETE FROM post_tags WHERE tag_id = OLD.id;
END //

DELIMITER ;

-- 评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '文章ID',
    parent_id BIGINT COMMENT '父评论ID',
    user_id BIGINT COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '评论内容',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '评论状态',
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

INSERT INTO comments (post_id, user_id, content)
VALUES (1, 1, '这篇文章很有帮助！');

INSERT INTO comments (post_id, parent_id, user_id, content)
VALUES (1, LAST_INSERT_ID(), 1, '我完全同意作者的观点');

-- 文章点赞表
CREATE TABLE IF NOT EXISTS post_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    post_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 评论点赞表
CREATE TABLE IF NOT EXISTS comment_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY (comment_id, user_id),
    FOREIGN KEY (comment_id) REFERENCES comments(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 收藏夹表
CREATE TABLE IF NOT EXISTS favorites_collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏夹ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    name VARCHAR(100) NOT NULL COMMENT '收藏夹名称',
    description TEXT COMMENT '收藏夹描述',
    is_public BOOLEAN DEFAULT TRUE COMMENT '是否公开',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 收藏表
CREATE TABLE IF NOT EXISTS favorites (
    collection_id BIGINT NOT NULL COMMENT '收藏夹ID',
    post_id BIGINT NOT NULL COMMENT '文章ID',
    user_id BIGINT NOT NULL COMMENT '操作用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (collection_id, post_id),
    FOREIGN KEY (collection_id) REFERENCES favorites_collections(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 关注表
CREATE TABLE follow (
    id INT PRIMARY KEY AUTO_INCREMENT,
    follower_id INT NOT NULL COMMENT '关注者ID',
    followed_id INT NOT NULL COMMENT '被关注者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follow (follower_id, followed_id),
    FOREIGN KEY (follower_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_id) REFERENCES user(id) ON DELETE CASCADE
);