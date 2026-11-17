package io.github.sebasjc19.d_abstraction;

import java.math.BigDecimal;

public class ExpressShipment extends DomesticShipment {
    private static final BigDecimal HEAVY_THRESHOLD_KG = new BigDecimal("20");

    public ExpressShipment(String trackingCode, BigDecimal weightKG) {
        super(trackingCode, weightKG);
    }

    @Override
    protected BigDecimal calculateCost() {
        BigDecimal extraCharge = getWeightKG().compareTo(HEAVY_THRESHOLD_KG) > 0 ? BigDecimal.TEN : BigDecimal.ZERO;
        return domesticRate().multiply(BigDecimal.TWO).add(extraCharge);
    }
}
