package ca.setc.services;

import ca.setc.annotations.MethodAnno;
import ca.setc.annotations.ParameterAnno;
import ca.setc.annotations.ServiceAnno;

import java.util.LinkedList;
import java.util.List;

/**
 * Car Loan calculator service
 */
@ServiceAnno(name = "CAR-LOAN", securityLevel = 1, description = "Car Loan thing")
public final class CarLoanService {

    private static final int[] MONTHS = new int[]{36, 48, 60};

    private static final double DIVISOR = 1200.0;

    private CarLoanService(){}

    /**
     *Calculates the monthly payments
     *
     * @param principal
     * @param rate
     * @return payment
     */
    @MethodAnno(name = "carLoanCalculator", returnDescriptions = {"36month", "48month", "60month"})
    public static Double[] carLoad(
            @ParameterAnno(name = "principal")
            Double principal,
            @ParameterAnno(name = "rate")
            Double rate) {
        List<Double> payments = new LinkedList<Double>();
        double monthlyRate = rate / DIVISOR;

        for (int month : MONTHS) {
            payments.add((monthlyRate + (monthlyRate / (Math.pow(1 + monthlyRate, month) - 1))) * principal);
        }

        return payments.toArray(new Double[payments.size()]);
    }
}
