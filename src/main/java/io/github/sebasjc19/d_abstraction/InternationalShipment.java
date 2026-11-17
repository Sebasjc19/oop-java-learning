package io.github.sebasjc19.d_abstraction;

import java.math.BigDecimal;

public class InternationalShipment extends Shipment {
    private static final BigDecimal BASE_FEE = new BigDecimal("20");
    private static final BigDecimal FEE_PER_KG = new BigDecimal("8");

    private final String countryCode;

    public InternationalShipment(String trackingCode, BigDecimal weightKG, String countryCode) {
        super(trackingCode, weightKG);
        if (countryCode == null || countryCode.isBlank()){
            throw new RuntimeException("Country code is needed");
        }
        if (!countryCode.matches("^[A-Z]{2}$")){
            throw new RuntimeException("Country code not valid");
        }
        this.countryCode = countryCode;
    }

    @Override
    protected BigDecimal calculateCost() {
        return BASE_FEE.add(FEE_PER_KG.multiply(getWeightKG()));
    }

    public String getCountryCode() {
        return countryCode;
    }
}
