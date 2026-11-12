package io.github.sebasjc19.b_inheritance;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


public class InheritanceTest {

    @Test
    void initializationTest(){
        Employee developer = new Developer("Sebastian",
                new BigDecimal("1000"),
                10,
                new BigDecimal("30")
        );
        assertTrue(developer.describe().contains("Sebastian"));
    }

    @Test
    void badInitializationTest(){
        assertThrows(
                RuntimeException.class,
                () -> new Manager("Sebastian",new BigDecimal("0"),new BigDecimal("-100"))
        );
    }

    @Test
    void baseEmployeeSalaryTest(){
        Employee employee = new Employee("Sebastian", new BigDecimal("850"));
        assertEquals(0, employee.calculatePay().compareTo(new BigDecimal("850")));
    }

    @Test
    void developerSalaryTest(){
        Employee developer = new Developer("Sebastian", new BigDecimal("1500"),10,new BigDecimal("40"));
        assertEquals(0,developer.calculatePay().compareTo(new BigDecimal("1900")));
    }

    @Test
    void managerSalaryTest(){
        Employee manager = new Manager("Sebastian",new BigDecimal("1000"),new BigDecimal("250"));
        assertEquals(0,manager.calculatePay().compareTo(new BigDecimal("1250")));
    }



}
