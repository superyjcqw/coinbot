package com.lh.bot.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Document(collection = "DataMinute")
public class DataMinute {

    @Id
    private ObjectId id;

    private String name;

    private Date time;

    private BigDecimal beginPrice;

    private BigDecimal endPrice;

    private BigDecimal topPrice;

    private BigDecimal bottomPrice;

    private BigDecimal changeRate;

    public DataMinute(String name, Date time, BigDecimal beginPrice, BigDecimal endPrice, BigDecimal topPrice, BigDecimal bottomPrice, BigDecimal changeRate) {
        this.name = name;
        this.time = time;
        this.beginPrice = beginPrice;
        this.endPrice = endPrice;
        this.topPrice = topPrice;
        this.bottomPrice = bottomPrice;
        this.changeRate = changeRate;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public BigDecimal getBeginPrice() {
        return beginPrice;
    }

    public void setBeginPrice(BigDecimal beginPrice) {
        this.beginPrice = beginPrice;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    public BigDecimal getTopPrice() {
        return topPrice;
    }

    public void setTopPrice(BigDecimal topPrice) {
        this.topPrice = topPrice;
    }

    public BigDecimal getBottomPrice() {
        return bottomPrice;
    }

    public void setBottomPrice(BigDecimal bottomPrice) {
        this.bottomPrice = bottomPrice;
    }

    public BigDecimal getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(BigDecimal changeRate) {
        this.changeRate = changeRate;
    }
}
