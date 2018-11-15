package com.lh.bot.job;

public class Temp {

    public static void 超短线复利 () {
        double init = 200000;
        double rate = 0.02;
        double result = init;
        for (int i = 0; i < 20; i ++) {
            result = result * (1 + rate);
            System.out.println(result);
        }
    }

    public static void 抵押算法 () {
        double 总数量 = 10000;
        double 价格 = 1;
        double 强平价 = 1.13;
        double 保证金比例 = 1.8;
        double 借款;
        double 可担保量 = 总数量;
        double 总金额 = 总数量 * 价格;
        while (可担保量 > 1) {
            借款 = 可担保量 / 保证金比例;
            可担保量 = 借款;
            总金额 += 借款;
        }
        System.out.println(总金额);
    }

    public static void main(String[] args) {
        超短线复利();
//        抵押算法();
    }
}
