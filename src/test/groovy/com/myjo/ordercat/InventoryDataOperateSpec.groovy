package com.myjo.ordercat

import com.myjo.ordercat.domain.InventoryInfo
import com.myjo.ordercat.handle.InventoryDataOperate
import com.myjo.ordercat.utils.OcBigDecimalUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class InventoryDataOperateSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(InventoryDataOperateSpec.class);



    def setup() {
    }

    def println() {

    }

    def "InventoryDataOperateSpec.filterPickRateList"(){
        when:


        Map<String, Integer> quarterMap = new HashMap<>();

        quarterMap.put("888888-100:1",5)
        quarterMap.put("888888-200:2",1)
        quarterMap.put("888888-300:3",1)
        quarterMap.put("888888-400:4",10)


        List<InventoryInfo> list = new ArrayList<>();
        def t1 = new InventoryInfo()
        t1.setPickRate(90)
        list.add(t1)

        def t2 = new InventoryInfo()
        t2.setGoodsNo("888888-100")
        t2.setWareHouseID(1)
        t2.setPickRate(60)
        list.add(t2)


        def t3 = new InventoryInfo()
        t3.setGoodsNo("888888-200")
        t3.setWareHouseID(2)
        t3.setPickRate(61)
        list.add(t3)

        def t4 = new InventoryInfo()
        t4.setGoodsNo("888888-300")
        t4.setWareHouseID(3)
        t4.setPickRate(70)
        list.add(t4)


        def t5 = new InventoryInfo()
        t5.setGoodsNo("888888-400")
        t5.setWareHouseID(4)
        t5.setPickRate(70)
        list.add(t5)


        list = InventoryDataOperate.filterPickRateList(list,quarterMap);

        then:
        list.size() == 2

    }

//    def "config"() {
//        when:
//        Logger.info("getTianmaSportUserName:" + orderCatContext.getTianmaSportUserName())
//        Logger.info("getTianmaSportPassWord:" + orderCatContext.getTianmaSportPassWord())
//        Logger.info("getTianmaSportVcHttpUrl:" + orderCatContext.getTianmaSportVcHttpUrl())
//        Logger.info("getTianmaSportVcImageFileName:" + orderCatContext.getTianmaSportVcImageFileName())
//        Logger.info("getTianmaSportLoginHttpUrl:" + orderCatContext.getTianmaSportLoginHttpUrl())
//        Logger.info("getTianmaSportIDGHttpUrl:" + orderCatContext.getTianmaSportIDGHttpUrl())
//        //System.out.println(postExample.login());
//        then:
//        "ok" == "ok"
//    }


}
