package io.github.sebasjc19.d_abstraction;

import java.math.BigDecimal;

public abstract class DomesticShipment extends Shipment {
    private static final BigDecimal BASE_FEE = new BigDecimal("5");
    private static final BigDecimal FEE_PER_KG = new BigDecimal("3");

    protected DomesticShipment(String trackingCode, BigDecimal weightKG) {
        super(trackingCode, weightKG);
    }

    protected BigDecimal domesticRate(){
        return BASE_FEE.add(FEE_PER_KG.multiply(getWeightKG()));
    }
}
