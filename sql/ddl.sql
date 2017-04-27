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
  `job_name` int(11) DEFAULT NULL COMMENT '任务名称',
  `begin_time` timestamp NOT NULL COMMENT '开始时间',
  `end_time` timestamp NOT NULL COMMENT '结束世纪',
  `elapsed` bigint(20) NOT NULL COMMENT '执行耗时,单位:毫秒',
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_NAME` (`job_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='job执行信息表'