package io.github.sebasjc19.d_abstraction;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractionTest {

    @Test
    void standarShipmentCostTest(){
        Shipment shipment = new StandardShipment("CO-001",new BigDecimal("10"));
        assertEquals(new BigDecimal("35.00"), shipment.getCost());
    }

    @Test
    void lightExpressShipmentCostTest(){
        Shipment shipment = new ExpressShipment("CO-001",new BigDecimal("10"));
        assertEquals(new BigDecimal("70.00"), shipment.getCost());
    }

    @Test
    void heavyExpressShipmentCostTest(){
        Shipment shipment = new ExpressShipment("CO-001",new BigDecimal("25"));
        assertEquals(new BigDecimal("170.00"), shipment.getCost());
    }

    @Test
    void internationalShipmentCostTest(){
        Shipment shipment = new InternationalShipment("CO-001",new BigDecimal("10"),"US");
        assertEquals(new BigDecimal("100.00"), shipment.getCost());
    }

    @Test
    void expressShipmentLightWeightValidationTest(){
        Shipment shipment = new ExpressShipment("CO-001", new BigDecimal("20"));
        assertEquals(new BigDecimal("130.00"), shipment.getCost());
    }

    @Test
    void expressShipmentHeavyWeightValidationTest(){
        Shipment shipment = new ExpressShipment("CO-001", new BigDecimal("20.01"));
        assertEquals(new BigDecimal("140.06"), shipment.getCost());
    }

    @Test
    void costRoundTest(){
        Shipment shipment = new StandardShipment("CO-001", new BigDecimal("1.333"));
        assertEquals(new BigDecimal("9.00"), shipment.getCost());
    }
}
