package com.lh.bot.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "ExecuteLog")
public class ExecuteLog {

    private String coin;

    private String batchNum;

    private Date time;

    private String operation;

    private Object value;

    private ObjectId tradeRecordId;

    public ExecuteLog(String coin, String batchNum, Date time, String operation, Object value, ObjectId tradeRecordId) {
        this.coin = coin;
        this.batchNum = batchNum;
        this.time = time;
        this.operation = operation;
        this.value = value;
        this.tradeRecordId = tradeRecordId;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ObjectId getTradeRecordId() {
        return tradeRecordId;
    }

    public void setTradeRecordId(ObjectId tradeRecordId) {
        this.tradeRecordId = tradeRecordId;
    }
}
