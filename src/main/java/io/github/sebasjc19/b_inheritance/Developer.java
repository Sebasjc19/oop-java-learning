package io.github.sebasjc19.b_inheritance;

import java.math.BigDecimal;

public class Developer extends Employee{

    private final int overtime;
    private final BigDecimal overtimeFee;

    public Developer(String name, BigDecimal baseSalary, int overtime, BigDecimal overtimeFee) {
        super(name, baseSalary);
        if (overtime<0){
            throw new RuntimeException("Overtime can't be negative");
        }
        validateNoNegative(overtimeFee,"Overtime fee");
        this.overtime = overtime;
        this.overtimeFee = overtimeFee;
    }

    @Override
    public BigDecimal calculatePay(){
        return super.calculatePay().
                add(overtimeFee.multiply(BigDecimal.valueOf(overtime)));
    }




}
