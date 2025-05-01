create database techblog;
use techblog;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号码',
    `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码，加密存储',
    `nick_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '昵称，默认是用户id',
    `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '人物头像',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uniqe_key_phone`(`phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1010 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

INSERT INTO `tb_user` VALUES (1, '13686869696', '', '小鱼同学', '/imgs/blogs/blog1.jpg', '2021-12-24 10:27:19', '2022-01-11 16:04:00');
INSERT INTO `tb_user` VALUES (2, '13838411438', '', '可可今天不吃肉', '/imgs/icons/kkjtbcr.jpg', '2021-12-24 15:14:39', '2021-12-28 19:58:04');
INSERT INTO `tb_user` VALUES (3, '13456789011', '', 'user_slxaxy2au9f3tanffaxr', '', '2022-01-07 12:07:53', '2022-01-07 12:07:53');
INSERT INTO `tb_user` VALUES (4, '13456789001', '', 'user_n0bb8mwwg4', '', '2022-01-07 16:11:33', '2022-01-07 16:11:33');
-- ----------------------------
-- Table structure for tb_user
-- ----------------------------

-- ----------------------------
-- Table structure for tb_user_info
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_info`;
CREATE TABLE `tb_user_info`  (
    `user_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键，用户id',
    `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '城市名称',
    `introduce` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '个人介绍，不要超过128个字符',
    `fans` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '粉丝数量',
    `followee` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '关注的人的数量',
    `gender` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '性别，0：男，1：女',
    `birthday` date NULL DEFAULT NULL COMMENT '生日',
    `credits` int(8) UNSIGNED NULL DEFAULT 0 COMMENT '积分',
    `level` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '会员级别，0~9级,0代表未开通会员',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`) USING BTREE,
    CONSTRAINT `fk_user_info_user` FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;
-- ----------------------------
-- Table structure for tb_user_info
-- ----------------------------

-- ----------------------------
-- Table structure for tb_follow
-- ----------------------------
DROP TABLE IF EXISTS `tb_follow`;
CREATE TABLE `tb_follow`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `follow_user_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联的用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;
-- ----------------------------
-- Table structure for tb_follow
-- ----------------------------

-- ----------------------------
-- Table structure for tb_voucher
-- ----------------------------
DROP TABLE IF EXISTS `tb_voucher`;
CREATE TABLE `tb_voucher`  (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代金券标题',
    `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '副标题',
    `rules` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用规则',
    `pay_value` bigint(10) UNSIGNED NOT NULL COMMENT '支付金额，单位是分。例如200代表2元',
    `actual_value` bigint(10) NOT NULL COMMENT '抵扣金额，单位是分。例如200代表2元',
    `type` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '0,普通券；1,秒杀券',
    `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '1,上架; 2,下架; 3,过期',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;

INSERT INTO `tb_voucher` VALUES (1, '50元代金券', '周一至周日均可使用', '全场通用\\n无需预约\\n可无限叠加\\不兑现、不找零\\n仅限堂食', 4750, 5000, 0, 1, '2022-01-04 09:42:39', '2022-01-04 09:43:31');
INSERT INTO `tb_voucher` VALUES (2, '100元代金券', '周一至周日均可使用', '', 100, 99, 1, 1, '2025-01-04 09:42:39', '2026-01-04 09:43:31');

-- ----------------------------
-- Table structure for tb_voucher
-- ----------------------------

-- ----------------------------
-- Table structure for tb_seckill_voucher
-- ----------------------------
DROP TABLE IF EXISTS `tb_seckill_voucher`;
CREATE TABLE `tb_seckill_voucher`  (
  `voucher_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联的优惠券的id',
  `stock` int(8) NOT NULL COMMENT '库存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `begin_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
  `end_time`   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`voucher_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀优惠券表，与优惠券是一对一关系' ROW_FORMAT = Compact;

INSERT INTO `tb_seckill_voucher` VALUES (2,10,'2025-01-04 09:42:39','2025-01-04 09:42:39','2026-01-04 09:43:31','2025-01-04 09:42:39');
-- ----------------------------
-- Table structure for tb_seckill_voucher
-- ----------------------------

-- ----------------------------
-- Table structure for tb_voucher_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_voucher_order`;
CREATE TABLE `tb_voucher_order`  (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '下单的用户id',
    `voucher_id` bigint(20) UNSIGNED NOT NULL COMMENT '购买的代金券id',
    `pay_type` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '支付方式 1：余额支付；2：支付宝；3：微信',
    `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
    `use_time` timestamp NULL DEFAULT NULL COMMENT '核销时间',
    `refund_time` timestamp NULL DEFAULT NULL COMMENT '退款时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Compact;
-- ----------------------------
-- Table structure for tb_voucher_order
-- ----------------------------

-- ----------------------------
-- Table structure for tb_article
-- ----------------------------
DROP TABLE IF EXISTS tb_articles;
CREATE TABLE tb_articles (
    id bigint(20) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(120) NOT NULL COMMENT '標題',
    summary VARCHAR(255) COMMENT '摘要',
    content LONGTEXT NOT NULL COMMENT '正文（支持Markdown）',
    cover_image VARCHAR(255) COMMENT '封面圖路徑',
    user_id bigint(20) UNSIGNED COMMENT '用戶ID',
    view_count INT UNSIGNED DEFAULT 0 COMMENT '閱讀量',
    comment_count INT UNSIGNED DEFAULT 0 COMMENT '評論數',
    like_count INT UNSIGNED DEFAULT 0 COMMENT '點贊數',
    collect_count INT UNSIGNED DEFAULT 0 COMMENT '收藏數',
    coin_count INT UNSIGNED DEFAULT 0 COMMENT '投幣數',
    is_draft BOOLEAN DEFAULT FALSE COMMENT '草稿狀態',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '創建時間',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO tb_articles VALUES (1,'xxx','xxx','xxx','xxx',1,100,5,20,10,2,false,'2022-01-04 09:42:39', '2022-01-04 09:43:31');
INSERT INTO tb_articles VALUES (2,'yyy','yyy','yyy','yyy',2,100,5,20,10,2,false,'2022-01-04 09:42:39', '2022-01-04 09:43:31');
-- ----------------------------
-- Table structure for tb_article
-- ----------------------------

-- ----------------------------
-- Table structure for tb_tag
-- ----------------------------
DROP TABLE IF EXISTS tb_tags;
CREATE TABLE tb_tags (
    id bigint(20) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE COMMENT '標籤名稱',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO tb_tags (id, name) VALUES(1, 'Spring Boot'),(2, 'Redis'),(3, 'Docker'),(4, 'Java');
-- ----------------------------
-- Table structure for tb_tag
-- ----------------------------

-- ----------------------------
-- Table structure for tb_article_tag
-- ----------------------------
DROP TABLE IF EXISTS tb_article_tags;
CREATE TABLE tb_article_tags (
    article_id bigint(20) UNSIGNED NOT NULL,
    tag_id bigint(20) UNSIGNED NOT NULL,
    PRIMARY KEY (article_id, tag_id),
    FOREIGN KEY (article_id) REFERENCES tb_articles(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tb_tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
-- ----------------------------
-- Table structure for tb_article_tag
-- ----------------------------

-- ----------------------------
-- Table structure for tb_comments
-- ----------------------------
DROP TABLE IF EXISTS tb_comments;
CREATE TABLE tb_comments (
    id bigint(20) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    content TEXT NOT NULL,
    article_id bigint(20) UNSIGNED NOT NULL,
    user_id bigint(20) UNSIGNED NOT NULL,
    parent_id bigint(20) UNSIGNED DEFAULT NULL COMMENT '父評論ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (article_id) REFERENCES tb_articles(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES tb_comments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
-- ----------------------------
-- Table structure for tb_comments
-- ----------------------------
