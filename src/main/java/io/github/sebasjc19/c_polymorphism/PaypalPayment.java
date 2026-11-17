package io.github.sebasjc19.c_polymorphism;

import java.math.BigDecimal;

public class PaypalPayment extends Payment{

    @Override
    public String process(BigDecimal amount) {
        return "Paypal: " + amount;
    }

}
