package com.lh.bot.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.bot.entity.ExecuteLog;
import com.lh.bot.entity.TradeRecord;
import com.lh.bot.platform.huobi.api.CustomClient;
import com.lh.bot.platform.huobi.request.CreateOrderRequest;
import com.lh.bot.platform.huobi.request.IntrustOrdersDetailRequest;
import com.lh.bot.platform.huobi.response.*;
import com.lh.bot.util.BeanManager;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SaleInstance {

    private static String batchNum;

    private static ObjectId tradeRecordId;

    private String coinType;

    private String aiCoinType;

    private String coin;

    private int scale;

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public void setAiCoinType(String aiCoinType) {
        this.aiCoinType = aiCoinType;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void sale() {
        //是否满足购买条件
        //是否持有该币
        //循环减价卖出
        //挂单成功更新购买状态
        batchNum = UUID.randomUUID().toString();
        Query query = new Query(Criteria.where("coin").is(aiCoinType));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        TradeRecord tradeRecord = BeanManager.getBean(MongoTemplate.class).findOne(query, TradeRecord.class);
        if (tradeRecord.getType().equals(TradeRecord.TYPE_SALE) && tradeRecord.getStatus().equals(TradeRecord.STATUS_INIT)) {
            tradeRecordId = tradeRecord.getId();
            addExecuteLog(tradeRecord.getSaleOrderId() == null ? "符合卖出条件" : "继续卖出", tradeRecord.getPrice());
            //撤销上一个买单
            cancelLastBuyOrder();

            //查询当前是不有挂单，有的话取消当前挂单重新减价挂单
            if (tradeRecord.getSaleOrderId() != null) {
                OrdersDetailResponse ordersDetail = CustomClient.getClient().ordersDetail(tradeRecord.getSaleOrderId());
                JSONObject orderJson = JSONObject.parseObject(JSON.toJSONString(ordersDetail.getData()));
                String state = orderJson.get("state").toString();
                if (state.equals(IntrustOrdersDetailRequest.OrderStates.FILLED)) {
                    addExecuteLog("已成交", JSON.toJSONString(ordersDetail));
                    //更新状态
                    BeanManager.getBean(MongoTemplate.class).updateFirst(Query.query(Criteria.where("_id").is(tradeRecord.getId())), Update.update("status", TradeRecord.STATUS_DONE).set("doneTime", new Date()), TradeRecord.class);
                    return;
                } else if (state.equals(IntrustOrdersDetailRequest.OrderStates.SUBMITTED)
                        || state.equals(IntrustOrdersDetailRequest.OrderStates.PARTIAL_FILLED)) {
                    cancelOrder(tradeRecord.getSaleOrderId(), state);
                }
            }

            //查持有该币余额
            AccountsResponse accounts = CustomClient.getClient().accounts();
            List<Accounts> list = (List<Accounts>) accounts.getData();
            String accountId = String.valueOf(list.get(0).getId());
            BigDecimal balance = CustomClient.getBalance(CustomClient.getClient(), accountId, coin);
            //持有数量小于1时
            if (balance.compareTo(new BigDecimal("0.01")) < 0) {
                addExecuteLog("检查到持有币不足，退出卖出流程", balance);
                BeanManager.getBean(MongoTemplate.class).updateFirst(Query.query(Criteria.where("_id").is(tradeRecord.getId())), Update.update("status", TradeRecord.STATUS_INVALID), TradeRecord.class);
            } else {
                addExecuteLog("检查到有可卖出币", balance);

                //挂单前准备
                //获取当前交易深度
                Depth tick = CustomClient.getDepth(CustomClient.getClient(), coinType);
                //找出最低卖单减一个最小单位
//                BigDecimal salePrice = new BigDecimal(String.valueOf(tick.getBids().get(0).get(0))).setScale(scale);
                BigDecimal salePrice = new BigDecimal(String.valueOf(tick.getAsks().get(0).get(0))).subtract(new BigDecimal("0.0001")).setScale(scale);
//                BigDecimal salePrice = (new BigDecimal(String.valueOf(tick.getBids().get(0).get(0))).add(new BigDecimal(String.valueOf(tick.getAsks().get(0).get(0))))).divide(new BigDecimal("2"), scale, BigDecimal.ROUND_DOWN);


                //创建并执行订单
                String orderId = createOrder(accountId, balance.setScale(2, BigDecimal.ROUND_DOWN), salePrice);
                //更新最后一次挂单ID
                BeanManager.getBean(MongoTemplate.class).updateFirst(Query.query(Criteria.where("_id").is(tradeRecord.getId())), Update.update("saleOrderId", orderId), TradeRecord.class);
            }
        }
    }

    private void cancelOrder(String states) {
        //取消未成交的订单
        IntrustOrdersDetailRequest req = new IntrustOrdersDetailRequest();
        req.symbol = coinType;
        req.types = IntrustOrdersDetailRequest.OrderType.BUY_LIMIT;
        req.states = states;
        IntrustDetailResponse intrustDetail = CustomClient.getClient().intrustOrdersDetail(req);
        JSONArray array = JSON.parseArray(JSON.toJSONString(intrustDetail.getData()));
        if (array.size() > 0) {
            addExecuteLog("检查到"+ (states.equals("submitted") ? "已提交" : "部分提交") +"订单", array.toString());
            List orderList = new ArrayList();
            for (int i = 0; i < array.size(); i ++) {
                orderList.add(JSON.parseObject(JSON.toJSONString(array.get(i))).get("id"));
            }
            BatchcancelResponse submitcancels = CustomClient.getClient().submitcancels(orderList);
            if (submitcancels.getStatus().equals("ok")) {
                addExecuteLog("取消" + (states.equals("submitted") ? "已提交" : "部分提交") + "订单", JSON.toJSONString(submitcancels));
            } else {
                addExecuteLog("取消失败", JSON.toJSONString(submitcancels));
            }
        }
    }

    private void cancelOrder(String orderId, String state) {
        addExecuteLog("检查到"+ (state.equals("submitted") ? "已提交" : "部分提交") +"卖出订单", "");
        SubmitcancelResponse submitcancel = CustomClient.getClient().submitcancel(orderId);
        if (submitcancel.getStatus().equals("ok")) {
            addExecuteLog("取消" + (state.equals("submitted") ? "已提交" : "部分提交") + "卖出订单", JSON.toJSONString(submitcancel));
        } else {
            addExecuteLog("取消失败", JSON.toJSONString(submitcancel));
        }
    }

    private String createOrder(String accountId, BigDecimal amount, BigDecimal price) {
        CreateOrderRequest createOrderReq = new CreateOrderRequest();
        createOrderReq.accountId = accountId;
        createOrderReq.amount = String.valueOf(amount);
        createOrderReq.price = String.valueOf(price);
        createOrderReq.symbol = coinType;
        createOrderReq.type = CreateOrderRequest.OrderType.SELL_LIMIT;
        createOrderReq.source = "api";
        Long orderId = CustomClient.getClient().createOrder(createOrderReq);
        addExecuteLog("创建卖出订单", new Object[]{amount, price});
        //执行订单
        String r = CustomClient.getClient().placeOrder(orderId);
        addExecuteLog("执行卖出订单", r);
        return r;
    }

    private void cancelLastBuyOrder() {
        Query query = new Query(Criteria.where("coin").is(aiCoinType).and("type").is(TradeRecord.TYPE_BUY));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        TradeRecord tradeRecord2 = BeanManager.getBean(MongoTemplate.class).findOne(query, TradeRecord.class);
        SubmitcancelResponse submitcancel = CustomClient.getClient().submitcancel(tradeRecord2.getBuyOrderId());
        if (submitcancel.getStatus().equals("ok")) {
            addExecuteLog("撤销上一个买单成功", submitcancel);
        }
    }

    private void addExecuteLog(String operation, Object value) {
        BeanManager.getBean(MongoTemplate.class).insert(new ExecuteLog(coinType, batchNum, new Date(), operation, value, tradeRecordId));
    }
}
