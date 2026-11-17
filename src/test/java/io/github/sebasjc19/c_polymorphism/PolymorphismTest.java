package io.github.sebasjc19.c_polymorphism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class PolymorphismTest {

    @Test
    void cardPayment(){
        Payment payment = new CardPayment();
        assertEquals(
                "Card: 3100",
                payment.process(new BigDecimal("3100"))
        );
    }

    @Test
    void cardRefund(){
        Payment payment = new CardPayment();
        if (payment instanceof CardPayment card){
            assertEquals(
                    "Refund to card: 3100",
                    card.refundToCard(new BigDecimal("3100"))
            );
        } else {
            fail("Expected a CardPayment");
        }
    }

    @Test
    void paypalPayment(){
        Payment payment = new PaypalPayment();
        assertEquals(
                "Paypal: 5000",
                payment.process(new BigDecimal("5000"))
        );
    }

    @Test
    void genericPayment(){
        Payment payment = new Payment();
        assertEquals(
                "Generic payment: 500",
                payment.process(new BigDecimal("500"))
        );
    }

    @Test
    void checkoutPayment(){
        Payment payment = new PaypalPayment();
        assertEquals(
                "Paypal: 3200",
                Payment.checkout(payment,new BigDecimal("3200"))
        );
    }
}
