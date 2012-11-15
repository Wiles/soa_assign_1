package ca.setc.services;

import ca.setc.annotations.MethodAnno;
import ca.setc.annotations.ParameterAnno;
import ca.setc.annotations.ServiceAnno;

import java.util.LinkedList;
import java.util.List;

@ServiceAnno(name = "CAR-LOAN", securityLevel = 1, description = "Car Loan thing")
public class CarLoan {

    private static int[] months = new int[]{36, 48, 60};

    @MethodAnno(name = "carLoanCalculator", returnDescriptions = {"Payment"})
    public static Double[] carLoad(
            @ParameterAnno(name = "principal")
            Double principal,
            @ParameterAnno(name = "rate")
            Double rate) {
        List<Double> payments = new LinkedList<Double>();
        double monthlyRate = rate / 1200.0;

        for (int month : months) {
            payments.add((monthlyRate + (monthlyRate / (Math.pow(1 + monthlyRate, month) - 1))) * principal);
        }

        return payments.toArray(new Double[payments.size()]);
    }
}
