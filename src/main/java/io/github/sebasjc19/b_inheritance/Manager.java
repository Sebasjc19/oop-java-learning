package io.github.sebasjc19.b_inheritance;

import java.math.BigDecimal;

public class Manager extends Employee{

    private final BigDecimal fixedBonus;

    public Manager(String name, BigDecimal baseSalary, BigDecimal fixedBonus) {
        super(name, baseSalary);
        validateNoNegative(fixedBonus,"Fixed bonus");
        this.fixedBonus = fixedBonus;
    }

    @Override
    public BigDecimal calculatePay(){
        return super.calculatePay().add(fixedBonus);
    }
}
