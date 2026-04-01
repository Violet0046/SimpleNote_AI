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