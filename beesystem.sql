/*
 Navicat Premium Data Transfer

 Source Server         : Bee
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : beesystem

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 23/03/2026 17:12:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apiaries
-- ----------------------------
DROP TABLE IF EXISTS `apiaries`;
CREATE TABLE `apiaries`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '蜂场id',
  `Fname` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂场名称',
  `Latitude` decimal(4, 2) NOT NULL COMMENT '纬度（-90~90°N）',
  `Longitude` decimal(5, 2) NOT NULL COMMENT '经度（-180~180°E）',
  `Altitude` int NOT NULL COMMENT '海拔（米）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_Fname`(`Fname` ASC) USING BTREE,
  INDEX `Pos`(`Latitude` ASC, `Longitude` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of apiaries
-- ----------------------------
INSERT INTO `apiaries` VALUES (1, '广东荔枝之乡采蜜点', 23.12, 113.27, 45);
INSERT INTO `apiaries` VALUES (2, '黑龙江完达山野生蜂场', 46.88, 131.12, 560);
INSERT INTO `apiaries` VALUES (3, '陕西秦岭土蜂蜜基地', 33.99, 108.54, 1150);
INSERT INTO `apiaries` VALUES (4, '青海门源油菜花海蜂场', 37.36, 101.62, 2900);
INSERT INTO `apiaries` VALUES (5, '四川若尔盖高原蜂场', 33.57, 102.99, 3450);
INSERT INTO `apiaries` VALUES (8, '许建毫王子养蜂场', 23.11, 113.18, 300);

-- ----------------------------
-- Table structure for beecolonies
-- ----------------------------
DROP TABLE IF EXISTS `beecolonies`;
CREATE TABLE `beecolonies`  (
  `Bno` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂箱编号',
  `BType` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂种',
  `BKing` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂王状态',
  `FlockAssess` char(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '群势评估',
  PRIMARY KEY (`Bno`) USING BTREE,
  INDEX `idx_BType`(`BType` ASC) USING BTREE,
  CONSTRAINT `p_BKing` CHECK ((`BKing` = _utf8mb4'好') or (`BKing` = _utf8mb4'差')),
  CONSTRAINT `p_FA` CHECK ((`FlockAssess` = _utf8mb4'分蜂') or (`FlockAssess` = _utf8mb4'合群'))
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of beecolonies
-- ----------------------------
INSERT INTO `beecolonies` VALUES ('BX001', '意大利蜂', '好', '合群');
INSERT INTO `beecolonies` VALUES ('BX002', '中华蜜蜂', '好', '分蜂');
INSERT INTO `beecolonies` VALUES ('BX003', '卡尼鄂拉蜂', '差', '合群');
INSERT INTO `beecolonies` VALUES ('BX004', '高加索蜂', '好', '分蜂');
INSERT INTO `beecolonies` VALUES ('BX005', '中华蜜蜂', '差', '分蜂');
INSERT INTO `beecolonies` VALUES ('BX006', '意大利蜂', '好', '合群');
INSERT INTO `beecolonies` VALUES ('BX007', '东北黑蜂', '好', '分蜂');
INSERT INTO `beecolonies` VALUES ('BX008', '安纳托利亚蜂', '差', '合群');
INSERT INTO `beecolonies` VALUES ('BX009', '中华蜜蜂', '好', '分蜂');
INSERT INTO `beecolonies` VALUES ('BX010', '意大利蜂', '差', '合群');

-- ----------------------------
-- Table structure for farm_plant
-- ----------------------------
DROP TABLE IF EXISTS `farm_plant`;
CREATE TABLE `farm_plant`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '关联id',
  `farm_id` int NOT NULL COMMENT '蜂场id',
  `plant_id` int NOT NULL COMMENT '蜜源植物id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `b_farm_plant`(`farm_id` ASC, `plant_id` ASC) USING BTREE,
  INDEX `plant_id`(`plant_id` ASC) USING BTREE,
  CONSTRAINT `farm_plant_ibfk_1` FOREIGN KEY (`farm_id`) REFERENCES `apiaries` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `farm_plant_ibfk_2` FOREIGN KEY (`plant_id`) REFERENCES `nectorsources` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of farm_plant
-- ----------------------------
INSERT INTO `farm_plant` VALUES (2, 1, 1);
INSERT INTO `farm_plant` VALUES (1, 1, 2);
INSERT INTO `farm_plant` VALUES (3, 1, 6);
INSERT INTO `farm_plant` VALUES (6, 2, 1);
INSERT INTO `farm_plant` VALUES (4, 2, 4);
INSERT INTO `farm_plant` VALUES (5, 2, 8);
INSERT INTO `farm_plant` VALUES (7, 3, 3);
INSERT INTO `farm_plant` VALUES (8, 3, 5);
INSERT INTO `farm_plant` VALUES (9, 3, 7);
INSERT INTO `farm_plant` VALUES (10, 4, 1);
INSERT INTO `farm_plant` VALUES (12, 4, 6);
INSERT INTO `farm_plant` VALUES (11, 4, 8);
INSERT INTO `farm_plant` VALUES (14, 5, 1);
INSERT INTO `farm_plant` VALUES (15, 5, 5);
INSERT INTO `farm_plant` VALUES (13, 5, 8);

-- ----------------------------
-- Table structure for honeybatches
-- ----------------------------
DROP TABLE IF EXISTS `honeybatches`;
CREATE TABLE `honeybatches`  (
  `Hno` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '取蜜批次号',
  `Pro` decimal(5, 2) NOT NULL COMMENT '产量(千克)',
  `Water` decimal(4, 2) NOT NULL COMMENT '含水率（%）',
  `Farm` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '相关蜂场',
  `Bee` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '产蜜蜂种',
  INDEX `Farm`(`Farm` ASC) USING BTREE,
  INDEX `Bee`(`Bee` ASC) USING BTREE,
  CONSTRAINT `honeybatches_ibfk_1` FOREIGN KEY (`Farm`) REFERENCES `apiaries` (`Fname`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `honeybatches_ibfk_2` FOREIGN KEY (`Bee`) REFERENCES `beecolonies` (`BType`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `p_Pro` CHECK (`Pro` >= 0),
  CONSTRAINT `p_Water` CHECK (`Water` between 0 and 100)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of honeybatches
-- ----------------------------
INSERT INTO `honeybatches` VALUES ('202603-001', 12.50, 19.20, '广东荔枝之乡采蜜点', '意大利蜂');
INSERT INTO `honeybatches` VALUES ('202603-002', 8.75, 21.50, '广东荔枝之乡采蜜点', '中华蜜蜂');
INSERT INTO `honeybatches` VALUES ('202604-001', 25.30, 18.40, '陕西秦岭土蜂蜜基地', '意大利蜂');
INSERT INTO `honeybatches` VALUES ('202604-002', 15.60, 17.80, '陕西秦岭土蜂蜜基地', '中华蜜蜂');
INSERT INTO `honeybatches` VALUES ('202605-001', 32.40, 18.10, '青海门源油菜花海蜂场', '意大利蜂');
INSERT INTO `honeybatches` VALUES ('202606-001', 42.80, 17.50, '黑龙江完达山野生蜂场', '东北黑蜂');
INSERT INTO `honeybatches` VALUES ('202606-002', 38.50, 17.90, '黑龙江完达山野生蜂场', '意大利蜂');
INSERT INTO `honeybatches` VALUES ('202607-001', 28.90, 19.80, '陕西秦岭土蜂蜜基地', '意大利蜂');
INSERT INTO `honeybatches` VALUES ('202607-002', 10.20, 22.40, '四川若尔盖高原蜂场', '中华蜜蜂');
INSERT INTO `honeybatches` VALUES ('202609-001', 45.80, 18.60, '四川若尔盖高原蜂场', '意大利蜂');

-- ----------------------------
-- Table structure for inspectionlogs
-- ----------------------------
DROP TABLE IF EXISTS `inspectionlogs`;
CREATE TABLE `inspectionlogs`  (
  `id` int NOT NULL COMMENT '巡检id',
  `BeeType` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂种',
  `PAD` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '病虫害情况',
  `Feed` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '饲喂状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `BeeType`(`BeeType` ASC) USING BTREE,
  CONSTRAINT `inspectionlogs_ibfk_1` FOREIGN KEY (`BeeType`) REFERENCES `beecolonies` (`BType`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `p_Feed` CHECK ((`Feed` = _utf8mb4'已饲') or (`Feed` = _utf8mb4'饥饿')),
  CONSTRAINT `p_PAD` CHECK ((`PAD` = _utf8mb4'有') or (`PAD` = _utf8mb4'无'))
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inspectionlogs
-- ----------------------------
INSERT INTO `inspectionlogs` VALUES (1, '意大利蜂', '无', '已饲');
INSERT INTO `inspectionlogs` VALUES (2, '中华蜜蜂', '无', '已饲');
INSERT INTO `inspectionlogs` VALUES (3, '东北黑蜂', '无', '已饲');
INSERT INTO `inspectionlogs` VALUES (4, '意大利蜂', '有', '饥饿');
INSERT INTO `inspectionlogs` VALUES (5, '中华蜜蜂', '无', '饥饿');
INSERT INTO `inspectionlogs` VALUES (6, '东北黑蜂', '有', '已饲');

-- ----------------------------
-- Table structure for nectorsources
-- ----------------------------
DROP TABLE IF EXISTS `nectorsources`;
CREATE TABLE `nectorsources`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '蜜源植物id',
  `Pname` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜜源植物名称',
  `PType` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜜源植物种类',
  `start_month` int NOT NULL COMMENT '花期开始月份',
  `end_month` int NOT NULL COMMENT '花期结束月份',
  `Distance` int NOT NULL COMMENT '距蜂场距离（米）',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `p_end_month` CHECK (`end_month` between 1 and 12),
  CONSTRAINT `p_start_month` CHECK (`start_month` between 1 and 12)
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of nectorsources
-- ----------------------------
INSERT INTO `nectorsources` VALUES (1, '油菜花', '草本', 3, 4, 500);
INSERT INTO `nectorsources` VALUES (2, '荔枝', '乔木', 3, 5, 1200);
INSERT INTO `nectorsources` VALUES (3, '槐花', '乔木', 4, 5, 800);
INSERT INTO `nectorsources` VALUES (4, '椴树', '乔木', 6, 7, 2500);
INSERT INTO `nectorsources` VALUES (5, '荆条', '灌木', 6, 8, 1500);
INSERT INTO `nectorsources` VALUES (6, '向日葵', '草本', 7, 8, 600);
INSERT INTO `nectorsources` VALUES (7, '枣树', '乔木', 6, 7, 900);
INSERT INTO `nectorsources` VALUES (8, '荞麦', '草本', 8, 9, 3000);

-- ----------------------------
-- Table structure for salesorders
-- ----------------------------
DROP TABLE IF EXISTS `salesorders`;
CREATE TABLE `salesorders`  (
  `BFarm` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂场',
  `Bty` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂种',
  `Channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '销售渠道',
  `Orders` decimal(3, 1) NOT NULL COMMENT '订单（千克）',
  `Price` decimal(3, 1) NOT NULL COMMENT '价格（元/千克）',
  INDEX `BFarm`(`BFarm` ASC) USING BTREE,
  INDEX `Bty`(`Bty` ASC) USING BTREE,
  CONSTRAINT `salesorders_ibfk_1` FOREIGN KEY (`BFarm`) REFERENCES `apiaries` (`Fname`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `salesorders_ibfk_2` FOREIGN KEY (`Bty`) REFERENCES `beecolonies` (`BType`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `p_Orders` CHECK (`Orders` >= 0),
  CONSTRAINT `p_Price` CHECK (`Price` >= 0)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of salesorders
-- ----------------------------
INSERT INTO `salesorders` VALUES ('广东荔枝之乡采蜜点', '意大利蜂', '电商平台', 25.5, 55.0);
INSERT INTO `salesorders` VALUES ('广东荔枝之乡采蜜点', '中华蜜蜂', '高端礼盒', 8.0, 85.0);
INSERT INTO `salesorders` VALUES ('广东荔枝之乡采蜜点', '意大利蜂', '电商平台', 15.0, 52.0);
INSERT INTO `salesorders` VALUES ('黑龙江完达山野生蜂场', '东北黑蜂', '高端礼盒', 12.5, 92.0);
INSERT INTO `salesorders` VALUES ('黑龙江完达山野生蜂场', '东北黑蜂', '出口贸易', 60.0, 55.0);
INSERT INTO `salesorders` VALUES ('黑龙江完达山野生蜂场', '意大利蜂', '线下批发', 90.0, 48.0);
INSERT INTO `salesorders` VALUES ('黑龙江完达山野生蜂场', '东北黑蜂', '线下批发', 45.0, 50.0);
INSERT INTO `salesorders` VALUES ('陕西秦岭土蜂蜜基地', '意大利蜂', '超市零售', 35.0, 52.0);
INSERT INTO `salesorders` VALUES ('陕西秦岭土蜂蜜基地', '中华蜜蜂', '电商平台', 20.0, 60.0);
INSERT INTO `salesorders` VALUES ('陕西秦岭土蜂蜜基地', '意大利蜂', '超市零售', 40.0, 48.0);
INSERT INTO `salesorders` VALUES ('陕西秦岭土蜂蜜基地', '中华蜜蜂', '超市零售', 28.0, 55.0);
INSERT INTO `salesorders` VALUES ('青海门源油菜花海蜂场', '意大利蜂', '线下批发', 99.0, 32.0);
INSERT INTO `salesorders` VALUES ('青海门源油菜花海蜂场', '意大利蜂', '出口贸易', 95.0, 30.0);
INSERT INTO `salesorders` VALUES ('青海门源油菜花海蜂场', '中华蜜蜂', '线下批发', 80.0, 35.0);
INSERT INTO `salesorders` VALUES ('青海门源油菜花海蜂场', '意大利蜂', '出口贸易', 70.0, 31.0);
INSERT INTO `salesorders` VALUES ('四川若尔盖高原蜂场', '中华蜜蜂', '高端礼盒', 10.0, 78.0);
INSERT INTO `salesorders` VALUES ('四川若尔盖高原蜂场', '意大利蜂', '超市零售', 30.0, 45.0);
INSERT INTO `salesorders` VALUES ('四川若尔盖高原蜂场', '中华蜜蜂', '电商平台', 18.0, 50.0);
INSERT INTO `salesorders` VALUES ('四川若尔盖高原蜂场', '意大利蜂', '高端礼盒', 6.5, 82.0);
INSERT INTO `salesorders` VALUES ('四川若尔盖高原蜂场', '中华蜜蜂', '线下批发', 55.0, 40.0);

-- ----------------------------
-- Table structure for seasonrecords
-- ----------------------------
DROP TABLE IF EXISTS `seasonrecords`;
CREATE TABLE `seasonrecords`  (
  `BeeFarm` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂场',
  `BeType` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL COMMENT '蜂种',
  `Months` int NOT NULL COMMENT '月份',
  `Prod` decimal(4, 1) NOT NULL COMMENT '月产量',
  `Money` decimal(7, 2) NOT NULL COMMENT '月销售额',
  INDEX `BeeFarm`(`BeeFarm` ASC) USING BTREE,
  INDEX `BeType`(`BeType` ASC) USING BTREE,
  CONSTRAINT `seasonrecords_ibfk_1` FOREIGN KEY (`BeeFarm`) REFERENCES `apiaries` (`Fname`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `seasonrecords_ibfk_2` FOREIGN KEY (`BeType`) REFERENCES `beecolonies` (`BType`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `p_Money` CHECK (`Money` >= 0),
  CONSTRAINT `p_Months` CHECK ((`Months` > 0) and (`Months` < 13)),
  CONSTRAINT `p_Prod` CHECK (`Prod` >= 0)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seasonrecords
-- ----------------------------
INSERT INTO `seasonrecords` VALUES ('广东荔枝之乡采蜜点', '意大利蜂', 3, 120.5, 6627.50);
INSERT INTO `seasonrecords` VALUES ('广东荔枝之乡采蜜点', '中华蜜蜂', 3, 45.0, 3825.00);
INSERT INTO `seasonrecords` VALUES ('广东荔枝之乡采蜜点', '意大利蜂', 4, 280.0, 14000.00);
INSERT INTO `seasonrecords` VALUES ('广东荔枝之乡采蜜点', '中华蜜蜂', 4, 90.5, 7692.50);
INSERT INTO `seasonrecords` VALUES ('陕西秦岭土蜂蜜基地', '意大利蜂', 4, 150.0, 9000.00);
INSERT INTO `seasonrecords` VALUES ('陕西秦岭土蜂蜜基地', '中华蜜蜂', 5, 60.5, 5142.50);
INSERT INTO `seasonrecords` VALUES ('陕西秦岭土蜂蜜基地', '意大利蜂', 5, 320.0, 17600.00);
INSERT INTO `seasonrecords` VALUES ('黑龙江完达山野生蜂场', '东北黑蜂', 6, 450.0, 33750.00);
INSERT INTO `seasonrecords` VALUES ('黑龙江完达山野生蜂场', '意大利蜂', 6, 380.5, 22830.00);
INSERT INTO `seasonrecords` VALUES ('黑龙江完达山野生蜂场', '东北黑蜂', 7, 520.0, 39000.00);
INSERT INTO `seasonrecords` VALUES ('黑龙江完达山野生蜂场', '意大利蜂', 7, 410.0, 24600.00);
INSERT INTO `seasonrecords` VALUES ('青海门源油菜花海蜂场', '意大利蜂', 7, 650.0, 19500.00);
INSERT INTO `seasonrecords` VALUES ('青海门源油菜花海蜂场', '中华蜜蜂', 7, 200.5, 7017.50);
INSERT INTO `seasonrecords` VALUES ('青海门源油菜花海蜂场', '意大利蜂', 8, 580.0, 17400.00);
INSERT INTO `seasonrecords` VALUES ('陕西秦岭土蜂蜜基地', '意大利蜂', 8, 110.0, 5500.00);
INSERT INTO `seasonrecords` VALUES ('四川若尔盖高原蜂场', '中华蜜蜂', 9, 180.0, 12600.00);
INSERT INTO `seasonrecords` VALUES ('四川若尔盖高原蜂场', '意大利蜂', 9, 290.5, 17430.00);
INSERT INTO `seasonrecords` VALUES ('四川若尔盖高原蜂场', '中华蜜蜂', 10, 80.0, 5600.00);
INSERT INTO `seasonrecords` VALUES ('广东荔枝之乡采蜜点', '意大利蜂', 10, 30.0, 1500.00);
INSERT INTO `seasonrecords` VALUES ('黑龙江完达山野生蜂场', '东北黑蜂', 1, 5.0, 450.00);
INSERT INTO `seasonrecords` VALUES ('青海门源油菜花海蜂场', '意大利蜂', 12, 8.5, 255.00);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL,
  `passwords` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bg_0900_ai_ci NULL DEFAULT '蜂农' COMMENT '用户身份',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  CONSTRAINT `p_role` CHECK ((`role` = _utf8mb4'管理员') or (`role` = _utf8mb4'蜂农'))
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bg_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'manager', '123456', '管理员');
INSERT INTO `users` VALUES (2, 'farmer1', '111111', '蜂农');
INSERT INTO `users` VALUES (3, 'farmer2', '222222', '蜂农');
INSERT INTO `users` VALUES (6, 'farmer3', '333333', '蜂农');

SET FOREIGN_KEY_CHECKS = 1;
