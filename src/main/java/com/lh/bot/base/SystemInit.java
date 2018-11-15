package com.lh.bot.base;

import com.lh.bot.job.HTJob;
import com.lh.bot.job.PAIBuyJob;
import com.lh.bot.job.PAISaleJob;
import com.lh.bot.util.BeanManager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * 系统初始化
 * Date:  17/7/12 下午8:01
 */
public class SystemInit {

    Map<String, BaseQuartzJob> monitorMap = BeanManager.getApplicationContext().getBeansOfType(BaseQuartzJob.class);

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        monitorMap.values().forEach(monitor -> {
            monitor.start();
        });
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        monitorMap.values().forEach(monitor ->
            monitor.destroy()
        );
    }

}

