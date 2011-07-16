/*
Navicat PGSQL Data Transfer

Source Server         : localhost PostgreSQL
Source Server Version : 80408
Source Host           : localhost:5432
Source Database       : ord_erp_dev
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 80408
File Encoding         : 65001

Date: 2011-07-16 21:43:15
*/


-- ----------------------------
-- Table structure for "public"."platform_instance_parameters"
-- ----------------------------
DROP TABLE "public"."platform_instance_parameters";
CREATE TABLE "public"."platform_instance_parameters" (
"id" int8 NOT NULL,
"platform_instance_id" int8 NOT NULL,
"parameter_name" varchar(128) NOT NULL,
"parameter_value" varchar(128)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of platform_instance_parameters
-- ----------------------------
INSERT INTO "public"."platform_instance_parameters" VALUES ('1', '1', 'api_key', 'R1R6NC8GB3CPXDPNFZ87N7T8LQ30SRH1');
INSERT INTO "public"."platform_instance_parameters" VALUES ('2', '1', 'api_url', 'http://test.psychicbazaar.com/api');
INSERT INTO "public"."platform_instance_parameters" VALUES ('3', '2', 'access_key_id', 'AKIAIDNIJIVSRTI5ATLQ');
INSERT INTO "public"."platform_instance_parameters" VALUES ('4', '2', 'secret_access_key', '9uldEMo0CVnrFkHH52ic');
INSERT INTO "public"."platform_instance_parameters" VALUES ('5', '2', 'merchant_id', 'A29MOI9NSKCQ2V');
INSERT INTO "public"."platform_instance_parameters" VALUES ('6', '2', 'marketplace_id', 'A1F83G8C2ARO7P');
INSERT INTO "public"."platform_instance_parameters" VALUES ('7', '2', 'marketplace_locale', 'GB');

-- ----------------------------
-- Table structure for "public"."platform_instances"
-- ----------------------------
DROP TABLE "public"."platform_instances";
CREATE TABLE "public"."platform_instances" (
"id" int8 NOT NULL,
"platform_instance_name" varchar(256) NOT NULL,
"platform_id" int8 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of platform_instances
-- ----------------------------
INSERT INTO "public"."platform_instances" VALUES ('1', 'Psychic Bazaar PrestaShop', '1');
INSERT INTO "public"."platform_instances" VALUES ('2', 'Psychic Bazaar Amazon Marketplace UK', '2');

-- ----------------------------
-- Table structure for "public"."platforms"
-- ----------------------------
DROP TABLE "public"."platforms";
CREATE TABLE "public"."platforms" (
"id" int8 NOT NULL,
"platform_name" varchar(256) NOT NULL,
"platform_connector" varchar(256) NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of platforms
-- ----------------------------
INSERT INTO "public"."platforms" VALUES ('1', 'PrestaShop', 'PrestaShopConnector');
INSERT INTO "public"."platforms" VALUES ('2', 'Amazon Marketplace', 'AmazonMarketplaceConnector');

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table "public"."platform_instance_parameters"
-- ----------------------------
ALTER TABLE "public"."platform_instance_parameters" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."platform_instances"
-- ----------------------------
ALTER TABLE "public"."platform_instances" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table "public"."platforms"
-- ----------------------------
ALTER TABLE "public"."platforms" ADD PRIMARY KEY ("id");
