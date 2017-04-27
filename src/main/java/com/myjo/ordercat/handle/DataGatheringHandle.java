package com.myjo.ordercat.handle;


import com.myjo.ordercat.domain.InventoryQueryCondition;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.http.TianmaSportHttp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by lee5hx on 17/4/23.
 */
public class DataGatheringHandle {

    private static final Logger Logger = LogManager.getLogger(DataGatheringHandle.class);

    private TianmaSportHttp tianmaSportHttp;

    public DataGatheringHandle(TianmaSportHttp tianmaSportHttp){
        this.tianmaSportHttp =tianmaSportHttp;
    }

    public void exec() throws Exception{
        List<InventoryQueryCondition> list = OrderCatConfig.getInventoryQueryConditions();
        Logger.debug("InventoryQueryCondition.list"+list.size());
        for(InventoryQueryCondition iqc :list){
            Logger.debug("InventoryQueryCondition.BrandName"+iqc.getBrandName());
            Logger.debug("InventoryQueryCondition.Quarter"+iqc.getQuarter());
            //tianmaSportHttp.inventoryDownGroup(iqc.getBrandName(),iqc.getQuarter());
        }
        Logger.debug("InventoryQueryCondition.exec done.");
    }
}
