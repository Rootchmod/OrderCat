package com.myjo.ordercat

import com.myjo.ordercat.config.OrderCatConfig
import com.myjo.ordercat.handle.DataGatheringHandle
import com.myjo.ordercat.http.TianmaSportHttp
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lee5hx on 17/4/20.
 */
@Unroll
class DataGatheringHandleSpec extends Specification {

    private static final Logger Logger = LogManager.getLogger(TianmaSportHttp.class);


    private static OrderCatConfig orderCatContext;
    private static TianmaSportHttp tianmaSportHttp;

    private static DataGatheringHandle dataGatheringHandle;

    def setupSpec() {
        orderCatContext = new OrderCatConfig();
        def map = new HashMap<>();
        tianmaSportHttp = new TianmaSportHttp(map,orderCatContext);
        dataGatheringHandle = new DataGatheringHandle(orderCatContext,tianmaSportHttp);

    }

    def println() {

    }

    def "data-gathering.exec"(){
        when:
        tianmaSportHttp.getVerifyCodeImage();
        String v = "1111";

        def f = tianmaSportHttp.login(v);
        tianmaSportHttp.main_html();
        dataGatheringHandle.exec();
        then:
        "ok" == "ok"
    }




}
