package com.lh.bot.job;

import com.lh.bot.base.BaseQuartzJob;
import com.lh.bot.base.BuyInstance;
import org.springframework.stereotype.Service;


/**
 *  每秒轮询是否满足卖出条件
 * Author: liuhuan
 * Date:  2018/8/8 下午7:53
 */
@Service
public class EosBuyJob extends BaseQuartzJob {


    @Override
    public void init() {
        setInitialDelay(2000L);
        setPeriod(30 * 1000L);
    }

    @Override
    public void run() throws Exception {
        BuyInstance buyInstance = new BuyInstance();
        buyInstance.setCoinType("eosusdt");
        buyInstance.setAiCoinType("huobiproeosusdt");
        buyInstance.setScale(4);
        buyInstance.buy();
    }




}
