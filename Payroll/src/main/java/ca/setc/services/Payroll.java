package ca.setc.services;

import ca.setc.annotations.MethodAnno;
import ca.setc.annotations.ParameterAnno;
import ca.setc.annotations.ServiceAnno;

/**
 * Payroll calculator service
 */
@ServiceAnno(name = "PAYROLL", securityLevel = 1, description = "Pay roll thing")
public final class Payroll {

    private Payroll(){}

    private static final double WORK_WEEK_HOURS = 40.0;
    private static final double OVERTIME_MULTIPLIER = 1.5;
    private static final double SEASONAL_BASE_PAY = 150.0;
    private static final int WEEKS_IN_YEAR = 52;

    /**
     * Calculates the paycheck amount
     *
     * @param employeeType
     * @param hoursWorked
     * @param rate
     * @param seasonal
     * @param contractWeeks
     * @return paycheck amount
     */
    @MethodAnno(name = "payCheckMaker", returnDescriptions = {"36 month", "48 month", "60 month"})
    public static Double payCheckMaker(
            @ParameterAnno(name = "type")
            String employeeType,
            @ParameterAnno(name = "hours")
            Double hoursWorked,
            @ParameterAnno(name = "rate")
            Double rate,
            @ParameterAnno(required = false, name = "seasonal")
            Double seasonal,
            @ParameterAnno(required = false, name = "contract")
            Integer contractWeeks) {
        if ("HOUR".equals(employeeType)) {
            if (hoursWorked <= WORK_WEEK_HOURS) {
                return hoursWorked * rate;
            } else {
                return (WORK_WEEK_HOURS * rate) + ((hoursWorked - WORK_WEEK_HOURS) * (rate * OVERTIME_MULTIPLIER));
            }
        } else if ("FULL".equals(employeeType)) {
            return rate / WEEKS_IN_YEAR;
        } else if ("SEASON".equals(employeeType)) {
            if (hoursWorked <= WORK_WEEK_HOURS) {
                return seasonal * rate;
            } else {
                return (rate * seasonal) + SEASONAL_BASE_PAY;
            }
        } else if ("CONTRACT".equals(employeeType)) {
            return rate / contractWeeks;
        }

        return 0.0;
    }
}
