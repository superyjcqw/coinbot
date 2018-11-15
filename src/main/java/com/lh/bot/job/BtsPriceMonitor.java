package com.lh.bot.job;

import com.lh.bot.base.BaseQuartzJob;
import com.lh.bot.base.MacdMonitorInstance;
import com.lh.bot.constants.GlobalConstants;
import com.lh.bot.entity.PriceMonitor;
import com.lh.bot.util.DateUtil;
import com.lh.bot.util.DingTalkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BtsPriceMonitor extends BaseQuartzJob {

    @Autowired
    private MongoTemplate mongoTemplate;

    private MacdMonitorInstance macdMonitorInstance =  new MacdMonitorInstance();

    @Override
    public void init() {
        setInitialDelay(2000L);
        setPeriod(60 * 1000L);
    }

    @Override
    public void run() throws Exception {
        macdMonitorInstance.setTag("zbbtsqc");
        List<Double> list = macdMonitorInstance.getDataLit(1);
        Double price = list.get(list.size() - 1);
        PriceMonitor priceMonitor = mongoTemplate.findOne(Query.query(Criteria.where("name").is("bts")), PriceMonitor.class);
        if (price < priceMonitor.getPrice() && (priceMonitor.getTipCount() == null || priceMonitor.getTipCount() < 3)) {
            DingTalkUtil.sendDingTalkMsg(GlobalConstants.DING_TALK_ACCESS_TOKEN_KDJ, DateUtil.format(new Date(), DateUtil.DATE_FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND) + "\t" + price);
            mongoTemplate.updateFirst(Query.query(Criteria.where("name").is("bts")), new Update().inc("tipCount", 1), PriceMonitor.class);
        }
    }

}
