/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50719
Source Host           : localhost:3306
Source Database       : delay_order

Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001

Date: 2018-11-25 12:50:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `order_exp`
-- ----------------------------
DROP TABLE IF EXISTS `order_exp`;
CREATE TABLE `order_exp` (
  `id` bigint(22) NOT NULL AUTO_INCREMENT COMMENT '订单的主键',
  `order_no` varchar(50) NOT NULL COMMENT '订单的编号',
  `order_note` varchar(100) NOT NULL COMMENT '订单的说明',
  `insert_time` datetime NOT NULL COMMENT '插入订单的时间',
  `expire_duration` bigint(22) NOT NULL COMMENT '订单的过期时长，单位秒',
  `expire_time` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '订单的过期时间',
  `order_status` smallint(6) NOT NULL DEFAULT '0' COMMENT '订单的状态，0：未支付；1：已支付；-1：已过期，关闭',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of order_exp
-- ----------------------------