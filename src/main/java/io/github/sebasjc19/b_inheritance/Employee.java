package io.github.sebasjc19.b_inheritance;

import java.math.BigDecimal;

public class Employee {

    private final String name;
    private final BigDecimal baseSalary;

    public Employee (String name, BigDecimal baseSalary){
        if (name == null || name.isBlank()){
            throw new RuntimeException("A valid name is needed");
        }
        validateNoNegative(baseSalary,"Base salary");
        this.name = name;
        this.baseSalary = baseSalary;
    }

    public BigDecimal calculatePay(){
        return this.baseSalary;
    }

    public String describe(){
        return "Name: "+name+"\nPay: "+calculatePay();
    }

    protected static void validateNoNegative(BigDecimal value, String field){
        if (value == null){
            throw new RuntimeException(field+" can't be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0){
            throw new RuntimeException(field+"Value can't be negative");
        }
    }

}
