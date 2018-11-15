package com.lh.bot.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PriceMonitor")
public class PriceMonitor {

    private String name;

    private Double price;

    private Integer tipCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getTipCount() {
        return tipCount;
    }

    public void setTipCount(Integer tipCount) {
        this.tipCount = tipCount;
    }
}
