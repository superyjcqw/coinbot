package com.lh.bot.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lh.bot.base.MacdMonitorInstance;

import java.util.ArrayList;
import java.util.List;

public class KDJUtil {

    public double RSV(double close, double lown, double highn) {
        return 100.0 * (close - lown) / (highn - lown);
    }

    public double lown(Double[] low, int start, int end) {
        double lown = low[start];
        for (int i = start + 1; i <= end; i++) {
            if (lown > low[i]) {
                lown = low[i];
            }
        }
        return lown;
    }

    public double highn(Double[] high, int start, int end) {
        double highn = high[start];
        for (int i = start + 1; i <= end; i++) {
            if (highn < high[i]) {
                highn = high[i];
            }
        }
        return highn;
    }

    public double[][] getKDJList(Double[] close, Double[] low, Double[] high) {
        int length = close.length;

        double[][] KDJ = new double[4][length];

        int start = 8;// start from index 8
        for (int index = 0; index < start; index++) {
            KDJ[0][index] = 50.0;
            KDJ[1][index] = 50.0;
            KDJ[2][index] = 50.0;
            KDJ[3][index] = 50.0;
        }

        for (int index = start; index < length; index++) {
            double lown = this.lown(low, index - 8, index);
            double highn = this.highn(high, index - 8, index);
            double RSV = this.RSV(close[index], lown, highn);
            KDJ[0][index] = (2.0 / 3.0) * KDJ[0][index - 1] + (1.0 / 3.0) * RSV;// K
            KDJ[1][index] = (2.0 / 3.0) * KDJ[1][index - 1] + (1.0 / 3.0) * KDJ[0][index];// D
            KDJ[2][index] = 3.0 * KDJ[0][index] - 2.0 * KDJ[1][index];// J
            KDJ[3][index] = RSV;
        }
        return KDJ;
    }

    public double[] getKDJ(double[] preKDJ, double close, double lown, double highn) {
        double[] KDJ = new double[4];
        double RSV = this.RSV(close, lown, highn);
        KDJ[0] = (2.0 / 3.0) * preKDJ[0] + (1.0 / 3.0) * RSV;// K
        KDJ[1] = (2.0 / 3.0) * preKDJ[1] + (1.0 / 3.0) * KDJ[0];// D
        KDJ[2] = 3.0 * KDJ[0] - 2.0 * KDJ[1];// J
        KDJ[3] = RSV;
        return KDJ;
    }


    /*public static void main(String[] args) {

        MacdMonitorInstance macdMonitorInstance = new MacdMonitorInstance();
        macdMonitorInstance.setTag("huobipropaibtc");
        String dataStr = macdMonitorInstance.download(5);

        JSONArray jsonArray = JSONObject.parseArray(JSONObject.parseObject(dataStr).get("data").toString());
        List<Double> list = new ArrayList<>();

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

        System.out.println(KDJ[0][KDJ[0].length - 2]);//K
        System.out.println(KDJ[1][KDJ[1].length - 2]);//D
        System.out.println(KDJ[2][KDJ[2].length - 2]);//J
        System.out.println(KDJ[3][KDJ[3].length - 2]);//RSV
    }*/
}
