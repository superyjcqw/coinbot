package com.lh.bot.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lh.bot.entity.ExecuteLog;
import com.lh.bot.entity.TradeRecord;
import com.lh.bot.platform.huobi.api.CustomClient;
import com.lh.bot.platform.huobi.request.CreateOrderRequest;
import com.lh.bot.platform.huobi.request.IntrustOrdersDetailRequest;
import com.lh.bot.platform.huobi.response.*;
import com.lh.bot.util.BeanManager;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class BuyInstance {

    private static String batchNum;

    private static ObjectId tradeRecordId;
    
    private String coinType;
    
    private String aiCoinType;

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

    public void buy() {
        //是否满足购买条件
        //是否有挂单
        //是否有余额
        //无挂单时加价挂单
        //有挂单时，超过10秒未成交，取消重新加价挂单
        //挂单成功更新购买状态
        batchNum = UUID.randomUUID().toString();
        Query query = new Query(Criteria.where("coin").is(aiCoinType));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        TradeRecord tradeRecord = BeanManager.getBean(MongoTemplate.class).findOne(query, TradeRecord.class);
        if (tradeRecord.getType().equals(TradeRecord.TYPE_BUY) && tradeRecord.getStatus().equals(TradeRecord.STATUS_INIT)) {
            tradeRecordId = tradeRecord.getId();
            addExecuteLog(tradeRecord.getBuyOrderId() == null ? "符合买入条件" : "继续购买", tradeRecord.getPrice());
            cancelLastSaleOrder();
            //查询当前是不有挂单，有的话取消当前挂单重新加价挂单
            if (tradeRecord.getBuyOrderId() != null) {
                OrdersDetailResponse ordersDetail = CustomClient.getClient().ordersDetail(tradeRecord.getBuyOrderId());
                JSONObject orderJson = JSONObject.parseObject(JSON.toJSONString(ordersDetail.getData()));
                String state = orderJson.get("state").toString();
                if (state.equals(IntrustOrdersDetailRequest.OrderStates.FILLED)) {
                    addExecuteLog("已成交", JSON.toJSONString(ordersDetail));
                    //更新状态
                    BeanManager.getBean(MongoTemplate.class).updateFirst(Query.query(Criteria.where("_id").is(tradeRecord.getId())), Update.update("status", TradeRecord.STATUS_DONE).set("doneTime", new Date()), TradeRecord.class);
                    return;
                } else if (state.equals(IntrustOrdersDetailRequest.OrderStates.SUBMITTED)
                        || state.equals(IntrustOrdersDetailRequest.OrderStates.PARTIAL_FILLED)) {
                    try {
                        cancelOrder(tradeRecord.getBuyOrderId(), state);
                    } catch (Exception e) {
                        //取消异常，可能已经成交
                        addExecuteLog("取消订单异常，可能已经成交", JSON.toJSONString(ordersDetail));
                        BeanManager.getBean(MongoTemplate.class).updateFirst(Query.query(Criteria.where("_id").is(tradeRecord.getId())), Update.update("status", TradeRecord.STATUS_DONE).set("doneTime", new Date()), TradeRecord.class);
                        return;
                    }
                }
            }

            //查余额
            AccountsResponse accounts = CustomClient.getClient().accounts();
            List<Accounts> list = (List<Accounts>) accounts.getData();
            String accountId = String.valueOf(list.get(0).getId());
            BigDecimal balance = CustomClient.getBalance(CustomClient.getClient(), accountId, "usdt");
            //小于万分之一btc时
            if (balance.compareTo(new BigDecimal("0.1")) < 0) {
                addExecuteLog("检查到余额不足，退出购买流程", balance);
            } else {
                addExecuteLog("检查到有可用余额", balance);

                //挂单前准备
                //获取当前交易深度
                Depth tick = CustomClient.getDepth(CustomClient.getClient(), coinType);
                //找出最高买单加一个最小单位
//                BigDecimal buyPrice = new BigDecimal(String.valueOf(tick.getAsks().get(0).get(0))).setScale(scale);
                BigDecimal buyPrice = new BigDecimal(String.valueOf(tick.getBids().get(0).get(0))).add(new BigDecimal("0.0001")).setScale(scale);
//                BigDecimal buyPrice = (new BigDecimal(String.valueOf(tick.getBids().get(0).get(0))).add(new BigDecimal(String.valueOf(tick.getAsks().get(0).get(0))))).divide(new BigDecimal("2"), scale, BigDecimal.ROUND_DOWN);

                //创建并执行订单
                BigDecimal amount = balance.divide(buyPrice, 2, BigDecimal.ROUND_DOWN);
                String orderId = createOrder(accountId, amount, buyPrice);
                //更新最后一次挂单ID
                BeanManager.getBean(MongoTemplate.class).updateFirst(Query.query(Criteria.where("_id").is(tradeRecord.getId())), Update.update("buyOrderId", orderId), TradeRecord.class);
            }
        }
    }



    private void cancelOrder(String orderId, String state) {
        addExecuteLog("检查到"+ (state.equals("submitted") ? "已提交" : "部分提交") +"买入订单", "");
        SubmitcancelResponse submitcancel = CustomClient.getClient().submitcancel(orderId);
        if (submitcancel.getStatus().equals("ok")) {
            addExecuteLog("取消" + (state.equals("submitted") ? "已提交" : "部分提交") + "买入订单", JSON.toJSONString(submitcancel));
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
        createOrderReq.type = CreateOrderRequest.OrderType.BUY_LIMIT;
        createOrderReq.source = "api";
        Long orderId = CustomClient.getClient().createOrder(createOrderReq);
        addExecuteLog("创建买入订单", new Object[]{amount, price});
        //执行订单
        String r = CustomClient.getClient().placeOrder(orderId);
        addExecuteLog("执行买入订单", r);
        return r;
    }

    private void cancelLastSaleOrder() {
        Query query = new Query(Criteria.where("coin").is(aiCoinType).and("type").is(TradeRecord.TYPE_SALE));
        query.with(new Sort(Sort.Direction.DESC, "_id"));
        TradeRecord tradeRecord = BeanManager.getBean(MongoTemplate.class).findOne(query, TradeRecord.class);
        SubmitcancelResponse submitcancel = CustomClient.getClient().submitcancel(tradeRecord.getSaleOrderId());
        if (submitcancel.getStatus().equals("ok")) {
            addExecuteLog("撤销上一个卖单成功", submitcancel);
        }
    }

    private void addExecuteLog(String operation, Object value) {
        BeanManager.getBean(MongoTemplate.class).insert(new ExecuteLog(coinType, batchNum, new Date(), operation, value, tradeRecordId));
    }

}
