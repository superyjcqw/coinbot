package com.lh.bot.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.bot.base.BaseQuartzJob;
import com.lh.bot.entity.DataMinute;
import com.lh.bot.util.DateUtil;
import com.lh.bot.util.HttpUtil;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Date:  17/7/12 下午8:05
 */
@Service
public class SpiderJob extends BaseQuartzJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpiderJob.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void init() {
//        setInitialDelay(2000L);
//        setPeriod(600 * 1000L);
    }

    @Override
    public void run() throws Exception {
//        download("eos");
//        download("bts");
//        query("eos");
//        query("bts");
    }


    public void download(String name) {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost("https://trans.bitkk.com/markets/klineLastData");
            List<org.apache.http.NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("needTickers", "1"));
            nvps.add(new BasicNameValuePair("symbol", name + "qc"));
            nvps.add(new BasicNameValuePair("type", "1day"));
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.DAY_OF_YEAR, -200);
            nvps.add(new BasicNameValuePair("since", ca.getTimeInMillis() + ""));
            post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            response = HttpUtil.getHttpClient().execute(post);
            String responseText = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSONObject.parseObject(responseText);
            JSONObject jsonDatas = JSONObject.parseObject(jsonObject.get("datas").toString());
            JSONArray jsonArray = JSONObject.parseArray(jsonDatas.get("data").toString());
            jsonArray.forEach(d ->{
                        JSONArray data = JSONObject.parseArray(d.toString());
//                        System.out.println(data);
                        Long time = Long.parseLong(data.get(0).toString());
                        BigDecimal beginPrice = new BigDecimal(data.get(1).toString());
                        BigDecimal endPrice = new BigDecimal(data.get(4).toString());
                        BigDecimal topPrice = new BigDecimal(data.get(2).toString());
                        BigDecimal bottomPrice = new BigDecimal(data.get(3).toString());
                        BigDecimal changeRate = endPrice.subtract(beginPrice).multiply(new BigDecimal(100)).divide(beginPrice,2,  BigDecimal.ROUND_HALF_EVEN);
//                        System.out.println(changeRate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(time);
                        DataMinute dataMinute =  new DataMinute(name, calendar.getTime(), beginPrice, endPrice, topPrice, bottomPrice, changeRate);
                        mongoTemplate.insert(dataMinute);
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HttpUtil.closeQuietly(response);
        }
    }


    public void query(String name) {
        Query query = Query.query(Criteria.where("name").is(name));
        query.with(new Sort(Sort.Direction.ASC, "_id"));
        List<DataMinute> list =  mongoTemplate.find(query, DataMinute.class);
        int size = 0;
        BigDecimal rate = new BigDecimal(0);
        boolean buy = false;
        for (DataMinute data : list) {
            if (data.getChangeRate().compareTo(new BigDecimal(0)) == -1) {
                size = 0;
                rate = new BigDecimal(0);
                buy = false;
            } else {
                size ++;
                rate = data.getChangeRate().add(rate);
                if (rate.compareTo(new BigDecimal(2)) == 1) {
                    buy = true;
                }
            }
            if (size > 3) {
                System.out.println(size + "\t" + data.getChangeRate() + "\t" + rate + "\t" + DateUtil.format(data.getTime(), DateUtil.DATE_FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
                        + "\t" + (buy ? "--------" : ""));
            }
        }
    }
}
