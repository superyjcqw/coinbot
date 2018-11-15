package com.lh.bot;

import com.lh.bot.base.Application;
import com.lh.bot.entity.TradeRecord;
import com.lh.bot.util.DateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class Stat {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void stat() {
       /* {
            Date beginTime = DateUtil.buildTime(2018, 7, 1, 0, 0, 0, 0);
            Query query = new Query(Criteria.where*//*("type").is(TradeRecord.TYPE_SALE).and*//*("coin").is("huobipropaiusdt").and("createTime").gt(beginTime));
            query.with(new Sort(Sort.Direction.ASC, "_id"));
            List<TradeRecord> list = mongoTemplate.find(query, TradeRecord.class);
            double total = 0;
            int 总卖出次数 = 0;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).getType().equals(TradeRecord.TYPE_SALE) && list.get(i -1).getMemo().contains("30分钟红柱子变大") && list.get(i -1).getMemo().contains("1分钟红柱子变大")) {
                    total += list.get(i).getIncome();
                    总卖出次数 ++;
                    System.out.println(DateUtil.format(list.get(i).getCreateTime(), "yyyy-MM-dd HH:mm:ss") + "====huobipropaiusdt 总卖出次数:" + 总卖出次数 + "\t" + "本次收益:" + String.format("%.2f", list.get(i).getIncome() - 0.2) + "\t"  + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - 总卖出次数 * 0.2) + "%");
                }
            }
            System.out.println("huobipropaiusdt 总卖出次数:" + 总卖出次数 + "\t" + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - 总卖出次数 * 0.2) + "%");
        }

        {
            Date beginTime = DateUtil.buildTime(2018, 7, 1, 0, 0, 0, 0);
            Query query = new Query(Criteria.where*//*("type").is(TradeRecord.TYPE_SALE).and*//*("coin").is("huobiprohitbtc").and("createTime").gt(beginTime));
            query.with(new Sort(Sort.Direction.ASC, "_id"));
            List<TradeRecord> list = mongoTemplate.find(query, TradeRecord.class);
            double total = 0;
            int 总卖出次数 = 0;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).getType().equals(TradeRecord.TYPE_SALE) && list.get(i -1).getMemo().contains("30分钟红柱子变大") && list.get(i -1).getMemo().contains("1分钟红柱子变大")) {
                    total += list.get(i).getIncome();
                    总卖出次数 ++;
                    System.out.println(DateUtil.format(list.get(i).getCreateTime(), "yyyy-MM-dd HH:mm:ss") + "====huobiprohitbtc 总卖出次数:" + 总卖出次数 + "\t" + "本次收益:" + String.format("%.2f", list.get(i).getIncome() - 0.2) + "\t"  + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - 总卖出次数 * 0.2) + "%");
                }
            }
            System.out.println("huobiprohitbtc 总卖出次数:" + 总卖出次数 + "\t" + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - 总卖出次数 * 0.2) + "%");
        }*/

        {
            Date beginTime = DateUtil.buildTime(2018, 9, 1, 0, 0, 0, 0);
            Query query = new Query(Criteria.where/*("type").is(TradeRecord.TYPE_SALE).and*/("coin").is("huobiproeosusdt").and("createTime").gt(beginTime));
            query.with(new Sort(Sort.Direction.ASC, "_id"));
            List<TradeRecord> list = mongoTemplate.find(query, TradeRecord.class);
            double total = 0;
            int 总卖出次数 = 0;
            for (int i = 0; i < list.size(); i ++) {
                if (list.get(i).getType().equals(TradeRecord.TYPE_SALE) && list.get(i -1).getMemo().contains("30分钟红柱子变大")) {
                    total += list.get(i).getIncome();
                    总卖出次数 ++;
                    System.out.println(DateUtil.format(list.get(i).getCreateTime(), "yyyy-MM-dd HH:mm:ss") + "====huobiproeosusdt 总卖出次数:" + 总卖出次数 + "\t" + "本次收益:" + String.format("%.2f", list.get(i).getIncome() - 0.2) + "\t"  + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - 总卖出次数 * 0.2) + "%");
                }
            }
            System.out.println("huobiproeosusdt 总卖出次数:" + 总卖出次数 + "\t" + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - 总卖出次数 * 0.2) + "%");
        }

       /* {
            Query query = new Query(Criteria.where("type").is(TradeRecord.TYPE_SALE).and("coin").is("huobiprobtsusdt"));
            query.fields().include("income");
            List<TradeRecord> list = mongoTemplate.find(query, TradeRecord.class);
            double total = 0;
            for (TradeRecord tr : list) {
                total += tr.getIncome();
            }
            System.out.println("huobiprobtsusdt 总卖出次数:" + list.size() + "\t" + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - list.size() * 0.2) + "%");
        }

        {
            Query query = new Query(Criteria.where("type").is(TradeRecord.TYPE_SALE).and("coin").is("huobiproeosusdt"));
            query.fields().include("income");
            List<TradeRecord> list = mongoTemplate.find(query, TradeRecord.class);
            double total = 0;
            for (TradeRecord tr : list) {
                total += tr.getIncome();
            }
            System.out.println("huobiproeosusdt 总卖出次数:" + list.size() + "\t" + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - list.size() * 0.2) + "%");
        }

        {
            Query query = new Query(Criteria.where("type").is(TradeRecord.TYPE_SALE).and("coin").is("huobiproadabtc"));
            query.fields().include("income");
            List<TradeRecord> list = mongoTemplate.find(query, TradeRecord.class);
            double total = 0;
            for (TradeRecord tr : list) {
                total += tr.getIncome();
            }
            System.out.println("huobiproadabtc 总卖出次数:" + list.size() + "\t" + "总收益:" + String.format("%.2f", total) + "%" + "\t" + "除去手续费后收益:" + String.format("%.2f", total - list.size() * 0.2) + "%");
        }*/
    }

}
