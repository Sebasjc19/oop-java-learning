package io.github.sebasjc19.d_abstraction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Shipment {
    private final String trackingCode;
    private final BigDecimal weightKG;

    protected Shipment(String trackingCode, BigDecimal weightKG) {
        if (trackingCode == null || trackingCode.isBlank()) {
            throw new RuntimeException("Tracking code is needed");
        }
        if (weightKG == null || weightKG.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Weight must be greater than 0");
        }
        this.trackingCode = trackingCode;
        this.weightKG = weightKG;
    }

    protected abstract BigDecimal calculateCost();

    public final BigDecimal getCost(){
        return calculateCost().setScale(2, RoundingMode.HALF_UP);
    }

    public final String summary() {
        return this.trackingCode + " | " + weightKG + " kg | " + "Cost: " + getCost();
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public BigDecimal getWeightKG() {
        return weightKG;
    }
}
