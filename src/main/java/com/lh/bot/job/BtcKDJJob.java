package com.lh.bot.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.bot.base.BaseQuartzJob;
import com.lh.bot.base.MacdMonitorInstance;
import com.lh.bot.constants.GlobalConstants;
import com.lh.bot.util.DateUtil;
import com.lh.bot.util.DingTalkUtil;
import com.lh.bot.util.KDJUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BtcKDJJob extends BaseQuartzJob {

    private Logger logger = LoggerFactory.getLogger(BtcKDJJob.class);


    private MacdMonitorInstance macdMonitorInstance =  new MacdMonitorInstance();

    @Override
    public void init() {
       /* while (true) {
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.MINUTE) % 5 == 0 && calendar.get(Calendar.SECOND) == 10) {
                break;
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        setInitialDelay(0L);
        setPeriod(300 * 1000L);
    }

    @Override
    public void run() throws Exception {
        /*logger.info(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "启动kdj任务");
        macdMonitorInstance.setTag("okcoinfuturesbtcquarterusd");
        String dataStr = macdMonitorInstance.download(5);
        JSONArray jsonArray = JSONObject.parseArray(JSONObject.parseObject(dataStr).get("data").toString());
        Double [] begin = new Double[jsonArray.size()];
        Double [] high = new Double[jsonArray.size()];
        Double [] low = new Double[jsonArray.size()];
        Double [] close = new Double[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i ++) {
            JSONArray jsonArray1 = JSONObject.parseArray(jsonArray.get(i).toString());
            begin[i] = Double.parseDouble(jsonArray1.get(1).toString());
            high[i] = Double.parseDouble(jsonArray1.get(2).toString());
            low[i] = Double.parseDouble(jsonArray1.get(3).toString());
            close[i] = Double.parseDouble(jsonArray1.get(4).toString());
        }

        double[][] KDJ = new KDJUtil().getKDJList(close, low, high);

        double lastK = KDJ[0][KDJ[0].length - 2];//K
        double lastD = KDJ[1][KDJ[1].length - 2];//D
        double lastJ = KDJ[2][KDJ[2].length - 2];//J

        double currentK = KDJ[0][KDJ[0].length - 1];//K
        double currentD = KDJ[1][KDJ[1].length - 1];//D
        double currentJ = KDJ[2][KDJ[2].length - 1];//J

        //死叉
        if (lastJ > lastK && lastJ > lastD && currentJ < currentK &&  currentJ < currentD) {
            DingTalkUtil.sendDingTalkMsg(GlobalConstants.DING_TALK_ACCESS_TOKEN_KDJ,  String.format("死叉，当前K %.2f D %.2f J %.2f", currentK, currentD, currentJ));
        }
        //金叉J
        if (lastJ < lastK && lastJ < lastD && currentJ > currentK &&  currentJ > currentD) {
            DingTalkUtil.sendDingTalkMsg(GlobalConstants.DING_TALK_ACCESS_TOKEN_KDJ,  String.format("金叉，当前K %.2f D %.2f J %.2f", currentK, currentD, currentJ));
        }*/
    }

}