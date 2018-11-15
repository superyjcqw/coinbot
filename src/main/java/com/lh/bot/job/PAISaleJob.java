package com.lh.bot.job;

import com.lh.bot.base.BaseQuartzJob;
import com.lh.bot.base.BuyInstance;
import com.lh.bot.base.SaleInstance;
import org.springframework.stereotype.Service;

/**
 *  每秒轮询是否满足购买条件
 * Author: liuhuan
 * Date:  2018/8/8 下午7:53
 */
@Service
public class PAISaleJob extends BaseQuartzJob {

    @Override
    public void init() {
//        setInitialDelay(2000L);
//        setPeriod(30 * 1000L);
    }

    @Override
    public void run() throws Exception {
//        SaleInstance saleInstance = new SaleInstance();
//        saleInstance.setCoinType("paiusdt");
//        saleInstance.setAiCoinType("huobipropaiusdt");
//        saleInstance.setCoin("pai");
//        saleInstance.sale();
    }

}
