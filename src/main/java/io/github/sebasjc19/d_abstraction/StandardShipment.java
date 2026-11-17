package io.github.sebasjc19.d_abstraction;

import java.math.BigDecimal;

public class StandardShipment extends DomesticShipment {
    public StandardShipment(String trackingCode, BigDecimal weightKG) {
        super(trackingCode, weightKG);
    }

    @Override
    protected BigDecimal calculateCost() {
        return domesticRate();
    }
}
