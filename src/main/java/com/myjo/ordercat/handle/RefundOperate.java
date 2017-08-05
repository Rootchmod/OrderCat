package com.myjo.ordercat.handle;

import com.myjo.ordercat.http.TaoBaoHttp;
import com.myjo.ordercat.http.TianmaSportHttp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptEngine;

/**
 * Created by lee5hx on 2017/8/3.
 */
public class RefundOperate {

    private static final Logger Logger = LogManager.getLogger(RefundOperate.class);

    private TianmaSportHttp tianmaSportHttp;

    private TaoBaoHttp taoBaoHttp;

    private ScriptEngine scriptEngine;

    public RefundOperate(TianmaSportHttp tianmaSportHttp, TaoBaoHttp taoBaoHttp, ScriptEngine scriptEngine) {
        this.tianmaSportHttp = tianmaSportHttp;
        this.taoBaoHttp = taoBaoHttp;
        this.scriptEngine = scriptEngine;
    }






}
