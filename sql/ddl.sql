DROP TABLE oc_warehouse_info;
CREATE TABLE `oc_warehouse_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `warehouse_id` int(11) DEFAULT NULL COMMENT '仓库ID',
  `warehouse_name` varchar(255) NOT NULL COMMENT '仓库名称',
  `pick_rate` int(11) DEFAULT NULL COMMENT '配货率,单位:百分比',
  `thed_time` int(11) DEFAULT NULL COMMENT '发货时效,单位:小时',
  `exec_job_id` int(11) DEFAULT NULL COMMENT '执行任务ID',
  `pick_date` int(2) DEFAULT NULL COMMENT 'Z1Z5(0) = 配货时间：周一至周五|Z1Z6(1)= 配货时间：周一至周六|Z1Z7(2)=配货时间：周一至周日',
  `udpate_warehouse_time` timestamp NULL DEFAULT NULL COMMENT '库存更新时间',
  `mark` varchar(3000) DEFAULT NULL COMMENT '货源说明',
  `retrun_desc` varchar(3000) DEFAULT NULL COMMENT '退货说明',
  `return_rate` int(11) DEFAULT NULL COMMENT '',
  `express_name` varchar(500) DEFAULT NULL COMMENT '支持快递',
  `end_t` varchar(255) DEFAULT NULL COMMENT '截单时间',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_ID` (`exec_job_id`),
  KEY `IDX_WAREHOUSE_ID` (`warehouse_id`),
  KEY `IDX_WAREHOUSE_NAME` (`warehouse_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='仓库信息';

DROP TABLE oc_job_exec_info;
CREATE TABLE `oc_job_exec_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `job_name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `begin_time` timestamp NOT NULL COMMENT '开始时间',
  `end_time` timestamp NULL COMMENT '结束世纪',
  `elapsed` bigint(20) NULL COMMENT '执行耗时,单位:毫秒',
  `status` varchar(255) NOT NULL COMMENT 'JOB执行状态 SUCCESS("SUCCESS"),FAILURE("FAILURE"),RUNNING("RUNNING")',
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_NAME` (`job_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job执行信息表';

DROP TABLE oc_inventory_info;
CREATE TABLE `oc_inventory_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `goodsNo` varchar(255) NULL COMMENT '商品货号',
  `warehouse_id` int(11) COMMENT '仓库ID',
  `warehouse_name` varchar(255)  NULL COMMENT '仓库名称',
  `size1` varchar(255)  COMMENT '中国尺码',
  `size2` varchar(255)  COMMENT '外国尺码',
  `brand` varchar(255)  COMMENT '品牌',
  `marketprice` decimal(25,10) COMMENT '市场价',
  `num2` int(11) COMMENT '库存数量',
  `division` varchar(255) COMMENT '类别',
  `cate` varchar(255) COMMENT '小类',
  `sex` varchar(255) COMMENT '性别',
  `quarter` varchar(255) COMMENT '季节',
  `discount` varchar(255) COMMENT '折扣',
  `pickRate` int(11) COMMENT '配货率',
  `warehouse_updateTime` timestamp COMMENT '库存更新时间',
  `pickDate` varchar(255) COMMENT '配货时间',
  `thedtime` varchar(255) COMMENT '发货时效,单位:小时',
  `proxyPrice` decimal(25,10) COMMENT '代理价',
  `purchasePrice` decimal(25,10) COMMENT '销售价',
  `salesCount` int(11) COMMENT '销售价',
  `expressName` varchar(255)  COMMENT '快递公司',
  `retrunDesc` varchar(2000)  COMMENT '-',
  `returnRate` int(11)  COMMENT '-',
  `endT` varchar(255) COMMENT '-',
  `mark` varchar(2000) COMMENT '-',
  `numIid` varchar(255) COMMENT '淘宝商品编码',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  `exec_job_id` int(11) DEFAULT NULL COMMENT '执行任务ID',
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_ID` (`exec_job_id`),
  KEY `IDX_GOODSNO` (`goodsNo`),
  KEY `IDX_NUMIID` (`numIid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='库存信息';

