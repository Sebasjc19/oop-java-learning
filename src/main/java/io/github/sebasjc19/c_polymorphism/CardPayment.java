package io.github.sebasjc19.c_polymorphism;

import java.math.BigDecimal;

public class CardPayment extends Payment{

    @Override
    public String process(BigDecimal amount) {
        return "Card: " + amount;
    }

    public String refundToCard(BigDecimal amount){
        return "Refund to card: " + amount;
    }

}
