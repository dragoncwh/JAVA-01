# 学习笔记


## 第12节课作业

2、(必做):基于电商交易场景(用户、商品、订单)，设计一套简单的表结构，提交DDL 的 SQL 文件到 Github(后面2周的作业依然要是用到这个表结构)。

> 路径: bean-config-w5

> XML: XMLConfig.java

> Annotation: AnnotationConfig.java

> AutowiredConfig: AutowiredConfig.java



### 用户表

```mysql
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`
(
	`user_id` INT(20) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(32) NOT NULL COMMENT '用户登录名',
  `nickname` VARCHAR(64) NOT NULL COMMENT '用户昵称',
  `avatar_url` VARCHAR(255) DEFAULT NULL,
  `email` VARCHAR(96) NOT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `password` VARCHAR(64) NOT NULL,
  `password_salt` VARCHAR(32) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT '1' COMMENT '账户状态: 0. inactive; 1. active; 2. suspend; ',
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NOT NULL,
  `last_access_time` TIMESTAMP NOT NULL,
  `last_access_ip` VARCHAR(45) NOT NULL,
  
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET = 'utf8mb4';
```



### 商品信息表

```mysql
DROP TABLE IF EXISTS `commodity_info`;
CREATE TABLE `commodity_info`
(
	`commodity_id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL COMMENT '商品名称',
  `description` VARCHAR(64) NOT NULL COMMENT '商品描述',
  `price` DECIMAL(10, 2) NOT NULL,
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NOT NULL,
  
  PRIMARY KEY (`commodity_id`)
) ENGINE = InnoDB DEFAULT CHARSET = 'utf8mb4';
```



### 订单信息表

```mysql
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`
(
  `order_id` BIGINT NOT NULL AUTO_INCREMENT,
  `shipping_id` BIGINT DEFAULT NULL,
  `payment_id` BIGINT DEFAULT NULL,
  `user_id` INT(20) NOT NULL,
  `order_status` TINYINT NOT NULL DEFAULT '1' COMMENT '订单状态: 0.取消; 1.未支付; 2.已支付; 3.等待发货; 4.已发货; 5.已签收; 6.退货申请; 7.退货完成',
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NOT NULL,
  
  PRIMARY KEY (`order_id`)
) ENGINE = InnoDB DEFAULT CHARSET = 'utf8mb4';

DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`
(
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL,
  `commodity_id` BIGINT NOT NULL,
  `commodity_unit_price` DECIMAL(10, 2) NOT NULL,
  `quantity` INT NOT NULL,
  `event_id` INT(20) DEFAULT NULL COMMENT '优惠或折扣信息',
  `user_id` INT(20) NOT NULL,
  `create_time` TIMESTAMP NOT NULL,
  `update_time` TIMESTAMP NOT NULL,
  
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = 'utf8mb4';
```

