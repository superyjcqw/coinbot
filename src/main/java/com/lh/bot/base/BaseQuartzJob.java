package com.lh.bot.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 基础监控器
 * Date:  17/7/12 下午5:58
 */
public abstract class BaseQuartzJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseQuartzJob.class);

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);

    private ScheduledFuture<?> sendFuture;

    private long initialDelay = 3000L;

    private long period = 10000L;

    public void start() {
        init();
        sendFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                run();
            } catch (Exception e) {
                LOGGER.error("Unexpected error occur at run(), cause: " + e.getMessage(), e);
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    public abstract void init();

    public abstract void run() throws Exception;

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public void destroy() {
        try {
            sendFuture.cancel(true);
        } catch (Throwable t) {
            LOGGER.error("Unexpected error occur at cancel sender timer, cause: " + t.getMessage(), t);
        }
    }

}
