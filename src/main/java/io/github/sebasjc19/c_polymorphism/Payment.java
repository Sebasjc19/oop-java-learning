package io.github.sebasjc19.c_polymorphism;

import java.math.BigDecimal;

public class Payment {

    public String process(BigDecimal amount){
        throw new RuntimeException("Method not valid");
    }

    public static String checkout(Payment payment, BigDecimal total){
        return payment.process(total);
    }
}
