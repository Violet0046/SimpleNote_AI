-- 初始化数据库和表结构的SQL脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS simplenote DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE simplenote;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(64) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(128) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(64) DEFAULT NULL COMMENT '用户昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `gender` TINYINT(1) DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
  `intro` VARCHAR(255) DEFAULT NULL COMMENT '个人简介',
  `ip_location` VARCHAR(64) DEFAULT NULL COMMENT 'IP属地',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 帖子/笔记表 (严格对齐 Post.java 实体类)
CREATE TABLE IF NOT EXISTS `post` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` INT(11) NOT NULL COMMENT '发帖人用户ID',
  `title` VARCHAR(128) NOT NULL COMMENT '帖子标题',
  `content` TEXT DEFAULT NULL COMMENT '帖子正文(可为空)',
  `images` TEXT DEFAULT NULL COMMENT '多图URL列表(逗号分隔)',
  `is_video` INT(11) DEFAULT 0 COMMENT '是否为视频: 0-图文, 1-视频',
  `likes_count` INT(11) DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子/笔记表';

-- 3. 评论表 (支持多级回复)
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评论主键ID',
  `post_id` BIGINT(20) NOT NULL COMMENT '所属帖子ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '评论者ID',
  `content` VARCHAR(1024) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父级评论ID(0代表顶级评论)',
  `reply_to_user_id` BIGINT(20) DEFAULT NULL COMMENT '被回复者ID',
  `likes_count` INT(11) DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除: 0-正常, 1-已删除',
  `ip_location` VARCHAR(64) DEFAULT NULL COMMENT '评论时的IP属地',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 4. 关注关系表
CREATE TABLE IF NOT EXISTS `follow_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `follower_id` BIGINT(20) NOT NULL COMMENT '粉丝ID（发起关注的用户）',
  `followed_id` BIGINT(20) NOT NULL COMMENT '博主ID（被关注的用户）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followed` (`follower_id`, `followed_id`),
  KEY `idx_followed_id` (`followed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注关系表';

-- 5. 帖子点赞表
CREATE TABLE IF NOT EXISTS `user_likes` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '点赞的用户ID',
  `post_id` BIGINT(20) NOT NULL COMMENT '被点赞的帖子ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞记录表';

-- 6. 评论点赞表
CREATE TABLE IF NOT EXISTS `comment_like` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `comment_id` BIGINT(20) NOT NULL COMMENT '被点赞评论ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_comment` (`user_id`, `comment_id`),
  KEY `idx_comment_id` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞记录表';