package io.github.sebasjc19.d_abstraction;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractionTest {

    @Test
    void test(){
        Shipment shipment = new ExpressShipment("CO",new BigDecimal("10"));
        System.out.println(shipment.summary());
    }
}
