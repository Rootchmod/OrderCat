package com.myjo.ordercat;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.mashape.unirest.http.Unirest;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.exception.OCException;
import com.myjo.ordercat.handle.ExecuteHandle;
import com.myjo.ordercat.handle.SyncInventory;
import com.myjo.ordercat.handle.SyncTaoBaoInventoryHandle;
import com.myjo.ordercat.handle.SyncWarehouseHandle;
import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import com.myjo.ordercat.job.SyncTaoBaoInventoryJob;
import com.myjo.ordercat.job.SyncWarehouseJob;
import com.myjo.ordercat.spm.OrdercatApplication;
import com.myjo.ordercat.spm.OrdercatApplicationBuilder;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_inventory_info.OcInventoryInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_job_exec_info.OcJobExecInfoManager;
import com.myjo.ordercat.spm.ordercat.ordercat.oc_warehouse_info.OcWarehouseInfoManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * Created by lee5hx on 17/4/19.
 */
public class Main {

    private static final Logger Logger = LogManager.getLogger(Main.class);

    @Parameter(names = {"--config", "-c"})
    private String config;
    @Parameter(names = {"--action", "-a"})
    private String action;

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
                .withPassword(OrderCatConfig.getDBPassword())
                .build();

        //设置超时时间
        Unirest.setTimeouts(300*1000,300*1000);


        OcWarehouseInfoManager ocWarehouseInfoManager = app.getOrThrow(OcWarehouseInfoManager.class);
        OcJobExecInfoManager ocJobExecInfoManager = app.getOrThrow(OcJobExecInfoManager.class);
        OcInventoryInfoManager ocInventoryInfoManager = app.getOrThrow(OcInventoryInfoManager.class);

        Map<String, String> map = new HashMap<>();

        TianmaSportHttp tianmaSportHttp = new TianmaSportHttp(map);
        TaoBaoHttp taoBaoHttp = new TaoBaoHttp();
        SyncInventory syncInventory = new SyncInventory(tianmaSportHttp, taoBaoHttp);
        syncInventory.setOcInventoryInfoManager(ocInventoryInfoManager);
        syncInventory.setOcWarehouseInfoManager(ocWarehouseInfoManager);
        syncInventory.setOcJobExecInfoManager(ocJobExecInfoManager);


        tianmaSportHttp.getVerifyCodeImage();

        System.out.print("请输入验证码:");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        JSONObject jsonObject = tianmaSportHttp.login(br.readLine());

        if(!jsonObject.getBoolean("success")){
            throw new OCException("天马平台-登陆失败");
        }

        tianmaSportHttp.main_html();


        ExecuteHandle eh;
        eh = new SyncWarehouseHandle(syncInventory);
        eh.setJobName("SyncWarehouseJob");
        eh.setOcJobExecInfoManager(ocJobExecInfoManager);

        ExecuteHandle eh1;
        eh1 = new SyncTaoBaoInventoryHandle(syncInventory);
        eh1.setJobName("SyncTaoBaoInventory");
        eh1.setOcJobExecInfoManager(ocJobExecInfoManager);


        if (action.equals("SyncSalesInfoJob")) {

        } else if (action.equals("SyncWarehouseJob")) {
            eh.exec();
        } else if (action.equals("SyncTaoBaoInventory")) {
            eh1.exec();
        } else if (action.equals("JobStart")) {
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();

            //SyncWarehouseJob
            JobDataMap map1 = new JobDataMap();
            map1.put("SyncWarehouseHandle",eh);
            map1.put("SyncTaoBaoInventoryHandle",eh1);
            JobDetail job = newJob(SyncWarehouseJob.class)
                    .usingJobData(map1)
                    .withIdentity("SyncWarehouseJob", "myjo")
                    .build();
            CronTrigger trigger = newTrigger()
                    .withIdentity("SyncWarehouseJobTrigger", "myjo")
                    .withSchedule(cronSchedule(OrderCatConfig.getSyncWarehouseJobTriggerCron()))
                    .build();
            sched.scheduleJob(job, trigger);


            //SyncTaoBaoInventoryJob
            JobDetail job1 = newJob(SyncTaoBaoInventoryJob.class)
                    .usingJobData(map1)
                    .withIdentity("SyncTaoBaoInventoryJob", "myjo")
                    .build();

            CronTrigger trigger1 = newTrigger()
                    .withIdentity("SyncTaoBaoInventoryJob", "myjo")
                    .withSchedule(cronSchedule(OrderCatConfig.getSyncTaoBaoInventoryJobTriggerCron()))
                    .build();
            sched.scheduleJob(job1, trigger1);


            sched.start();


        }


    }




}
