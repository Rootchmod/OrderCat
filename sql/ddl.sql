DROP TABLE IF EXISTS oc_warehouse_info;
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

DROP TABLE IF EXISTS oc_job_exec_info;
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

ALTER TABLE oc_job_exec_info ADD COLUMN `error_message` TEXT AFTER `status`;




DROP TABLE IF EXISTS oc_sales_info;
CREATE TABLE `oc_sales_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `numIid` varchar(255) COMMENT '淘宝商品编码',
  `sales_count` int(11) DEFAULT NULL COMMENT '销量',
  `exec_job_id` int(11) DEFAULT NULL COMMENT '执行任务ID',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_ID` (`exec_job_id`),
  KEY `IDX_NUM_IID` (`numIid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='销量信息';



DROP TABLE IF EXISTS oc_logistics_companies_info;
CREATE TABLE `oc_logistics_companies_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `lc_id` BIGINT DEFAULT NULL COMMENT '物流公司标识',
  `lc_code` varchar(255) COMMENT '物流公司代码',
  `lc_name` varchar(255) COMMENT '物流公司简称',
  `lc_reg_mail_no` varchar(255) COMMENT '运单号验证正则表达式',
  `lc_is_enable` TINYINT COMMENT '是否启用:1/0',
  `exec_job_id` BIGINT DEFAULT NULL COMMENT '执行任务ID',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  KEY `IDX_JOB_ID` (`exec_job_id`),
  KEY `IDX_LC_ID` (`lc_id`),
  KEY `IDX_LC_IS_ENABLE` (`lc_is_enable`),
  KEY `IDX_LC_CODE` (`lc_code`),
  KEY `IDX_LC_NAME` (`lc_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物流公司信息';



DROP TABLE IF EXISTS oc_inventory_info;
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
  `salesPrice` decimal(25,10) COMMENT '销售价',
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



DROP TABLE IF EXISTS oc_sync_inventory_item_info;
CREATE TABLE `oc_sync_inventory_item_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `numIid` varchar(255) COMMENT '淘宝商品编码',
  `status` varchar(255) NOT NULL COMMENT '宝贝同步状态 ARE_SYNCHRONIZED("ARE_SYNCHRONIZED"),NOT_SYNCHRONIZED("NOT_SYNCHRONIZED")',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  KEY `IDX_NUMIID` (`numIid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='同步宝贝信息';


DROP TABLE IF EXISTS oc_fenxiao_check_result;
CREATE TABLE `oc_fenxiao_check_result` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `tid` BIGINT DEFAULT NULL COMMENT '订单ID',
  `order_status` varchar(255)  COMMENT '订单状态:TRADE_NO_CREATE_PAY(没有创建支付宝交易) WAIT_BUYER_PAY(等待买家付款) WAIT_SELLER_SEND_GOODS(等待卖家发货,即:买家已付款) WAIT_BUYER_CONFIRM_GOODS(等待买家确认收货,即:卖家已发货) TRADE_BUYER_SIGNED(买家已签收,货到付款专用) TRADE_FINISHED(交易成功) TRADE_CLOSED(交易关闭) TRADE_CLOSED_BY_TAOBAO(交易被淘宝关闭) ALL_WAIT_PAY(包含：WAIT_BUYER_PAY、TRADE_NO_CREATE_PAY) ALL_CLOSED(包含：TRADE_CLOSED、TRADE_CLOSED_BY_TAOBAO)',
  `refundId` BIGINT DEFAULT NULL COMMENT '退款ID',
  `numIid` BIGINT DEFAULT NULL COMMENT '宝贝ID',
  `title` varchar(1000) NOT NULL COMMENT '宝贝标题',
  `fenxiaoId` BIGINT COMMENT '分销ID',
  `supplier_nick` varchar(255) COMMENT '供应商nick',
  `distributor_nick` varchar(255) COMMENT '分销商昵称',
  `fenxiao_refund_status` varchar(255) COMMENT '分销退款状态 1：买家已经申请退款，等待卖家同意 2：卖家已经同意退款，等待买家退货 3：买家已经退货，等待卖家确认收货 4：退款关闭 5：退款成功 6：卖家拒绝退款 12：同意退款，待打款 9：没有申请退款 10：卖家拒绝确认收货',
  `fenxiao_refund_fee` decimal(25,10) COMMENT '分销退款的金额',
  `fenxiao_pay_sup_fee` decimal(25,10) COMMENT '分销-支付给供应商的金额',
  `fenxiao_refund_desc` varchar(255)  COMMENT '分销-退款原因',
  `fenxiao_refund_reason` varchar(255) COMMENT '分销-退款说明',
  `status` varchar(255)  COMMENT '对账状态 NOT_FENXIAO("NOT_FENXIAO"),NOT_FENXIAO_REFUND("NOT_FENXIAO_REFUND"),NOT_FENXIAO("NOT_FENXIAO"),SUCCESS_REFUND("SUCCESS_REFUND")',
  `remarks` TEXT COMMENT '备注(json格式)',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  KEY `IDX_NUMIID` (`numIid`),
  KEY `IDX_REFUNDID` (`refundId`),
  KEY `IDX_TID` (`tid`),
  KEY `IDX_FENXIAOID` (`fenxiaoId`),
  KEY `IDX_STATUS` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分销对账结果表';


DROP TABLE IF EXISTS oc_tianma_check_result;

DROP TABLE IF EXISTS oc_tmsport_check_result;
CREATE TABLE `oc_tmsport_check_result` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `tm_order_ids` varchar(255) NULL COMMENT '天马订单ID',
  `tm_outer_order_id` varchar(255) NULL COMMENT '天马外部订单编码',
  `tm_order_num` BIGINT COMMENT '天马订单数量',
  `tm_num` BIGINT COMMENT '天马购买数量',
  `tb_order_num` BIGINT COMMENT '淘宝订单数量',
  `tb_num` BIGINT COMMENT '淘宝购买数量',
  `tb_created` DATETIME DEFAULT NULL COMMENT '淘宝订单时间',
  `tb_paytime` DATETIME DEFAULT NULL COMMENT '淘宝订单支付时间',
  `tb_price` decimal(25,10)  COMMENT '商品价格。精确到2位小数;单位:元。如:200.07，表示:200元7分',
  `tb_payment` decimal(25,10)  COMMENT '子订单实付金额。精确到2位小数，单位:元。如:200.07，表示:200元7分。对于多子订单的交易，计算公式如下：payment = price * num + adjust_fee - discount_fee ；单子订单交易，payment与主订单的payment一致，对于退款成功的子订单，由于主订单的优惠分摊金额，会造成该字段可能不为0.00元。建议使用退款前的实付金额减去退款单中的实际退款金额计算。',
  `tb_discountFee` decimal(25,10)  COMMENT '子订单级订单优惠金额。精确到2位小数;单位:元。如:200.07，表示:200元7分',
  `tb_totalFee` decimal(25,10)  COMMENT '应付金额（商品价格 * 商品数量 + 手工调整金额 - 子订单级订单优惠金额）。精确到2位小数;单位:元。如:200.07，表示:200元7分',
  `dz_status` varchar(255)  COMMENT '对账状态',
  `dz_details_message` TEXT COMMENT '对账详细描述',
  `remarks` TEXT COMMENT '备注(json格式)',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  KEY `IDX_TM_OUTER_ORDER_ID` (`tm_outer_order_id`),
  KEY `IDX_DZ_STATUS` (`dz_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='天马体育对账结果表';


DROP TABLE IF EXISTS oc_tm_order_records;
CREATE TABLE `oc_tm_order_records` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `tid` varchar(255) NULL COMMENT '淘宝订单ID',
  `type` varchar(255)  COMMENT '下单类型：手工补单，自动下单',
  `status` varchar(255)  COMMENT '下单状态：成功或失败',
  `order_info` TEXT COMMENT '订单信息-json',
  `fail_cause` TEXT COMMENT '失败原因',
  `wh_snapshot_data` TEXT COMMENT '仓库快照数据(自动机器下单时，才会有数据)',
  `machine_cid` TEXT COMMENT '下单机器CID(自动机器下单时，才会有数据)',
  `add_time` timestamp NOT NULL COMMENT '下单时间',
  PRIMARY KEY (`id`),
  KEY `IDX_TID` (`tid`),
  KEY `IDX_TYPE` (`type`),
  KEY `IDX_STATUS` (`status`),
  KEY `IDX_ADD_TIME` (`add_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='下单记录表';
