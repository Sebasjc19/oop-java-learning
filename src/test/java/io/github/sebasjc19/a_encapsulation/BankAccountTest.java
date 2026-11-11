package io.github.sebasjc19.a_encapsulation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {

    @Test
    void classInitialization(){
        BankAccount bankAccount = new BankAccount("Sebastian", new BigDecimal("1500"));
        assertAll(
                () -> assertEquals("Sebastian", bankAccount.getOwner()),
                () -> assertEquals(0, new BigDecimal("1500").compareTo(bankAccount.getBalance()))
        );
    }

    @Test
    void depositIncreasesBalance(){
        BankAccount bankAccount = new BankAccount("Sebastian", new BigDecimal("1500"));
        bankAccount.deposit(new BigDecimal("500"));
        assertEquals(0, new BigDecimal("2000").compareTo(bankAccount.getBalance()));
    }

    @Test
    void withdrawDecreasesBalance(){
        BankAccount bankAccount = new BankAccount("Sebastian", new BigDecimal("1500"));
        bankAccount.withdraw(new BigDecimal("500"));
        assertEquals(0, new BigDecimal("1000").compareTo(bankAccount.getBalance()));
    }

    @Test
    void initializationException(){
        assertThrows(RuntimeException.class,
                () -> new BankAccount("", new BigDecimal("-10"))
        );
    }

    @Test
    void depositException(){
        BankAccount bankAccount = new BankAccount("Sebastian", new BigDecimal("100"));
        assertThrows(RuntimeException.class,
                () -> bankAccount.deposit(new BigDecimal("-100"))
        );

    }

    @Test
    void withdrawException(){
        BankAccount bankAccount = new BankAccount("Sebastian", new BigDecimal("100"));
        assertThrows(RuntimeException.class,
                () -> bankAccount.withdraw(new BigDecimal("101"))
        );
    }


}
