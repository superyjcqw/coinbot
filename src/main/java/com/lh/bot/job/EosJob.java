package com.lh.bot.job;

import com.lh.bot.base.BaseQuartzJob;
import com.lh.bot.base.BigMacdMonitorSimpleInstance;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class EosJob extends BaseQuartzJob {

    private BigMacdMonitorSimpleInstance macdMonitorInstance =  new BigMacdMonitorSimpleInstance();

    @Override
    public void init() {
         while (true) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 2);
            if (calendar.get(Calendar.MINUTE) % 15 == 0 && calendar.get(Calendar.SECOND) == 0) {
                break;
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        setInitialDelay(0L);
        setPeriod(15 * 60 * 1000L);
    }

    @Override
    public void run() throws Exception {
        macdMonitorInstance.setTag("huobiproeosusdt");
        macdMonitorInstance.monitor();
    }

}