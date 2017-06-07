package com.myjo.ordercat.spm.ordercat.ordercat.oc_tianma_check_result.generated;

import com.myjo.ordercat.spm.ordercat.ordercat.oc_tianma_check_result.OcTianmaCheckResult;
import com.speedment.runtime.config.identifier.ColumnIdentifier;
import com.speedment.runtime.config.identifier.TableIdentifier;
import com.speedment.runtime.core.annotation.GeneratedCode;
import com.speedment.runtime.core.util.OptionalUtil;
import com.speedment.runtime.field.ComparableField;
import com.speedment.runtime.field.LongField;
import com.speedment.runtime.field.StringField;
import com.speedment.runtime.typemapper.TypeMapper;
import com.speedment.runtime.typemapper.time.TimestampToLocalDateTimeMapper;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

/**
 * The generated base for the {@link
 * com.myjo.ordercat.spm.ordercat.ordercat.oc_tianma_check_result.OcTianmaCheckResult}-interface
 * representing entities of the {@code oc_tianma_check_result}-table in the
 * database.
 * <p>
 * This file has been automatically generated by Speedment. Any changes made to
 * it will be overwritten.
 * 
 * @author Speedment
 */
@GeneratedCode("Speedment")
public interface GeneratedOcTianmaCheckResult {
    
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getId()} method.
     */
    final LongField<OcTianmaCheckResult, Long> ID = LongField.create(
        Identifier.ID,
        OcTianmaCheckResult::getId,
        OcTianmaCheckResult::setId,
        TypeMapper.primitive(), 
        true
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTbOrderId()} method.
     */
    final ComparableField<OcTianmaCheckResult, Long, Long> TB_ORDER_ID = ComparableField.create(
        Identifier.TB_ORDER_ID,
        o -> OptionalUtil.unwrap(o.getTbOrderId()),
        OcTianmaCheckResult::setTbOrderId,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTbOrderStatus()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TB_ORDER_STATUS = StringField.create(
        Identifier.TB_ORDER_STATUS,
        o -> OptionalUtil.unwrap(o.getTbOrderStatus()),
        OcTianmaCheckResult::setTbOrderStatus,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTbNumIid()} method.
     */
    final ComparableField<OcTianmaCheckResult, Long, Long> TB_NUM_IID = ComparableField.create(
        Identifier.TB_NUM_IID,
        o -> OptionalUtil.unwrap(o.getTbNumIid()),
        OcTianmaCheckResult::setTbNumIid,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTbTitle()} method.
     */
    final StringField<OcTianmaCheckResult, String> TB_TITLE = StringField.create(
        Identifier.TB_TITLE,
        o -> OptionalUtil.unwrap(o.getTbTitle()),
        OcTianmaCheckResult::setTbTitle,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTbPayment()} method.
     */
    final ComparableField<OcTianmaCheckResult, BigDecimal, BigDecimal> TB_PAYMENT = ComparableField.create(
        Identifier.TB_PAYMENT,
        o -> OptionalUtil.unwrap(o.getTbPayment()),
        OcTianmaCheckResult::setTbPayment,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTbRefundId()} method.
     */
    final ComparableField<OcTianmaCheckResult, Long, Long> TB_REFUND_ID = ComparableField.create(
        Identifier.TB_REFUND_ID,
        o -> OptionalUtil.unwrap(o.getTbRefundId()),
        OcTianmaCheckResult::setTbRefundId,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTbRefundStatus()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TB_REFUND_STATUS = StringField.create(
        Identifier.TB_REFUND_STATUS,
        o -> OptionalUtil.unwrap(o.getTbRefundStatus()),
        OcTianmaCheckResult::setTbRefundStatus,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTbNum()} method.
     */
    final ComparableField<OcTianmaCheckResult, Long, Long> TB_NUM = ComparableField.create(
        Identifier.TB_NUM,
        o -> OptionalUtil.unwrap(o.getTbNum()),
        OcTianmaCheckResult::setTbNum,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmOrderId()} method.
     */
    final ComparableField<OcTianmaCheckResult, Long, Long> TM_ORDER_ID = ComparableField.create(
        Identifier.TM_ORDER_ID,
        o -> OptionalUtil.unwrap(o.getTmOrderId()),
        OcTianmaCheckResult::setTmOrderId,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmOuterOrderId()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TM_OUTER_ORDER_ID = StringField.create(
        Identifier.TM_OUTER_ORDER_ID,
        o -> OptionalUtil.unwrap(o.getTmOuterOrderId()),
        OcTianmaCheckResult::setTmOuterOrderId,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmGoodsNo()} method.
     */
    final StringField<OcTianmaCheckResult, String> TM_GOODS_NO = StringField.create(
        Identifier.TM_GOODS_NO,
        o -> OptionalUtil.unwrap(o.getTmGoodsNo()),
        OcTianmaCheckResult::setTmGoodsNo,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmOrderStatus()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TM_ORDER_STATUS = StringField.create(
        Identifier.TM_ORDER_STATUS,
        o -> OptionalUtil.unwrap(o.getTmOrderStatus()),
        OcTianmaCheckResult::setTmOrderStatus,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmDeliveryNo()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TM_DELIVERY_NO = StringField.create(
        Identifier.TM_DELIVERY_NO,
        o -> OptionalUtil.unwrap(o.getTmDeliveryNo()),
        OcTianmaCheckResult::setTmDeliveryNo,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmDeliveryName()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TM_DELIVERY_NAME = StringField.create(
        Identifier.TM_DELIVERY_NAME,
        o -> OptionalUtil.unwrap(o.getTmDeliveryName()),
        OcTianmaCheckResult::setTmDeliveryName,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmWarehouseId()}
     * method.
     */
    final ComparableField<OcTianmaCheckResult, Integer, Integer> TM_WAREHOUSE_ID = ComparableField.create(
        Identifier.TM_WAREHOUSE_ID,
        o -> OptionalUtil.unwrap(o.getTmWarehouseId()),
        OcTianmaCheckResult::setTmWarehouseId,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmBuyerName()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TM_BUYER_NAME = StringField.create(
        Identifier.TM_BUYER_NAME,
        o -> OptionalUtil.unwrap(o.getTmBuyerName()),
        OcTianmaCheckResult::setTmBuyerName,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmWarehouseName()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TM_WAREHOUSE_NAME = StringField.create(
        Identifier.TM_WAREHOUSE_NAME,
        o -> OptionalUtil.unwrap(o.getTmWarehouseName()),
        OcTianmaCheckResult::setTmWarehouseName,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmPayPrice()} method.
     */
    final ComparableField<OcTianmaCheckResult, BigDecimal, BigDecimal> TM_PAY_PRICE = ComparableField.create(
        Identifier.TM_PAY_PRICE,
        o -> OptionalUtil.unwrap(o.getTmPayPrice()),
        OcTianmaCheckResult::setTmPayPrice,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmPostFee()} method.
     */
    final ComparableField<OcTianmaCheckResult, BigDecimal, BigDecimal> TM_POST_FEE = ComparableField.create(
        Identifier.TM_POST_FEE,
        o -> OptionalUtil.unwrap(o.getTmPostFee()),
        OcTianmaCheckResult::setTmPostFee,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getTmNoshipmentRemark()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> TM_NOSHIPMENT_REMARK = StringField.create(
        Identifier.TM_NOSHIPMENT_REMARK,
        o -> OptionalUtil.unwrap(o.getTmNoshipmentRemark()),
        OcTianmaCheckResult::setTmNoshipmentRemark,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getSize1()} method.
     */
    final StringField<OcTianmaCheckResult, String> SIZE1 = StringField.create(
        Identifier.SIZE1,
        o -> OptionalUtil.unwrap(o.getSize1()),
        OcTianmaCheckResult::setSize1,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getSize2()} method.
     */
    final StringField<OcTianmaCheckResult, String> SIZE2 = StringField.create(
        Identifier.SIZE2,
        o -> OptionalUtil.unwrap(o.getSize2()),
        OcTianmaCheckResult::setSize2,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getDzStatus()} method.
     */
    final StringField<OcTianmaCheckResult, String> DZ_STATUS = StringField.create(
        Identifier.DZ_STATUS,
        o -> OptionalUtil.unwrap(o.getDzStatus()),
        OcTianmaCheckResult::setDzStatus,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getDzDetailsMessage()}
     * method.
     */
    final StringField<OcTianmaCheckResult, String> DZ_DETAILS_MESSAGE = StringField.create(
        Identifier.DZ_DETAILS_MESSAGE,
        o -> OptionalUtil.unwrap(o.getDzDetailsMessage()),
        OcTianmaCheckResult::setDzDetailsMessage,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getRemarks()} method.
     */
    final StringField<OcTianmaCheckResult, String> REMARKS = StringField.create(
        Identifier.REMARKS,
        o -> OptionalUtil.unwrap(o.getRemarks()),
        OcTianmaCheckResult::setRemarks,
        TypeMapper.identity(), 
        false
    );
    /**
     * This Field corresponds to the {@link OcTianmaCheckResult} field that can
     * be obtained using the {@link OcTianmaCheckResult#getAddTime()} method.
     */
    final ComparableField<OcTianmaCheckResult, Timestamp, LocalDateTime> ADD_TIME = ComparableField.create(
        Identifier.ADD_TIME,
        OcTianmaCheckResult::getAddTime,
        OcTianmaCheckResult::setAddTime,
        new TimestampToLocalDateTimeMapper(), 
        false
    );
    
    /**
     * Returns the id of this OcTianmaCheckResult. The id field corresponds to
     * the database column ordercat.ordercat.oc_tianma_check_result.id.
     * 
     * @return the id of this OcTianmaCheckResult
     */
    long getId();
    
    /**
     * Returns the tbOrderId of this OcTianmaCheckResult. The tbOrderId field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_order_id.
     * 
     * @return the tbOrderId of this OcTianmaCheckResult
     */
    OptionalLong getTbOrderId();
    
    /**
     * Returns the tbOrderStatus of this OcTianmaCheckResult. The tbOrderStatus
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_order_status.
     * 
     * @return the tbOrderStatus of this OcTianmaCheckResult
     */
    Optional<String> getTbOrderStatus();
    
    /**
     * Returns the tbNumIid of this OcTianmaCheckResult. The tbNumIid field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_numIid.
     * 
     * @return the tbNumIid of this OcTianmaCheckResult
     */
    OptionalLong getTbNumIid();
    
    /**
     * Returns the tbTitle of this OcTianmaCheckResult. The tbTitle field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_title.
     * 
     * @return the tbTitle of this OcTianmaCheckResult
     */
    Optional<String> getTbTitle();
    
    /**
     * Returns the tbPayment of this OcTianmaCheckResult. The tbPayment field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_payment.
     * 
     * @return the tbPayment of this OcTianmaCheckResult
     */
    Optional<BigDecimal> getTbPayment();
    
    /**
     * Returns the tbRefundId of this OcTianmaCheckResult. The tbRefundId field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_refundID.
     * 
     * @return the tbRefundId of this OcTianmaCheckResult
     */
    OptionalLong getTbRefundId();
    
    /**
     * Returns the tbRefundStatus of this OcTianmaCheckResult. The
     * tbRefundStatus field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_refundStatus.
     * 
     * @return the tbRefundStatus of this OcTianmaCheckResult
     */
    Optional<String> getTbRefundStatus();
    
    /**
     * Returns the tbNum of this OcTianmaCheckResult. The tbNum field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_num.
     * 
     * @return the tbNum of this OcTianmaCheckResult
     */
    OptionalLong getTbNum();
    
    /**
     * Returns the tmOrderId of this OcTianmaCheckResult. The tmOrderId field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_order_id.
     * 
     * @return the tmOrderId of this OcTianmaCheckResult
     */
    OptionalLong getTmOrderId();
    
    /**
     * Returns the tmOuterOrderId of this OcTianmaCheckResult. The
     * tmOuterOrderId field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_outer_order_id.
     * 
     * @return the tmOuterOrderId of this OcTianmaCheckResult
     */
    Optional<String> getTmOuterOrderId();
    
    /**
     * Returns the tmGoodsNo of this OcTianmaCheckResult. The tmGoodsNo field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_goods_no.
     * 
     * @return the tmGoodsNo of this OcTianmaCheckResult
     */
    Optional<String> getTmGoodsNo();
    
    /**
     * Returns the tmOrderStatus of this OcTianmaCheckResult. The tmOrderStatus
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_order_status.
     * 
     * @return the tmOrderStatus of this OcTianmaCheckResult
     */
    Optional<String> getTmOrderStatus();
    
    /**
     * Returns the tmDeliveryNo of this OcTianmaCheckResult. The tmDeliveryNo
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_delivery_no.
     * 
     * @return the tmDeliveryNo of this OcTianmaCheckResult
     */
    Optional<String> getTmDeliveryNo();
    
    /**
     * Returns the tmDeliveryName of this OcTianmaCheckResult. The
     * tmDeliveryName field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_delivery_name.
     * 
     * @return the tmDeliveryName of this OcTianmaCheckResult
     */
    Optional<String> getTmDeliveryName();
    
    /**
     * Returns the tmWarehouseId of this OcTianmaCheckResult. The tmWarehouseId
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_warehouse_id.
     * 
     * @return the tmWarehouseId of this OcTianmaCheckResult
     */
    OptionalInt getTmWarehouseId();
    
    /**
     * Returns the tmBuyerName of this OcTianmaCheckResult. The tmBuyerName
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_buyer_name.
     * 
     * @return the tmBuyerName of this OcTianmaCheckResult
     */
    Optional<String> getTmBuyerName();
    
    /**
     * Returns the tmWarehouseName of this OcTianmaCheckResult. The
     * tmWarehouseName field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_warehouse_name.
     * 
     * @return the tmWarehouseName of this OcTianmaCheckResult
     */
    Optional<String> getTmWarehouseName();
    
    /**
     * Returns the tmPayPrice of this OcTianmaCheckResult. The tmPayPrice field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_payPrice.
     * 
     * @return the tmPayPrice of this OcTianmaCheckResult
     */
    Optional<BigDecimal> getTmPayPrice();
    
    /**
     * Returns the tmPostFee of this OcTianmaCheckResult. The tmPostFee field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_postFee.
     * 
     * @return the tmPostFee of this OcTianmaCheckResult
     */
    Optional<BigDecimal> getTmPostFee();
    
    /**
     * Returns the tmNoshipmentRemark of this OcTianmaCheckResult. The
     * tmNoshipmentRemark field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_noshipment_Remark.
     * 
     * @return the tmNoshipmentRemark of this OcTianmaCheckResult
     */
    Optional<String> getTmNoshipmentRemark();
    
    /**
     * Returns the size1 of this OcTianmaCheckResult. The size1 field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.size1.
     * 
     * @return the size1 of this OcTianmaCheckResult
     */
    Optional<String> getSize1();
    
    /**
     * Returns the size2 of this OcTianmaCheckResult. The size2 field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.size2.
     * 
     * @return the size2 of this OcTianmaCheckResult
     */
    Optional<String> getSize2();
    
    /**
     * Returns the dzStatus of this OcTianmaCheckResult. The dzStatus field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.dz_status.
     * 
     * @return the dzStatus of this OcTianmaCheckResult
     */
    Optional<String> getDzStatus();
    
    /**
     * Returns the dzDetailsMessage of this OcTianmaCheckResult. The
     * dzDetailsMessage field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.dz_details_message.
     * 
     * @return the dzDetailsMessage of this OcTianmaCheckResult
     */
    Optional<String> getDzDetailsMessage();
    
    /**
     * Returns the remarks of this OcTianmaCheckResult. The remarks field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.remarks.
     * 
     * @return the remarks of this OcTianmaCheckResult
     */
    Optional<String> getRemarks();
    
    /**
     * Returns the addTime of this OcTianmaCheckResult. The addTime field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.add_time.
     * 
     * @return the addTime of this OcTianmaCheckResult
     */
    LocalDateTime getAddTime();
    
    /**
     * Sets the id of this OcTianmaCheckResult. The id field corresponds to the
     * database column ordercat.ordercat.oc_tianma_check_result.id.
     * 
     * @param id to set of this OcTianmaCheckResult
     * @return   this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setId(long id);
    
    /**
     * Sets the tbOrderId of this OcTianmaCheckResult. The tbOrderId field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_order_id.
     * 
     * @param tbOrderId to set of this OcTianmaCheckResult
     * @return          this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTbOrderId(Long tbOrderId);
    
    /**
     * Sets the tbOrderStatus of this OcTianmaCheckResult. The tbOrderStatus
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_order_status.
     * 
     * @param tbOrderStatus to set of this OcTianmaCheckResult
     * @return              this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTbOrderStatus(String tbOrderStatus);
    
    /**
     * Sets the tbNumIid of this OcTianmaCheckResult. The tbNumIid field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_numIid.
     * 
     * @param tbNumIid to set of this OcTianmaCheckResult
     * @return         this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTbNumIid(Long tbNumIid);
    
    /**
     * Sets the tbTitle of this OcTianmaCheckResult. The tbTitle field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_title.
     * 
     * @param tbTitle to set of this OcTianmaCheckResult
     * @return        this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTbTitle(String tbTitle);
    
    /**
     * Sets the tbPayment of this OcTianmaCheckResult. The tbPayment field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_payment.
     * 
     * @param tbPayment to set of this OcTianmaCheckResult
     * @return          this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTbPayment(BigDecimal tbPayment);
    
    /**
     * Sets the tbRefundId of this OcTianmaCheckResult. The tbRefundId field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_refundID.
     * 
     * @param tbRefundId to set of this OcTianmaCheckResult
     * @return           this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTbRefundId(Long tbRefundId);
    
    /**
     * Sets the tbRefundStatus of this OcTianmaCheckResult. The tbRefundStatus
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tb_refundStatus.
     * 
     * @param tbRefundStatus to set of this OcTianmaCheckResult
     * @return               this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTbRefundStatus(String tbRefundStatus);
    
    /**
     * Sets the tbNum of this OcTianmaCheckResult. The tbNum field corresponds
     * to the database column ordercat.ordercat.oc_tianma_check_result.tb_num.
     * 
     * @param tbNum to set of this OcTianmaCheckResult
     * @return      this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTbNum(Long tbNum);
    
    /**
     * Sets the tmOrderId of this OcTianmaCheckResult. The tmOrderId field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_order_id.
     * 
     * @param tmOrderId to set of this OcTianmaCheckResult
     * @return          this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmOrderId(Long tmOrderId);
    
    /**
     * Sets the tmOuterOrderId of this OcTianmaCheckResult. The tmOuterOrderId
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_outer_order_id.
     * 
     * @param tmOuterOrderId to set of this OcTianmaCheckResult
     * @return               this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmOuterOrderId(String tmOuterOrderId);
    
    /**
     * Sets the tmGoodsNo of this OcTianmaCheckResult. The tmGoodsNo field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_goods_no.
     * 
     * @param tmGoodsNo to set of this OcTianmaCheckResult
     * @return          this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmGoodsNo(String tmGoodsNo);
    
    /**
     * Sets the tmOrderStatus of this OcTianmaCheckResult. The tmOrderStatus
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_order_status.
     * 
     * @param tmOrderStatus to set of this OcTianmaCheckResult
     * @return              this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmOrderStatus(String tmOrderStatus);
    
    /**
     * Sets the tmDeliveryNo of this OcTianmaCheckResult. The tmDeliveryNo field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_delivery_no.
     * 
     * @param tmDeliveryNo to set of this OcTianmaCheckResult
     * @return             this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmDeliveryNo(String tmDeliveryNo);
    
    /**
     * Sets the tmDeliveryName of this OcTianmaCheckResult. The tmDeliveryName
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_delivery_name.
     * 
     * @param tmDeliveryName to set of this OcTianmaCheckResult
     * @return               this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmDeliveryName(String tmDeliveryName);
    
    /**
     * Sets the tmWarehouseId of this OcTianmaCheckResult. The tmWarehouseId
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_warehouse_id.
     * 
     * @param tmWarehouseId to set of this OcTianmaCheckResult
     * @return              this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmWarehouseId(Integer tmWarehouseId);
    
    /**
     * Sets the tmBuyerName of this OcTianmaCheckResult. The tmBuyerName field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_buyer_name.
     * 
     * @param tmBuyerName to set of this OcTianmaCheckResult
     * @return            this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmBuyerName(String tmBuyerName);
    
    /**
     * Sets the tmWarehouseName of this OcTianmaCheckResult. The tmWarehouseName
     * field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_warehouse_name.
     * 
     * @param tmWarehouseName to set of this OcTianmaCheckResult
     * @return                this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmWarehouseName(String tmWarehouseName);
    
    /**
     * Sets the tmPayPrice of this OcTianmaCheckResult. The tmPayPrice field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_payPrice.
     * 
     * @param tmPayPrice to set of this OcTianmaCheckResult
     * @return           this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmPayPrice(BigDecimal tmPayPrice);
    
    /**
     * Sets the tmPostFee of this OcTianmaCheckResult. The tmPostFee field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_postFee.
     * 
     * @param tmPostFee to set of this OcTianmaCheckResult
     * @return          this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmPostFee(BigDecimal tmPostFee);
    
    /**
     * Sets the tmNoshipmentRemark of this OcTianmaCheckResult. The
     * tmNoshipmentRemark field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.tm_noshipment_Remark.
     * 
     * @param tmNoshipmentRemark to set of this OcTianmaCheckResult
     * @return                   this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setTmNoshipmentRemark(String tmNoshipmentRemark);
    
    /**
     * Sets the size1 of this OcTianmaCheckResult. The size1 field corresponds
     * to the database column ordercat.ordercat.oc_tianma_check_result.size1.
     * 
     * @param size1 to set of this OcTianmaCheckResult
     * @return      this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setSize1(String size1);
    
    /**
     * Sets the size2 of this OcTianmaCheckResult. The size2 field corresponds
     * to the database column ordercat.ordercat.oc_tianma_check_result.size2.
     * 
     * @param size2 to set of this OcTianmaCheckResult
     * @return      this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setSize2(String size2);
    
    /**
     * Sets the dzStatus of this OcTianmaCheckResult. The dzStatus field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.dz_status.
     * 
     * @param dzStatus to set of this OcTianmaCheckResult
     * @return         this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setDzStatus(String dzStatus);
    
    /**
     * Sets the dzDetailsMessage of this OcTianmaCheckResult. The
     * dzDetailsMessage field corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.dz_details_message.
     * 
     * @param dzDetailsMessage to set of this OcTianmaCheckResult
     * @return                 this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setDzDetailsMessage(String dzDetailsMessage);
    
    /**
     * Sets the remarks of this OcTianmaCheckResult. The remarks field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.remarks.
     * 
     * @param remarks to set of this OcTianmaCheckResult
     * @return        this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setRemarks(String remarks);
    
    /**
     * Sets the addTime of this OcTianmaCheckResult. The addTime field
     * corresponds to the database column
     * ordercat.ordercat.oc_tianma_check_result.add_time.
     * 
     * @param addTime to set of this OcTianmaCheckResult
     * @return        this OcTianmaCheckResult instance
     */
    OcTianmaCheckResult setAddTime(LocalDateTime addTime);
    
    enum Identifier implements ColumnIdentifier<OcTianmaCheckResult> {
        
        ID                   ("id"),
        TB_ORDER_ID          ("tb_order_id"),
        TB_ORDER_STATUS      ("tb_order_status"),
        TB_NUM_IID           ("tb_numIid"),
        TB_TITLE             ("tb_title"),
        TB_PAYMENT           ("tb_payment"),
        TB_REFUND_ID         ("tb_refundID"),
        TB_REFUND_STATUS     ("tb_refundStatus"),
        TB_NUM               ("tb_num"),
        TM_ORDER_ID          ("tm_order_id"),
        TM_OUTER_ORDER_ID    ("tm_outer_order_id"),
        TM_GOODS_NO          ("tm_goods_no"),
        TM_ORDER_STATUS      ("tm_order_status"),
        TM_DELIVERY_NO       ("tm_delivery_no"),
        TM_DELIVERY_NAME     ("tm_delivery_name"),
        TM_WAREHOUSE_ID      ("tm_warehouse_id"),
        TM_BUYER_NAME        ("tm_buyer_name"),
        TM_WAREHOUSE_NAME    ("tm_warehouse_name"),
        TM_PAY_PRICE         ("tm_payPrice"),
        TM_POST_FEE          ("tm_postFee"),
        TM_NOSHIPMENT_REMARK ("tm_noshipment_Remark"),
        SIZE1                ("size1"),
        SIZE2                ("size2"),
        DZ_STATUS            ("dz_status"),
        DZ_DETAILS_MESSAGE   ("dz_details_message"),
        REMARKS              ("remarks"),
        ADD_TIME             ("add_time");
        
        private final String columnName;
        private final TableIdentifier<OcTianmaCheckResult> tableIdentifier;
        
        Identifier(String columnName) {
            this.columnName      = columnName;
            this.tableIdentifier = TableIdentifier.of(    getDbmsName(), 
                getSchemaName(), 
                getTableName());
        }
        
        @Override
        public String getDbmsName() {
            return "ordercat";
        }
        
        @Override
        public String getSchemaName() {
            return "ordercat";
        }
        
        @Override
        public String getTableName() {
            return "oc_tianma_check_result";
        }
        
        @Override
        public String getColumnName() {
            return this.columnName;
        }
        
        @Override
        public TableIdentifier<OcTianmaCheckResult> asTableIdentifier() {
            return this.tableIdentifier;
        }
    }
}