package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/6/13.
 */
public class TmOrderDetail {

//    {
//        "data": [
//        {
//            "id": 24957381,
//                "beforestatus": "已付款",
//                "sysuser": "青岛麦巨商贸",
//                "accountdetail": null,
//                "dealdate": "2017-09-14 17:13:16",
//                "orderid": 24693687,
//                "dealdescr": " 取消-已退款退回邮费:24.0,货款：134.55"
//        },
//        {
//            "id": 24956849,
//                "beforestatus": "待付款",
//                "sysuser": "青岛麦巨商贸",
//                "accountdetail": null,
//                "dealdate": "2017-09-14 16:30:50",
//                "orderid": 24693687,
//                "dealdescr": "付款 付款金额:158.55;货款:134.55元,邮费:24.0元"
//        }
//  ],
//        "success": true
//    }

    private long id;
    private String beforestatus;
    private String sysuser;
    private String dealdate;
    private String dealdescr;
    private long orderid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBeforestatus() {
        return beforestatus;
    }

    public void setBeforestatus(String beforestatus) {
        this.beforestatus = beforestatus;
    }

    public String getSysuser() {
        return sysuser;
    }

    public void setSysuser(String sysuser) {
        this.sysuser = sysuser;
    }

    public String getDealdate() {
        return dealdate;
    }

    public void setDealdate(String dealdate) {
        this.dealdate = dealdate;
    }

    public String getDealdescr() {
        return dealdescr;
    }

    public void setDealdescr(String dealdescr) {
        this.dealdescr = dealdescr;
    }

    public long getOrderid() {
        return orderid;
    }

    public void setOrderid(long orderid) {
        this.orderid = orderid;
    }
}
