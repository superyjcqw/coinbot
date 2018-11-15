package com.lh.bot.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "TRecord")
public class TradeRecord {

    /**
     * 交易类型 - 买
     */
    public static final Integer TYPE_BUY = 1;
    /**
     * 交易类型 - 卖
     */
    public static final Integer TYPE_SALE = 2;

    /**
     * 状态 - 初始化
     */
    public static final Integer STATUS_INIT = 1;
    /**
     * 状态 - 已处理
     */
    public static final Integer STATUS_DONE = 2;
    /**
     * 状态 - 无效的
     */
    public static final Integer STATUS_INVALID = 3;

    @Id
    private ObjectId id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 交易类型 1买  2卖
     */
    private Integer type;
    /**
     * 交易备注
     */
    private String memo;
    /**
     * 币种
     */
    private String coin;
    /**
     * 收益
     */
    private Double income;
    /**
     * 交易价格
     */
    private Double price;
    /**
     * 状态 0 未操作 1已买或已卖
     */
    private Integer status;
    /**
     * 买入挂单号
     */
    private String buyOrderId;
    /**
     * 卖出挂单号
     */
    private String saleOrderId;
    /**
     * 成交时间
     */
    private String doneTime;

    public TradeRecord(Date createTime, Integer type, String memo, String coin, Double income, Double price, Integer status) {
        this.createTime = createTime;
        this.type = type;
        this.memo = memo;
        this.coin = coin;
        this.income = income;
        this.price = price;
        this.status = status;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBuyOrderId() {
        return buyOrderId;
    }

    public void setBuyOrderId(String buyOrderId) {
        this.buyOrderId = buyOrderId;
    }

    public String getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(String saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public String getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime;
    }
}
