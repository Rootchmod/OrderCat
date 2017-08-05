package com.myjo.ordercat;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.aol.micro.server.MicroserverApp;
import com.aol.micro.server.config.Microserver;
import com.aol.micro.server.module.Module;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.mashape.unirest.http.Unirest;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.context.OrderCatContext;
import com.myjo.ordercat.domain.JobName;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.handle.*;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.job.*;
import com.myjo.ordercat.spm.OrdercatApplication;
import com.myjo.ordercat.spm.OrdercatApplicationBuilder;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_fenxiao_check_result.OcFenxiaoCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_logistics_companies_info.OcLogisticsCompaniesInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sales_info.OcSalesInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_sync_inventory_item_info.OcSyncInventoryItemInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tm_order_records.OcTmOrderRecordsManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_tmsport_check_result.OcTmsportCheckResultManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager;
import com.myjo.ordercat.utils.OcEncryptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * Created by lee5hx on 17/4/19.
 */
@Microserver(properties={"cors.simple","false"})
public class Main {

    private static final Logger Logger = LogManager.getLogger(Main.class);

    @Parameter(names = {"--config", "-c"})
    private String config;
    @Parameter(names = {"--action", "-a"})
    private String action;
    @Parameter(names = {"--cid", "-cid"})
    private String consumerId;


