package io.github.sebasjc19.c_polymorphism;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class PolymorphismTest {

    @Test
    void cardPayment(){
        Payment payment = new CardPayment();
        assertTrue(
                payment.process(new BigDecimal("3100")).contains("Card:")
        );
    }

    @Test
    void cardRefund(){
        CardPayment payment = new CardPayment();
        CardPayment cardPayment = payment;
        assertTrue(
                cardPayment.refundToCard(new BigDecimal("3100")).contains("Refund")
        );
    }

    @Test
    void paypalPayment(){
        Payment payment = new PaypalPayment();
        assertTrue(
                payment.process(new BigDecimal("5000")).contains("Paypal:")
        );
    }

    @Test
    void paymentError(){
        Payment payment = new Payment();
        assertThrows(Exception.class,
                () -> payment.process(new BigDecimal("9000"))
        );
    }

    @Test
    void checkoutPayment(){
        Payment payment = new PaypalPayment();

        assertTrue(
                Payment.checkout(payment,new BigDecimal("3200")).contains("Paypal:")
        );
    }
}
