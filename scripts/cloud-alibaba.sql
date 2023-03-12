/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 50724
Source Host           : localhost:3306
Source Database       : cloud-alibaba

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2023-03-12 16:46:36
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACCOUNT_CODE` varchar(255) DEFAULT NULL,
  `ACCOUNT_NAME` varchar(255) DEFAULT NULL,
  `AMOUNT` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_ACCOUNT_CODE` (`ACCOUNT_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES ('17', 'tian', 'tian', '140.00');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `PRODUCT_CODE` varchar(255) DEFAULT NULL COMMENT '编码',
  `PRODUCT_NAME` varchar(255) DEFAULT NULL COMMENT '名称',
  `COUNT` int(11) DEFAULT '0' COMMENT '库存数量',
  `PRICE` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_PRODUCT_CODE` (`PRODUCT_CODE`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1', '001', 'demoDataxx', '6', '10.00');
INSERT INTO `product` VALUES ('2', '002', '手表', '20', '20.00');
INSERT INTO `product` VALUES ('3', '003', '键盘', '30', '30.00');
INSERT INTO `product` VALUES ('4', '004', '辣条', '40', '40.00');

-- ----------------------------
-- Table structure for rocketmq_transaction_log
-- ----------------------------
DROP TABLE IF EXISTS `rocketmq_transaction_log`;
CREATE TABLE `rocketmq_transaction_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transaction_id` varchar(50) DEFAULT NULL COMMENT '事务ID',
  `log` varchar(500) DEFAULT '' COMMENT '日志',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of rocketmq_transaction_log
-- ----------------------------
INSERT INTO `rocketmq_transaction_log` VALUES ('5', '8078a2f6-75e7-4524-8f05-9f5aeb7b121b', '执行删除订单操作', null);
INSERT INTO `rocketmq_transaction_log` VALUES ('6', '83277620-59a5-425c-ab21-324f6f25c0b8', '执行删除订单操作', null);
INSERT INTO `rocketmq_transaction_log` VALUES ('7', '23b70913-37c0-4ea3-a622-3a724643a397', '执行删除订单操作', null);
INSERT INTO `rocketmq_transaction_log` VALUES ('8', '0724b771-cf7b-4bb8-b3a1-7236123fd0d2', '执行删除订单操作', null);
INSERT INTO `rocketmq_transaction_log` VALUES ('10', '127f7bcb-fb18-4c74-9490-4ec0c1d28ac1', '执行删除订单操作', '2023-03-09 10:54:44');
INSERT INTO `rocketmq_transaction_log` VALUES ('11', 'c4d7951e-6c1d-4754-9933-052ee4735585', '执行删除订单操作', '2023-03-09 10:56:09');
INSERT INTO `rocketmq_transaction_log` VALUES ('12', '04f351df-1bd7-4e78-bf58-d8e4f38d35d8', '执行删除订单操作', '2023-03-09 11:01:58');
INSERT INTO `rocketmq_transaction_log` VALUES ('15', 'd074b0ad-28c0-49b5-92f2-b41febf52757', '执行删除订单操作', '2023-03-09 11:21:47');
INSERT INTO `rocketmq_transaction_log` VALUES ('16', '17cf351b-b88d-4234-a419-302e182ee499', '执行删除订单操作', '2023-03-09 11:25:17');
INSERT INTO `rocketmq_transaction_log` VALUES ('17', 'cafe5a52-ad17-45fe-9284-9b35e4d7fde3', '执行删除订单操作', '2023-03-09 11:36:01');
INSERT INTO `rocketmq_transaction_log` VALUES ('18', '78354cbc-9396-4ff6-b8c3-d26ca39173c3', '执行删除订单操作', '2023-03-09 12:54:50');

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `ORDER_NO` varchar(255) DEFAULT NULL,
  `ACCOUNT_CODE` varchar(255) DEFAULT NULL,
  `PRODUCT_CODE` varchar(255) DEFAULT NULL,
  `COUNT` int(11) DEFAULT '0',
  `AMOUNT` decimal(10,2) DEFAULT '0.00',
  `STATUS` varchar(10) DEFAULT 'VALID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES ('3', 'b1e4e531-1f28-4cc5-b9fb-889b6231d213', 'tian', '001', '2', '20.00', 'INVALID', '2022-12-31 18:09:56');
INSERT INTO `t_order` VALUES ('4', 'b4094c99-ffdd-4146-bb05-8967b8bca0a0', 'tian', '001', '2', '20.00', 'INVALID', '2022-12-31 18:09:56');
INSERT INTO `t_order` VALUES ('5', '30faaacd-36d8-41f2-9e42-1e3c48d16373', 'tian', '001', '1', '10.00', 'VALID', '2022-12-31 19:08:47');
INSERT INTO `t_order` VALUES ('6', 'fd2e3b17-f95c-4cfa-9852-01388d1d016d', 'tian', '001', '1', '10.00', 'VALID', '2022-12-31 19:09:18');
INSERT INTO `t_order` VALUES ('7', '30583f6a-18c4-4015-a836-bc3d41ecd224', 'tian', '001', '1', '10.00', 'VALID', '2022-12-31 23:33:13');
INSERT INTO `t_order` VALUES ('8', '84744db5-045b-4547-800a-c6f9e6b2f633', 'tian', '001', '1', '10.00', 'INVALID', '2022-12-31 23:34:29');

-- ----------------------------
-- Table structure for t_order2021
-- ----------------------------
DROP TABLE IF EXISTS `t_order2021`;
CREATE TABLE `t_order2021` (
  `id` bigint(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `cloumn` varchar(255) DEFAULT NULL,
  `day_date` char(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_order2021
-- ----------------------------
INSERT INTO `t_order2021` VALUES ('630435211867324416', null, null, null, '2021');
INSERT INTO `t_order2021` VALUES ('630435686146637824', null, null, null, '2021');

-- ----------------------------
-- Table structure for t_order2022
-- ----------------------------
DROP TABLE IF EXISTS `t_order2022`;
CREATE TABLE `t_order2022` (
  `id` bigint(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `cloumn` varchar(255) DEFAULT NULL,
  `day_date` char(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_order2022
-- ----------------------------
INSERT INTO `t_order2022` VALUES ('630434969088425984', null, null, null, '2022');
INSERT INTO `t_order2022` VALUES ('630435744443269120', null, null, null, '2022');

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'increment id',
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(100) NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime NOT NULL COMMENT 'create datetime',
  `log_modified` datetime NOT NULL COMMENT 'modify datetime',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='AT transaction mode undo table';

-- ----------------------------
-- Records of undo_log
-- ----------------------------