    public static void main(String args[]) throws Exception {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    public void run() throws Exception {

        OrderCatConfig.init(config);

        OrdercatApplication app = new OrdercatApplicationBuilder()
                .withConnectionUrl(OrderCatConfig.getDBmsName(), OrderCatConfig.getDBConnectionUrl())
                .withUsername(OrderCatConfig.getDBUsername())
                .withPassword(OcEncryptionUtils.base64Decoder(OrderCatConfig.getDBPassword(),5))
                //.withLogging(ApplicationBuilder.LogType.CONNECTION)
                //.withParam("connectionpool.maxAge", "8000")
                //.withParam("connectionpool.maxRetainSize", "20")
                .build();



        //设置超时时间
        Unirest.setTimeouts(300 * 1000, 300 * 1000);


        //speedment
        OcWarehouseInfoManager ocWarehouseInfoManager = app.getOrThrow(OcWarehouseInfoManager.class);
        OcJobExecInfoManager ocJobExecInfoManager = app.getOrThrow(OcJobExecInfoManager.class);
        //OcInventoryInfoManager ocInventoryInfoManager = app.getOrThrow(OcInventoryInfoManager.class);
        OcSalesInfoManager ocSalesInfoManager = app.getOrThrow(OcSalesInfoManager.class);
        OcLogisticsCompaniesInfoManager ocLogisticsCompaniesInfoManager = app.getOrThrow(OcLogisticsCompaniesInfoManager.class);
        OcSyncInventoryItemInfoManager ocSyncInventoryItemInfoManager = app.getOrThrow(OcSyncInventoryItemInfoManager.class);
        OcFenxiaoCheckResultManager ocFenxiaoCheckResultManager = app.getOrThrow(OcFenxiaoCheckResultManager.class);
        OcTmsportCheckResultManager ocTmsportCheckResultManager = app.getOrThrow(OcTmsportCheckResultManager.class);
        OcTmOrderRecordsManager ocTmOrderRecordsManager = app.getOrThrow(OcTmOrderRecordsManager.class);


        OrderCatContext.setOcFenxiaoCheckResultManager(ocFenxiaoCheckResultManager);
        OrderCatContext.setOcTmsportCheckResultManager(ocTmsportCheckResultManager);
        OrderCatContext.setOcJobExecInfoManager(ocJobExecInfoManager);
        OrderCatContext.setOcTmOrderRecordsManager(ocTmOrderRecordsManager);
        OrderCatContext.setOcWarehouseInfoManager(ocWarehouseInfoManager);


        Logger.info("初始化[speedment]-完成.");


        //脚本引擎
        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");


        Logger.info("初始化[nashorn]-js脚本引擎完成.");


        Map<String, String> map = new HashMap<>();
        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();

        Logger.info("初始化[TianmaSportHttp,TaoBaoHttp]-完成.");


        SyncInventory syncInventory = new SyncInventory(tianmaSportHttp, taoBaoHttp, e);
        syncInventory.setOcWarehouseInfoManager(ocWarehouseInfoManager);
        syncInventory.setOcJobExecInfoManager(ocJobExecInfoManager);
        syncInventory.setOcSalesInfoManager(ocSalesInfoManager);
        syncInventory.setOcSyncInventoryItemInfoManager(ocSyncInventoryItemInfoManager);


        SendGoods sendGoods = new SendGoods(tianmaSportHttp, taoBaoHttp);
        sendGoods.setOcLogisticsCompaniesInfoManager(ocLogisticsCompaniesInfoManager);
        //sendGoods.setOcJobExecInfoManager(ocJobExecInfoManager);


        AccountCheck ac = new AccountCheck(tianmaSportHttp, taoBaoHttp);
        //ac.setOcSyncInventoryItemInfoManager(ocSyncInventoryItemInfoManager);
        //ac.setOcFenxiaoCheckResultManager(ocFenxiaoCheckResultManager);
        ac.setOcTmsportCheckResultManager(ocTmsportCheckResultManager);


        OrderOperate orderOperate = new OrderOperate(tianmaSportHttp,taoBaoHttp,e);
        orderOperate.setOcTmOrderRecordsManager(ocTmOrderRecordsManager);
        OrderCatContext.setOrderOperate(orderOperate);

        tianmaSportHttp.getVerifyCodeImage();

        System.out.print("请输入验证码:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        JSONObject jsonObject = tianmaSportHttp.login(br.readLine());

        if (!jsonObject.getBoolean("success")) {
            throw new OCException("天马平台-登陆失败");
        }

        tianmaSportHttp.main_html();

        ExecuteHandle eh;
        eh = new SyncWarehouseHandle(syncInventory);
        eh.setJobName(JobName.SYNC_WAREHOUSE_JOB.getValue());
        eh.setOcJobExecInfoManager(ocJobExecInfoManager);

        ExecuteHandle eh1;
        eh1 = new SyncTaoBaoInventoryHandle(syncInventory);
        eh1.setJobName(JobName.SYNC_TAOBAO_INVENTORY_JOB.getValue());
        eh1.setOcJobExecInfoManager(ocJobExecInfoManager);

        ExecuteHandle eh2;
        eh2 = new SyncSalesInfoHandle(syncInventory);
        eh2.setJobName(JobName.SYNC_SALES_INFO_JOB.getValue());
        eh2.setOcJobExecInfoManager(ocJobExecInfoManager);

        ExecuteHandle eh3;
        eh3 = new AsRefundAcHandle(ac);
        eh3.setJobName(JobName.AS_REFUND_ACCOUNT_CHECK_JOB.getValue());
        eh3.setOcJobExecInfoManager(ocJobExecInfoManager);


        ExecuteHandle eh4;
        eh4 = new AutoSendHandle(sendGoods);
        eh4.setJobName(JobName.AUTO_SEND_GOODS_JOB.getValue());
        eh4.setOcJobExecInfoManager(ocJobExecInfoManager);


        ExecuteHandle eh5;
        eh5 = new TianMaAcHandle(ac);
        eh5.setJobName(JobName.TIANMA_ACCOUNT_CHECK_JOB.getValue());
        eh5.setOcJobExecInfoManager(ocJobExecInfoManager);



        if (action.equals(JobName.SYNC_SALES_INFO_JOB.getValue())) {
            eh2.exec();
        } else if (action.equals(JobName.SYNC_WAREHOUSE_JOB.getValue())) {
            eh.exec();
        } else if (action.equals(JobName.AS_REFUND_ACCOUNT_CHECK_JOB.getValue())) {
            eh3.exec();
        } else if (action.equals(JobName.SYNC_TAOBAO_INVENTORY_JOB.getValue())) {
            eh1.exec();
        } else if (action.equals(JobName.AUTO_SEND_GOODS_JOB.getValue())) {
            eh4.exec();
        }else if (action.equals(JobName.TIANMA_ACCOUNT_CHECK_JOB.getValue())) {
            eh5.exec();
        }
        else if(action.equals("morder")){
//            OrderOperate orderOperate = new OrderOperate(tianmaSportHttp,taoBaoHttp);
//            orderOperate.manualOrder(28219168266790387l,"182",null,null);
        }
        else if (action.equals("order_robot")) {
//            TaobaoClient client = new DefaultTaobaoClient(OrderCatConfig.getTaobaoApiUrl(), OrderCatConfig.getTaobaoApiAppKey(), OrderCatConfig.getTaobaoApiAppSecret());
//            JushitaJmsUserAddRequest req = new JushitaJmsUserAddRequest();
//            req.setTopicNames("taobao_trade_TradeBuyerPay");
//            JushitaJmsUserAddResponse rsp = client.execute(req, OrderCatConfig.getTaobaoApiSessionKey());
//            Logger.info(rsp.getBody());
//            Properties properties = new Properties();
//            properties.put(PropertyKeyConst.ConsumerId, consumerId);
//            properties.put(PropertyKeyConst.AccessKey,  OrderCatConfig.getTaobaoApiAppKey());
//            properties.put(PropertyKeyConst.SecretKey,  OrderCatConfig.getTaobaoApiAppSecret());
//            Consumer consumer = ONSFactory.createConsumer(properties);
//            consumer.subscribe("rmq_sys_jst_23279400", "*", (Message message, ConsumeContext context) -> {
//                Logger.info("Receive: " + message);
//
//
//                String msg_body=null;
//                try {
//                    msg_body = new String(message.getBody(),"UTF-8");
//                } catch (UnsupportedEncodingException e1) {
//                    e1.printStackTrace();
//                }
//                com.alibaba.fastjson.JSONObject object = JSON.parseObject(msg_body);
//
//                long tid = object.getLongValue("tid");
//
//
//                Logger.info("tid: " + tid);
//
//                return Action.CommitMessage;
//            });
//            consumer.start();



            Properties properties = new Properties();
            properties.put(PropertyKeyConst.ConsumerId, "CID_MJ_MT1");
            properties.put(PropertyKeyConst.AccessKey, OrderCatConfig.getTaobaoApiAppKey());
            properties.put(PropertyKeyConst.SecretKey, OrderCatConfig.getTaobaoApiAppSecret());
            Consumer consumer = ONSFactory.createConsumer(properties);
            consumer.subscribe("rmq_sys_jst_23279400", "*", new MessageListener() {
                public Action consume(Message message, ConsumeContext context) {
                    System.out.println("Receive: " + message);

                    String msg_body=null;
                    try {
                        msg_body = new String(message.getBody(), "UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    com.alibaba.fastjson.JSONObject object = JSON.parseObject(msg_body);

                    com.alibaba.fastjson.JSONObject contentObject = object.getJSONObject("content");

                    long tid = contentObject.getLongValue("tid");

                    orderOperate.autoOrder(tid,"CID_MJ_MT1");

                    Logger.info(String.format("Order TID:%d",tid));
                    return Action.CommitMessage;
                }
            });
            consumer.start();
            System.out.println("Consumer Started");

            Logger.info(String.format("Consumer-[%s] Started",consumerId));
        }
        else if (action.equals("order_robot_clean")) {
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.ConsumerId, "CID_MJ_MT1");
            properties.put(PropertyKeyConst.AccessKey, OrderCatConfig.getTaobaoApiAppKey());
            properties.put(PropertyKeyConst.SecretKey, OrderCatConfig.getTaobaoApiAppSecret());
            Consumer consumer = ONSFactory.createConsumer(properties);
            consumer.subscribe("rmq_sys_jst_23279400", "*", new MessageListener() {
                public Action consume(Message message, ConsumeContext context) {
                    System.out.println("Receive: " + message);

                    String msg_body=null;
                    try {
                        msg_body = new String(message.getBody(), "UTF-8");
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    com.alibaba.fastjson.JSONObject object = JSON.parseObject(msg_body);

                    com.alibaba.fastjson.JSONObject contentObject = object.getJSONObject("content");

                    long tid = contentObject.getLongValue("tid");



                    Logger.info(String.format("Order TID:%d",tid));
                    return Action.CommitMessage;
                }
            });
            consumer.start();
            System.out.println("Consumer Started");

            Logger.info(String.format("Consumer-[%s] Started",consumerId));
        }




        else if (action.equals("start")) {


            //new MicroserverApp(()->"order_cat_api").start();

            MicroserverApp server = new MicroserverApp(this.getClass(),new Module() {
                @Override
                public String getContext() {
                    return "order_cat_api";
                }
            });
            server.start();

            Logger.info("MicroserverApp 启动成功!");

            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            //SyncWarehouseJob
            JobDataMap map1 = new JobDataMap();
            map1.put("SyncWarehouseHandle", eh);
            map1.put("SyncTaoBaoInventoryHandle", eh1);
            map1.put("SyncSalesInfoHandle", eh2);
            map1.put("FenXiaoAcHandle", eh3);
            map1.put("AutoSendHandle", eh4);
            map1.put("TianmaAcHandle", eh5);
            map1.put("TianmaSportHttp",tianmaSportHttp);

            JobDetail job = newJob(SyncWarehouseJob.class)
                    .usingJobData(map1)
                    .withIdentity(JobName.SYNC_WAREHOUSE_JOB.getValue(), "myjo")
                    .build();
            CronTrigger trigger = newTrigger()
                    .withIdentity(JobName.SYNC_WAREHOUSE_JOB.getValue() + "Trigger", "myjo")
                    .withSchedule(cronSchedule(OrderCatConfig.getSyncWarehouseJobTriggerCron()))
                    .build();
            sched.scheduleJob(job, trigger);

            //SyncTaoBaoInventoryJob
            JobDetail job1 = newJob(SyncTaoBaoInventoryJob.class)
                    .usingJobData(map1)
                    .withIdentity(JobName.SYNC_TAOBAO_INVENTORY_JOB.getValue(), "myjo")
                    .build();

            CronTrigger trigger1 = newTrigger()
                    .withIdentity(JobName.SYNC_TAOBAO_INVENTORY_JOB.getValue() + "Trigger", "myjo")
                    .withSchedule(cronSchedule(OrderCatConfig.getSyncTaoBaoInventoryJobTriggerCron()))
                    .build();
            sched.scheduleJob(job1, trigger1);


            //SyncSalesInfoJob
            JobDetail job2 = newJob(SyncSalesInfoJob.class)
                    .usingJobData(map1)
                    .withIdentity(JobName.SYNC_SALES_INFO_JOB.getValue(), "myjo")
                    .build();

            CronTrigger trigger2 = newTrigger()
                    .withIdentity(JobName.SYNC_SALES_INFO_JOB.getValue() + "Trigger", "myjo")
                    .withSchedule(cronSchedule(OrderCatConfig.getSyncSalesInfoJobTriggerCron()))
                    .build();
            sched.scheduleJob(job2, trigger2);

            //FenxiaoAccountCheckJob
//            JobDetail job3 = newJob(FenxiaoAccountCheckJob.class)
//                    .usingJobData(map1)
//                    .withIdentity(JobName.FENXIAO_ACCOUNT_CHECK_JOB.getValue(), "myjo")
//                    .build();
//
//            CronTrigger trigger3 = newTrigger()
//                    .withIdentity(JobName.FENXIAO_ACCOUNT_CHECK_JOB.getValue() + "Trigger", "myjo")
//                    .withSchedule(cronSchedule(OrderCatConfig.getFenxiaoAccountCheckJobTriggerCron()))
//                    .build();
//            sched.scheduleJob(job3, trigger3);

            //AutoSendGoodsJob
            JobDetail job4 = newJob(AutoSendGoodsJob.class)
                    .usingJobData(map1)
                    .withIdentity(JobName.AUTO_SEND_GOODS_JOB.getValue(), "myjo")
                    .build();

            CronTrigger trigger4 = newTrigger()
                    .withIdentity(JobName.AUTO_SEND_GOODS_JOB.getValue() + "Trigger", "myjo")
                    .withSchedule(cronSchedule(OrderCatConfig.getAutoSendGoodsJobTriggerCron()))
                    .build();
            sched.scheduleJob(job4, trigger4);




            //GuessMailNoKeepJob
            JobDetail job5 = newJob(GuessMailNoKeepJob.class)
                    .usingJobData(map1)
                    .withIdentity("GuessMailNoKeepJob", "myjo")
                    .build();

            CronTrigger trigger5 = newTrigger()
                    .withIdentity("GuessMailNoKeepJob" + "Trigger", "myjo")
                    .withSchedule(cronSchedule(OrderCatConfig.getGuessMailNoKeepJobTriggerCron()))
                    .build();
            sched.scheduleJob(job5, trigger5);



            //TianmaAccountCheckJob
            JobDetail job6 = newJob(TianmaAccountCheckJob.class)
                    .usingJobData(map1)
                    .withIdentity(JobName.TIANMA_ACCOUNT_CHECK_JOB.getValue(), "myjo")
                    .build();

            CronTrigger trigger6 = newTrigger()
                    .withIdentity(JobName.TIANMA_ACCOUNT_CHECK_JOB.getValue() + "Trigger", "myjo")
                    .withSchedule(cronSchedule(OrderCatConfig.getTianmaAccountCheckJobTriggerCron()))
                    .build();
            sched.scheduleJob(job6, trigger6);

            sched.start();


            Logger.info(String.format("启动-MicroserverApp[%s].","order_cat_api"));

        }


    }


}
