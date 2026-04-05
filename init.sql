USE `simplenote_db`;
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子主键ID',
  `user_id` INT NOT NULL COMMENT '发帖人的用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '帖子标题',
  `content` TEXT COMMENT '帖子正文（可为空，小红书很多只发图没正文）',
  `images` VARCHAR(1000) COMMENT '【升级】多图URL列表(多张图片用逗号隔开,或存JSON)',
  `likes_count` INT DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 2. 新增：评论表
CREATE TABLE `comment` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '评论主键ID',
  `post_id` INT NOT NULL COMMENT '所属帖子的ID',
  `user_id` INT NOT NULL COMMENT '发评论的用户ID',
  `content` VARCHAR(500) NOT NULL COMMENT '评论正文',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 给帖子表的 user_id 加个目录
ALTER TABLE `post` ADD INDEX `idx_user_id` (`user_id`);
-- 给评论表的 post_id 加个目录
ALTER TABLE `comment` ADD INDEX `idx_post_id` (`post_id`);

CREATE TABLE `user_likes` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
  `user_id` INT NOT NULL COMMENT '点赞的用户ID',
  `post_id` INT NOT NULL COMMENT '被点赞的帖子ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  UNIQUE KEY `uk_user_post` (`user_id`, `post_id`) COMMENT '联合唯一索引：防止同一个人对同一篇帖子重复点赞'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞关联表';

UPDATE user SET avatar = 'http://localhost:8080/1.jpg' WHERE avatar IS NULL;

ALTER TABLE user 
ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

CREATE TABLE `follow_user` (
  `id` INT AUTO_INCREMENT COMMENT '自增主键' PRIMARY KEY,
  `follower_id` INT NOT NULL COMMENT '粉丝ID（发起关注的人）',
  `followed_id` INT NOT NULL COMMENT '博主ID（被关注的人）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  -- 核心：建立联合唯一键。防止张三无限制地关注李四100次，导致数据错乱！
  UNIQUE KEY `uk_follower_followed` (`follower_id`,`followed_id`),
  -- 加快根据博主ID查粉丝列表的速度
  KEY `idx_followed_id` (`followed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注关系表';

ALTER TABLE user 
ADD COLUMN intro VARCHAR(255) DEFAULT '' COMMENT '个人简介',
ADD COLUMN gender TINYINT DEFAULT 0 COMMENT '性别：0未知，1男，2女',
ADD COLUMN ip_location VARCHAR(100) DEFAULT '未知' COMMENT 'IP属地';

UPDATE user 
SET intro = '还没有简介哦~', gender = 1, ip_location = '陕西'
WHERE gender IS NULL OR gender = 0; -- 把老用户的简介和性别补齐，这里性别默认赋值男

CREATE TABLE `comment` (
  `id` BIGINT AUTO_INCREMENT COMMENT '评论主键',
  `post_id` INT NOT NULL COMMENT '这条评论属于哪篇笔记',
  `user_id` INT NOT NULL COMMENT '发表这条评论的用户ID',
  `content` VARCHAR(500) NOT NULL COMMENT '评论的具体内容',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID(0代表是一级评论)',
  `reply_to_user_id` INT DEFAULT NULL COMMENT '被回复人的ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笔记评论表';

-- 给笔记表增加逻辑删除字段
ALTER TABLE `post` ADD COLUMN `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常，1已删除（作者删帖）';

-- 给评论表增加逻辑删除字段
ALTER TABLE `comment` ADD COLUMN `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0正常，1已删除（用户删评）';

-- 让每篇笔记都定格发布时的 IP
ALTER TABLE `post` ADD COLUMN `ip_location` VARCHAR(50) DEFAULT '未知' COMMENT '发布时的IP属地';

-- 让每条评论都定格发布时的 IP
ALTER TABLE `comment` ADD COLUMN `ip_location` VARCHAR(50) DEFAULT '未知' COMMENT '评论时的IP属地';

-- 给 comment 表加上点赞统计字段
ALTER TABLE comment ADD COLUMN likes_count INT DEFAULT 0;

-- 创建独立的评论点赞表
CREATE TABLE comment_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_comment_user (comment_id, user_id)
);

ALTER TABLE `post` ADD COLUMN `is_video` TINYINT DEFAULT 0 COMMENT '是否为视频：0图片，1视频';