package com.codingforhappy.dao;

import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Range;

@SuppressWarnings("UnstableApiUsage")
public class PricingMeter {
    private static final ImmutableRangeMap<Double, PriceRule> MAP = new ImmutableRangeMap.Builder<Double, PriceRule>()
            .put(Range.closedOpen(0.0, 10.0), new PriceRule(5, 1, 0)) // [0.0, 10.0)
            .put(Range.open(10.0, 20.0), new PriceRule(10, 1.5, 0)) //(10.0, 20.0)
            .put(Range.open(20.0, 40.0), new PriceRule(20, 2, 10)) //(20.0, 40.0)
            .put(Range.openClosed(40.0, 100.0), new PriceRule(20, 2.5, 20)) //(40.0, 100.0)
            .build();

    private PricingMeter() {
    }

    /**
     * @param distance 距离为0.0到100.0km，距离超出请自行与司机协商车费
     * @return 参考收费
     * @throws Exception 距离错误
     */
    public static double calculatePrice(double distance) throws Exception {
        PriceRule rule = MAP.get(distance);
        if (rule == null)
            throw new Exception("wrong distance");
        return rule.calculate(distance);
    }
}


class PriceRule {
    private double startPrice; //起步价
    private double perKm; //每公里价格
    private double extra; //额外收费

    PriceRule(double startPrice, double perKm, double extra) {
        this.startPrice = startPrice;
        this.perKm = perKm;
        this.extra = extra;
    }

    double calculate(double distance) {
        return startPrice + perKm * distance + extra;
    }
}