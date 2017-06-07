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

ALTER TABLE oc_job_exec_info ADD COLUMN `error_message` TEXT AFTER `status`;




DROP TABLE oc_sales_info;
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



DROP TABLE oc_logistics_companies_info;
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



DROP TABLE oc_sync_inventory_item_info;
CREATE TABLE `oc_sync_inventory_item_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `numIid` varchar(255) COMMENT '淘宝商品编码',
  `status` varchar(255) NOT NULL COMMENT '宝贝同步状态 ARE_SYNCHRONIZED("ARE_SYNCHRONIZED"),NOT_SYNCHRONIZED("NOT_SYNCHRONIZED")',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  KEY `IDX_NUMIID` (`numIid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='同步宝贝信息';


DROP TABLE oc_fenxiao_check_result;
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


DROP TABLE oc_tianma_check_result;
CREATE TABLE `oc_tianma_check_result` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `tb_order_id` BIGINT DEFAULT NULL COMMENT '淘宝订单ID',
  `tb_order_status` varchar(255)  COMMENT '淘宝订单状态',
  `tb_numIid` BIGINT DEFAULT NULL COMMENT '淘宝宝贝ID',
  `tb_title` varchar(1000) COMMENT '淘宝宝贝标题',
  `tb_payment` decimal(25,10) COMMENT '淘宝支付价格',
  `tb_refundID` BIGINT COMMENT '淘宝退款单ID',
  `tb_refundStatus` varchar(255) COMMENT '淘宝退款状态',
  `tb_num` BIGINT COMMENT '淘宝购买宝贝数量',
  `tm_order_id` BIGINT DEFAULT NULL COMMENT '天马订单ID',
  `tm_outer_order_id` varchar(255) NULL COMMENT '天马外部订单编码',
  `tm_goods_no` varchar(255)  COMMENT '天马货号',
  `tm_order_status` varchar(255)  COMMENT '天马订单状态',
  `tm_delivery_no` varchar(255)  COMMENT '天马快递单号',
  `tm_delivery_name` varchar(255)  COMMENT '天马快递名称',
  `tm_warehouse_id` INTEGER(10)  COMMENT '天马仓库ID',
  `tm_buyer_name` varchar(255)  COMMENT '天马买家名称',
  `tm_warehouse_name` varchar(255)  COMMENT '天马仓库名称',
  `tm_payPrice` decimal(25,10)  COMMENT '天马支付价格',
  `tm_postFee` decimal(25,10)  COMMENT '天马运费',
  `tm_noshipment_Remark` varchar(255)  COMMENT '天马订单备注',
  `size1` varchar(255)  COMMENT '中国码',
  `size2` varchar(255)  COMMENT '国外码',
  `dz_status` varchar(255)  COMMENT '对账状态',
  `dz_details_message` TEXT COMMENT '对账详细描述',
  `remarks` TEXT COMMENT '备注(json格式)',
  `add_time` timestamp NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  KEY `IDX_TM_ORDER_ID` (`tm_order_id`),
  KEY `IDX_TB_ORDER_ID` (`tb_order_id`),
  KEY `IDX_DZ_STATUS` (`dz_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='天马对账结果表';


