USE simplenote_db;

START TRANSACTION;

-- Post 24: 武康路晚春真的很好拍
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (24, 5, '武康路最近确实很适合傍晚去，五点半之后的光线特别温柔。', 0, NULL, 8, '2026-04-13 18:42:00', 0, '上海');
SET @c24_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (24, 1, '我也是差不多这个时间段去的，确实比中午更出片。', @c24_1, 5, 3, '2026-04-13 18:48:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (24, 6, '周末人会比工作日多很多，想拍空一点的街景还是建议工作日去。', 0, NULL, 6, '2026-04-13 19:05:00', 0, '上海');
SET @c24_2 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (24, 8, '同意，工作日傍晚的体验最好，周六下午真的全是拍照的人。', @c24_2, 6, 2, '2026-04-13 19:16:00', 0, '上海');

-- Post 25: 安福路周末散步路线分享
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (25, 1, '安福路和武康路这条线真的很稳，第一次带朋友去也不会踩雷。', 0, NULL, 7, '2026-04-13 10:40:00', 0, '上海');
SET @c25_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (25, 2, '对，我最近朋友来上海基本都会带去这边先走一圈。', @c25_1, 1, 3, '2026-04-13 10:56:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (25, 8, '常熟路出来一路走过去很顺，不会绕路这点很加分。', 0, NULL, 5, '2026-04-13 11:12:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (25, 9, '而且吃饭和喝咖啡都好找，适合临时约见面。', 0, NULL, 4, '2026-04-13 11:30:00', 0, '上海');

-- Post 29: 上海最近很火的拍照小街
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (29, 2, '最近这种生活感街区确实很火，比那种硬打卡点更自然。', 0, NULL, 6, '2026-04-11 17:00:00', 0, '上海');
SET @c29_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (29, 6, '对，主要是整条街的氛围在线，不是只靠某一家店撑着。', @c29_1, 2, 3, '2026-04-11 17:12:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (29, 10, '我更喜欢这种能边走边拍的地方，没那么累。', 0, NULL, 2, '2026-04-11 17:28:00', 0, '上海');

-- Post 30: 杭州九溪徒步新手也能走
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (30, 4, '九溪对新手真的友好，强度不大，但一路景色都很舒服。', 0, NULL, 8, '2026-04-13 09:48:00', 0, '杭州');
SET @c30_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (30, 7, '是的，我这次就是想找个不太累但能接触一点山路的地方。', @c30_1, 4, 4, '2026-04-13 09:55:00', 0, '杭州');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (30, 1, '穿普通运动鞋就可以吗，最近也想去试试。', 0, NULL, 3, '2026-04-13 10:08:00', 0, '杭州');
SET @c30_2 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (30, 9, '可以，我上次穿轻便运动鞋完全够用，重点是别穿太滑的底。', @c30_2, 1, 2, '2026-04-13 10:16:00', 0, '杭州');

-- Post 31: 西湖边早起一小时很值
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (31, 3, '早起去西湖真的会刷新印象，和白天完全不是一个密度。', 0, NULL, 7, '2026-04-12 08:40:00', 0, '杭州');
SET @c31_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (31, 8, '对，七点多那个时间点刚刚好，再晚一点游客就明显多起来了。', @c31_1, 3, 3, '2026-04-12 08:52:00', 0, '杭州');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (31, 6, '早起党看到这条太有共鸣了，晨光真的很顶。', 0, NULL, 5, '2026-04-12 09:10:00', 0, '杭州');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (31, 10, '而且那个时候沿湖边散步的人整体都很安静，体验很好。', 0, NULL, 4, '2026-04-12 09:26:00', 0, '杭州');

-- Post 35: 法喜寺附近春天太有氛围
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (35, 5, '法喜寺附近的氛围感确实很强，适合慢慢走，不适合赶行程。', 0, NULL, 6, '2026-04-08 14:10:00', 0, '杭州');
SET @c35_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (35, 2, '对，我这次也是完全没想打卡几个点，结果体验反而更舒服。', @c35_1, 5, 2, '2026-04-08 14:20:00', 0, '杭州');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (35, 7, '如果想找安静一点的春天去处，这边真的很合适。', 0, NULL, 3, '2026-04-08 14:36:00', 0, '杭州');

-- Post 38: 雨天去看展比逛街更舒服
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (38, 1, '我现在雨天首选也是看展，至少不会一路都在找地方躲雨。', 0, NULL, 7, '2026-04-13 15:36:00', 0, '上海');
SET @c38_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (38, 5, '是的，而且比商场纯逛街更有内容一点，不容易无聊。', @c38_1, 1, 3, '2026-04-13 15:48:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (38, 8, '最近上海这类室内活动确实讨论度很高，雨天真的太需要了。', 0, NULL, 5, '2026-04-13 16:06:00', 0, '上海');
SET @c38_2 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (38, 3, '尤其适合朋友见面，边看边聊会自然很多。', @c38_2, 8, 2, '2026-04-13 16:18:00', 0, '上海');

-- Post 39: 适合雨天约会的室内路线
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (39, 4, '先看展再吃饭最后喝咖啡，这条线真的很适合雨天约会。', 0, NULL, 6, '2026-04-12 18:02:00', 0, '上海');
SET @c39_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (39, 6, '对，重点是不赶，能留出聊天的空间。', @c39_1, 4, 2, '2026-04-12 18:10:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (39, 9, '比单纯去商场更像认真安排过的约会。', 0, NULL, 4, '2026-04-12 18:26:00', 0, '上海');

-- Post 41: 周末室内去处我投展览一票
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (41, 2, '最近如果问我周末室内去哪，我也大概率会先推荐展览。', 0, NULL, 7, '2026-04-10 19:00:00', 0, '上海');
SET @c41_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (41, 8, '主要是节奏可以自己掌控，这点很舒服。', @c41_1, 2, 3, '2026-04-10 19:08:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (41, 5, '而且有些展看完之后周边吃饭也很方便。', 0, NULL, 2, '2026-04-10 19:26:00', 0, '上海');

-- Post 42: 南京夜里散步真的会上头
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (42, 1, '南京那种老城区夜里散步的氛围感，真的很容易让人上头。', 0, NULL, 8, '2026-04-13 21:32:00', 0, '南京');
SET @c42_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (42, 9, '是的，尤其灯一亮起来以后，整个城市会变得很温柔。', @c42_1, 1, 4, '2026-04-13 21:40:00', 0, '南京');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (42, 6, '比起白天打卡，我反而更喜欢这种夜里的慢节奏。', 0, NULL, 5, '2026-04-13 21:52:00', 0, '南京');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (42, 4, '最近站内好像也一直有人在聊南京夜游这类路线。', 0, NULL, 3, '2026-04-13 22:06:00', 0, '南京');

-- Post 43: 老门东晚上的烟火气很足
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (43, 3, '老门东晚上确实比白天更有味道，适合带朋友第一次去。', 0, NULL, 6, '2026-04-12 20:36:00', 0, '南京');
SET @c43_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (43, 10, '对，晚上那种烟火气特别完整，吃饭和散步都方便。', @c43_1, 3, 2, '2026-04-12 20:44:00', 0, '南京');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (43, 8, '如果只安排南京一晚，这里真的很适合。', 0, NULL, 3, '2026-04-12 21:02:00', 0, '南京');

-- Post 46: 玄武湖春天散步太稳了
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (46, 5, '玄武湖一直都很稳，尤其适合不想做太多攻略的时候。', 0, NULL, 5, '2026-04-09 18:00:00', 0, '南京');
SET @c46_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (46, 3, '对，它的优点就是怎么走都不会太累，容错率高。', @c46_1, 5, 2, '2026-04-09 18:10:00', 0, '南京');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (46, 7, '春天傍晚去真的很舒服，风也不会太大。', 0, NULL, 3, '2026-04-09 18:26:00', 0, '南京');

-- Post 48: 预算不高也能有周末约会感
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (48, 2, '低预算路线最近真的很受欢迎，大家更想要氛围而不是硬消费。', 0, NULL, 7, '2026-04-07 19:20:00', 0, '上海');
SET @c48_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (48, 5, '对，我这次就觉得花得不多但周末感很足。', @c48_1, 2, 3, '2026-04-07 19:32:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (48, 10, '公园加散步这种安排现在比排队餐厅更吸引我。', 0, NULL, 4, '2026-04-07 19:48:00', 0, '上海');

-- Post 51: 春天的公园真的比商场更治愈
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (51, 4, '最近站内好多帖子都在聊公园和野餐，春天这类去处确实很能打。', 0, NULL, 6, '2026-04-04 16:10:00', 0, '杭州');
SET @c51_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (51, 8, '是的，而且成本低，随时都能出发。', @c51_1, 4, 2, '2026-04-04 16:22:00', 0, '杭州');

-- Post 54: 周末露营广告，装备全新转卖
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (54, 6, '这个更像转卖信息，和找去处的帖子还是不太一样。', 0, NULL, 1, '2026-04-13 09:30:00', 0, '苏州');

-- Post 56: 求问明天去哪玩比较好
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (56, 9, '如果在杭州的话，最近九溪和植物园讨论度都挺高。', 0, NULL, 2, '2026-04-11 22:20:00', 0, '杭州');

-- Post 58: 上海周末拍照喝咖啡一条龙
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (58, 1, '这类半天路线最近在上海真的很受欢迎，松弛感也够。', 0, NULL, 8, '2026-04-01 16:28:00', 0, '上海');
SET @c58_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (58, 5, '对，拍照和喝咖啡放在一起，特别适合久没见面的朋友。', @c58_1, 1, 3, '2026-04-01 16:38:00', 0, '上海');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (58, 7, '如果只有半天时间，这种安排真的比跑远路高效。', 0, NULL, 5, '2026-04-01 16:52:00', 0, '上海');
SET @c58_2 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (58, 3, '而且不会太累，逛完还能留点余力吃饭。', @c58_2, 7, 2, '2026-04-01 17:04:00', 0, '上海');

-- Post 59: 杭州春天最喜欢的松弛感路线
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (59, 2, '杭州最近被聊得最多的确实不是硬核景点，而是这种松弛路线。', 0, NULL, 7, '2026-04-02 10:36:00', 0, '杭州');
SET @c59_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (59, 6, '对，感觉大家现在更在意舒服和节奏，不是必须打卡很多地方。', @c59_1, 2, 3, '2026-04-02 10:46:00', 0, '杭州');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (59, 10, '公园、湖边、树多的街区最近都很有讨论度。', 0, NULL, 4, '2026-04-02 11:02:00', 0, '杭州');

-- Post 60: 南京晚上适合约会的地方
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (60, 5, '南京晚上约会这类路线最近也很火，夜景和散步都很加分。', 0, NULL, 7, '2026-04-03 19:50:00', 0, '南京');
SET @c60_1 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (60, 7, '我也是这么想的，先吃饭再慢慢走会自然很多。', @c60_1, 5, 3, '2026-04-03 20:02:00', 0, '南京');
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (60, 9, '比起特别正式的安排，这种路线更容易放松。', 0, NULL, 4, '2026-04-03 20:18:00', 0, '南京');
SET @c60_2 = LAST_INSERT_ID();
INSERT INTO `comment` (`post_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `likes_count`, `create_time`, `is_deleted`, `ip_location`)
VALUES (60, 1, '而且最近天气刚好，晚上走路不会太热也不会冷。', @c60_2, 9, 2, '2026-04-03 20:30:00', 0, '南京');

COMMIT;
