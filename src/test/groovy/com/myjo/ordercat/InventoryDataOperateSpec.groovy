package com.myjo.ordercat

import com.myjo.ordercat.domain.InventoryInfo
import com.myjo.ordercat.handle.InventoryDataOperate
import com.myjo.ordercat.utils.OcStringUtils
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

        quarterMap.put("888888-100:1:42.5",5)
        quarterMap.put("888888-100:1:42",6)
        quarterMap.put("888888-200:2:42",2)
        quarterMap.put("888888-300:3:43",1)
        quarterMap.put("888888-500:5:43",1)



        List<InventoryInfo> list = new ArrayList<>();


        def t2 = new InventoryInfo()
        t2.setGoodsNo("888888-100")
        t2.setWareHouseID(1)
        t2.setPickRate(53)
        t2.setSize1("42.5")
        list.add(t2)


        def t3 = new InventoryInfo()
        t3.setGoodsNo("888888-100")
        t3.setWareHouseID(1)
        t3.setPickRate(53)
        t3.setSize1("42")
        list.add(t3)

        def t4 = new InventoryInfo()
        t4.setGoodsNo("888888-200")
        t4.setWareHouseID(2)
        t4.setPickRate(70)
        t4.setSize1("42")
        list.add(t4)


        def t5 = new InventoryInfo()
        t5.setGoodsNo("888888-300")
        t5.setWareHouseID(3)
        t5.setPickRate(70)
        t5.setSize1("43")
        list.add(t5)


        def t6 = new InventoryInfo()
        t6.setGoodsNo("888888-500")
        t6.setWareHouseID(5)
        t6.setPickRate(65)
        t6.setSize1("43")
        list.add(t6)


        list = InventoryDataOperate.filterPickRateList(list,quarterMap);

        then:
        list.size() == 2

    }
    def "OcStringUtils.isNumeric"(){
        when:
            def f = OcStringUtils.isNumeric("443.2")
            def f1 = OcStringUtils.isNumeric("12C")
            def f2 = OcStringUtils.isNumeric("-44.2")
        then:
            f == true
            f1 == false
            f2 == true

    }


    def "OcStringUtils.filterAvgPriceList"(){
        when:
        def f = OcStringUtils.isNumeric("443.2")
        def f1 = OcStringUtils.isNumeric("12C")
        def f2 = OcStringUtils.isNumeric("-44.2")
        then:
        f == true
        f1 == false
        f2 == true

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
