-- secondSkill
-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (
  `id` bigint(20) NOT NULL,
  `goods_name` varchar(30) DEFAULT NULL,
  `goods_title` varchar(64) DEFAULT NULL,
  `goods_img` varchar(64) DEFAULT NULL,
  `goods_detail` longtext,
  `goods_price` decimal(10,2) DEFAULT NULL,
  `goods_stock` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('1', 'iphoneX', 'Apple/苹果iPhone X 全网通4G手机苹果X 10', '/img/iphonex.png', 'Apple/苹果iPhone X 全网通4G手机苹果X 10', '7788.00', '100');
INSERT INTO `goods` VALUES ('2', '华为 META 10', 'Huawei/华为 META 10全网通4G智能手机', '/img/meta10.png', 'Huawei/华为 META 10 8G+256G 全网通4G智能手机', '5299.00', '50');
INSERT INTO `goods` VALUES ('3', '苹果8', 'Apple/苹果iPhone 8 全网通4G手机苹果8', '/img/iphone8.png', 'Apple/苹果iPhone 8 全网通4G手机苹果8', '999.00', '9999');
INSERT INTO `goods` VALUES ('4', '小米 6', 'xiaomi/小米 6', '/img/mi6.png', '小米 6', '1599.00', '200');

-- ----------------------------
-- Table structure for sec_skill_goods
-- ----------------------------
DROP TABLE IF EXISTS `sec_skill_goods`;
CREATE TABLE `sec_skill_goods` (
  `id` bigint(20) NOT NULL,
  `goods_id` bigint(20) DEFAULT NULL,
  `sec_skill_price` decimal(10,2) DEFAULT NULL,
  `stock_count` int(11) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `goods_id` (`goods_id`),
  CONSTRAINT `sec_skill_goods_ibfk_1` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sec_skill_goods
-- ----------------------------
INSERT INTO `sec_skill_goods` VALUES ('1', '1', '999', '10', '2018-06-13 11:05:50', '2018-06-13 11:08:00');
INSERT INTO `sec_skill_goods` VALUES ('2', '2', '888', '10', '2018-06-01 00:00:00', '2022-10-01 22:56:15');
INSERT INTO `sec_skill_goods` VALUES ('3', '3', '799', '10', '2018-06-17 23:05:28', '2018-12-01 23:05:34');
INSERT INTO `sec_skill_goods` VALUES ('4', '4', '499', '10', '2018-06-17 23:05:31', '2021-08-17 23:05:42');

-- ----------------------------
-- Table structure for sec_skill_order
-- ----------------------------
DROP TABLE IF EXISTS `sec_skill_order`;
CREATE TABLE `sec_skill_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `goods_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `u_uid_gid` (`user_id`,`goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `sec_skill_user`;
CREATE TABLE `sec_skill_user` (
  `id` bigint(20) NOT NULL COMMENT '用户手机号码',
  `nickname` varchar(255) NOT NULL,
  `password` varchar(32) DEFAULT NULL COMMENT 'md5(md5(pass明文+固定salt)+salt)',
  `salt` varchar(10) NOT NULL,
  `head` varchar(128) DEFAULT NULL COMMENT '头像，云存储的ID',
  `register_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '上次登陆时间',
  `login_count` int(11) DEFAULT '0' COMMENT '登陆次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sec_skill_user
-- ----------------------------
INSERT INTO `sec_skill_user` VALUES ('12345678901', '测试', '5e648c47d8ef436833ef6a78b096a566', '1a2b3c4d', null, '2018-06-13 15:45:54', '2018-06-13 15:45:54', '0');
INSERT INTO `sec_skill_user` VALUES ('12345678900', 'dahuaidan', '5e648c47d8ef436833ef6a78b096a566', '1a2b3c4d', null, '2018-06-10 19:18:17', '2018-06-10 19:18:17', '0');
-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `goods_id` bigint(20) DEFAULT NULL,
  `delivery_addr_id` bigint(20) DEFAULT NULL COMMENT '收获地址',
  `goods_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '冗余过来的商品名称',
  `goods_count` int(11) DEFAULT NULL COMMENT 's数量',
  `goods_price` decimal(10,2) DEFAULT NULL,
  `order_channel` tinyint(4) DEFAULT NULL COMMENT '订单渠道，1在线，2android，3ios',
  `status` tinyint(4) DEFAULT NULL COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
  `create_date` datetime DEFAULT NULL,
  `pay_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;




