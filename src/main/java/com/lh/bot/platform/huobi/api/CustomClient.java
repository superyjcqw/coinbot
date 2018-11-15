package com.lh.bot.platform.huobi.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.bot.platform.huobi.request.DepthRequest;
import com.lh.bot.platform.huobi.response.BalanceResponse;
import com.lh.bot.platform.huobi.response.Depth;
import com.lh.bot.platform.huobi.response.DepthResponse;

import java.math.BigDecimal;

import static com.alibaba.fastjson.JSON.parseArray;

public class CustomClient {

    static final String API_KEY = "e7d101a2-********-36cb4b91-*****";
    static final String API_SECRET = "1eafb65b-*******-d5490557-*****";

    static ApiClient client;

    public synchronized static ApiClient getClient() {
        if (client == null) {
            client = new ApiClient(API_KEY, API_SECRET);
        }
        return client;
    }

    public static BigDecimal getBalance(ApiClient client, String accountId, String type) {
        BalanceResponse balanceResponse = client.balance(accountId);
        JSONObject jsonObjectData = JSONObject.parseObject(JSONObject.toJSONString(balanceResponse.getData()));
        JSONArray listData = parseArray(jsonObjectData.get("list").toString());
        BigDecimal balance = new BigDecimal(0);
        for (int i = 0; i < listData.size(); i ++) {
            JSONObject jsonObjectBalance = JSONObject.parseObject(listData.get(i).toString());
            if (jsonObjectBalance.get("currency").equals(type) && jsonObjectBalance.get("type").equals("trade")) {//可用余额
                balance = new BigDecimal(jsonObjectBalance.get("balance").toString());
                break;
            }
        }
        return balance;
    }

    public static Depth getDepth(ApiClient client, String type) {
        DepthRequest depthRequest = new DepthRequest();
        depthRequest.setSymbol(type);
        depthRequest.setType("step0");
        DepthResponse depthResponse = client.depth(depthRequest);
        return depthResponse.getTick();
    }
}
