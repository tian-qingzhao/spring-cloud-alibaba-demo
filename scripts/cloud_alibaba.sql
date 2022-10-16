/*
Navicat MySQL Data Transfer

Source Server         : 虚拟桌面
Source Server Version : 50712
Source Host           : 10.0.23.48:3306
Source Database       : cloud_alibaba

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2020-08-27 18:30:33
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
INSERT INTO `account` VALUES ('1', 'javadaily', 'JAVA日知录', '999.00');
INSERT INTO `account` VALUES ('17', 'jianzh5', 'jianzh5', '199.00');

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
INSERT INTO `product` VALUES ('1', 'demoDataxx', 'demoDataxx', '10', '10.00');
INSERT INTO `product` VALUES ('2', 'P002', '手表', '93', '250.00');
INSERT INTO `product` VALUES ('3', 'P003', '键盘', '100', '5.00');
INSERT INTO `product` VALUES ('4', 'P004', '辣条', '987', '20.00');

-- ----------------------------
-- Table structure for rocketmq_transaction_log
-- ----------------------------
DROP TABLE IF EXISTS `rocketmq_transaction_log`;
CREATE TABLE `rocketmq_transaction_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transaction_id` varchar(50) DEFAULT NULL COMMENT '事务ID',
  `log` varchar(500) DEFAULT '' COMMENT '日志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of rocketmq_transaction_log
-- ----------------------------
INSERT INTO `rocketmq_transaction_log` VALUES ('5', '8078a2f6-75e7-4524-8f05-9f5aeb7b121b', '执行删除订单操作');

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
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES ('43', '3881a524-93c0-41af-9f83-b5bbf6da9dbe', 'jianzh5', 'P004', '10', '200.00', 'INVALID');
INSERT INTO `t_order` VALUES ('71', '86425e0b-f854-47d0-8498-14f7c8e78668', 'javadaily', 'P002', '1', '1.00', 'VALID');
INSERT INTO `t_order` VALUES ('72', '33a682eb-9232-4126-b166-1ce10fee1664', 'jianzh5', 'P002', '1', '1.00', 'VALID');

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
