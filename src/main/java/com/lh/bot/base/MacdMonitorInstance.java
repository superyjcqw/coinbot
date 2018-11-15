package com.lh.bot.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.bot.constants.GlobalConstants;
import com.lh.bot.entity.TradeRecord;
import com.lh.bot.job.PAIBuyJob;
import com.lh.bot.job.PAISaleJob;
import com.lh.bot.util.BeanManager;
import com.lh.bot.util.DingTalkUtil;
import com.lh.bot.util.HttpUtil;
import com.lh.bot.util.MACDUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MacdMonitorInstance {

    private Logger logger = LoggerFactory.getLogger(MacdMonitorInstance.class);

    public boolean buy = false;

    private double firstBuyPrice = 0;

    private String tag;

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * 下载数据，与host refer User-Agent有关
     * @param minutes 多少分钟段的
     * @return demo如下
     * {
    "count": "eyJpdiI6IkZXYmhSSE1jQkhtRHREQmp2R1pVU2c9PSIsInZhbHVlIjoiQ1wvYjhPb3VYTnc1YzlWb2JndVJVbGc9PSIsIm1hYyI6ImI0MjI4OGFjZDI0OWEzZWE0Y2FkZjk5ODc5NDI4MTA1MjcwZTM4YTNjMTRjY2Y2NmU2OWRhOTY1OGI4NGQ3MDUifQ==",
    "data": [
    [
    1530518400,
    0.00009,
    0.00009889,
    0.00005,
    0.0000675,
    24757653.786755066
    ]}
     */
    public String download(int minutes) {
        CloseableHttpResponse response = null;
        try {
            HttpGet get = new HttpGet("https://www.aicoin.net.cn/chart/api/data/period?symbol=" + tag + "&step=" + minutes * 60);
            get.setHeader("host", "www.aicoin.net.cn");
            get.setHeader("Accept", "*/*");
            get.setHeader("Referer", "https://www.aicoin.net.cn/chart/7DC673C2");
            get.setHeader("Accept-Encoding", "gzip, deflate, br");
            get.setHeader("Connection", "keep-alive");
            get.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
            response = HttpUtil.getHttpClient().execute(get);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HttpUtil.closeQuietly(response);
        }
        return null;
    }

    /**
     * 获取各时间段收盘数据
     * @param minutes 多少分钟段的
     * @return
     */
    public List<Double> getDataLit(int minutes) {
        String jsonStr =  download(minutes);
        JSONArray jsonArray = JSONObject.parseArray(JSONObject.parseObject(jsonStr).get("data").toString());
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i ++) {
            JSONArray jsonArray1 = JSONObject.parseArray(jsonArray.get(i).toString());
            list.add(Double.parseDouble(jsonArray1.get(jsonArray1.size() - 2).toString()));
        }
        return list;
    }

    public double[] getAnalyticMACD(List<Double> list) {
        double currentMACD = MACDUtil.getDefaultMACD(list);
        list.remove(list.size() - 1);
        double lastMACD = MACDUtil.getDefaultMACD(list);
        return new double[] {lastMACD, currentMACD};
    }

    public void monitor() {
        String A = null;
        String B = null;
        String C = null;
        String B1 = null;
        double price = 0;
//        String diff30;
//        String diff5;
        {
            List<Double> list = getDataLit(30);
            double[] analyticMACD  = getAnalyticMACD(list);
            if (analyticMACD[0] > 0 && analyticMACD[1] > analyticMACD[0]) {
                logger.info(tag + "30分钟红柱子变大");
                A = "30分钟红柱子变大";
//                diff30 = String.format("%.2f", (analyticMACD[1] - analyticMACD[0]) * 100 / analyticMACD[0]);

            }/* else if (analyticMACD[0] < 0 && analyticMACD[1] < 0 && analyticMACD[1] > analyticMACD[0] ) {
                logger.info(tag + "30分钟绿柱子变小");
                A = "30分钟绿柱子变小";
            }*/
        }
        {
            List<Double> list = getDataLit(5);
            double[] analyticMACD  = getAnalyticMACD(list);
            Double endPrice = list.get(list.size() -1);
            if (analyticMACD[0] > 0 && analyticMACD[1] > analyticMACD[0]) {
                logger.info(tag + "5分钟红柱子变大");
                B = "5分钟红柱子变大";
//                diff5 = String.format("%.2f", (analyticMACD[1] - analyticMACD[0]) * 100 / analyticMACD[0]);
            } else if (analyticMACD[1] < analyticMACD[0]) {
                logger.info(tag + "5分钟红柱子变小");
                B1 = "5分钟红柱子变小";
                price = endPrice;
            }
        }
        {
            List<Double> list = getDataLit(1);
            double[] analyticMACD  = getAnalyticMACD(list);
            Double endPrice = list.get(list.size() -1);
            if (analyticMACD[0] < 0 && analyticMACD[1] > 0) {
                logger.info(tag + "1分钟柱子拐点");
                C = "1分钟柱子拐点";
                price = endPrice;
            } else if (analyticMACD[0] > 0 && analyticMACD[1] > analyticMACD[0]) {
                logger.info(tag + "1分钟红柱子变大");
                C = "1分钟红柱子变大";
                price = endPrice;
            }
        }

        if (A != null && B != null && C != null) {
            String memo = A + "," + B + "," + C + ", " + price + "进";
            if (!buy) {
                firstBuyPrice = price;
                BeanManager.getBean(MongoTemplate.class).insert(new TradeRecord(new Date(), TradeRecord.TYPE_BUY, memo, tag, null, price, TradeRecord.STATUS_INIT));
                if (tag.equals("huobipropaibtc")) {
                    try {
                        new PAIBuyJob().run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            buy = true;
            DingTalkUtil.sendDingTalkMsg(GlobalConstants.DING_TALK_ACCESS_TOKEN_MACD,  tag + " " + memo);
        }
        if (buy && B1 != null) {
            buy = false;
            double income = (price - firstBuyPrice) * 100 / firstBuyPrice;
            String formatIncome = String.format("%.2f", income);
            String memo =  B1 + "," + price + "出" + "\t收益" + formatIncome + "%";
            BeanManager.getBean(MongoTemplate.class).insert(new TradeRecord(new Date(), TradeRecord.TYPE_SALE, memo, tag, Double.valueOf(formatIncome), price, TradeRecord.STATUS_INIT));
            DingTalkUtil.sendDingTalkMsg(GlobalConstants.DING_TALK_ACCESS_TOKEN_MACD,  tag + " " + memo);
            if (tag.equals("huobipropaibtc")) {
                try {
                    new PAISaleJob().run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
