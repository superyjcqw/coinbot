package com.lh.bot.job;

import com.lh.bot.base.BaseQuartzJob;
import com.lh.bot.base.MacdMonitorInstance;
import org.springframework.stereotype.Service;

@Service
public class AdaJob extends BaseQuartzJob {

    private MacdMonitorInstance macdMonitorInstance =  new MacdMonitorInstance();

    @Override
    public void init() {
//        setInitialDelay(2000L);
//        setPeriod(20 * 1000L);
    }

    @Override
    public void run() throws Exception {
//        macdMonitorInstance.setTag("huobiproadabtc");
//        macdMonitorInstance.monitor();
    }
}