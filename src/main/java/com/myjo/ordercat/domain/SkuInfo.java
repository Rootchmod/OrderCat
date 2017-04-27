package com.myjo.ordercat.domain;

/**
 * Created by lee5hx on 17/4/24.
 */
public class SkuInfo {

//
//    {
//            "barcode": "823229678306",
//            "change_prop": "1627207:1080574113:28341,28320",
//            "created": "2017-04-03 08:50:28",
//            "modified": "2017-04-22 10:44:45",
//            "num_iid": 547934591629,
//            "outer_id": "844656-003-39",
//            "price": "459.00",
//            "properties": "1627207:1080574113;20549:672",
//            "properties_name": "1627207:1080574113:颜色分类:黑\/煤黑\/帆白;20549:672:鞋码:39",
//            "quantity": 1,
//            "sku_id": 3320971420805,
//            "sku_spec_id": 1000014202657757,
//            "status": "normal",
//            "with_hold_quantity": 0
//    }


    private String barcode; // barcode String 823229678306 商品级别的条形码
    private String change_prop;//change_prop String pid:vid:vid1,vid2,vid3;pid:vid:vid1,vid2基础色数据 ": "1627207:1080574113:28341,28320",
    private String created;//created String sku创建日期 时间格式：yyyy-MM-dd HH:mm:ss": "2017-04-03 08:50:28",
    private String modified;//String sku最后修改日期 时间格式：yyyy-MM-dd HH:mm:ss": "2017-04-22 10:44:45",
    private String num_iid;//sku所属商品数字id : 547934591629,
    private String outer_id;//商家设置的外部id。天猫和集市的卖家，需要登录才能获取到自己的商家编码，不能获取到他人的商家编码":"844656-003-39"
    private String price;//String 200.07属于这个sku的商品的价格 取值范围:0-100000000;精确到2位小数;单位:元。如:200.07，表示:200元7分。": "459.00",
    private String properties;//sku的销售属性组合字符串（颜色，大小，等等，可通过类目API获取某类目下的销售属性）,格式是p1:v1;p2:v2": "1627207:1080574113;20549:672",
    private String properties_name;//自定义属性1:属性值1sku所对应的销售属性的中文名字串，格式如：pid1:vid1:pid_name1:vid_name1;pid2:vid2:pid_name2:vid_name2": "1627207:1080574113:颜色分类:黑\/煤黑\/帆白;20549:672:鞋码:39",
    private String quantity;//属于这个sku的商品的数量，": 1,
    private String sku_id;//sku的id: 3320971420805,
    private String sku_spec_id;//表示SKu上的产品规格信息": 1000014202657757,
    private String status;//normalsku状态。 normal:正常 ；delete:删除": "normal",
    private String with_hold_quantity;//商品在付款减库存的状态下，该sku上未付款的订单数量: 0


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getChange_prop() {
        return change_prop;
    }

    public void setChange_prop(String change_prop) {
        this.change_prop = change_prop;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getNum_iid() {
        return num_iid;
    }

    public void setNum_iid(String num_iid) {
        this.num_iid = num_iid;
    }

    public String getOuter_id() {
        return outer_id;
    }

    public void setOuter_id(String outer_id) {
        this.outer_id = outer_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getProperties_name() {
        return properties_name;
    }

    public void setProperties_name(String properties_name) {
        this.properties_name = properties_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSku_id() {
        return sku_id;
    }

    public void setSku_id(String sku_id) {
        this.sku_id = sku_id;
    }

    public String getSku_spec_id() {
        return sku_spec_id;
    }

    public void setSku_spec_id(String sku_spec_id) {
        this.sku_spec_id = sku_spec_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWith_hold_quantity() {
        return with_hold_quantity;
    }

    public void setWith_hold_quantity(String with_hold_quantity) {
        this.with_hold_quantity = with_hold_quantity;
    }
}
