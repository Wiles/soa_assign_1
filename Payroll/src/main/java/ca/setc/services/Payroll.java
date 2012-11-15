package ca.setc.services;

import ca.setc.annotations.MethodAnno;
import ca.setc.annotations.ParameterAnno;
import ca.setc.annotations.ServiceAnno;

@ServiceAnno(name = "PAYROLL", securityLevel = 1, description = "Pay roll thing")
public class Payroll {

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
            if (hoursWorked <= 40.0) {
                return hoursWorked * rate;
            } else {
                return (40.0 * rate) + ((hoursWorked - 40.0) * (rate * 1.50));
            }
        } else if ("FULL".equals(employeeType)) {
            return rate / 52;
        } else if ("SEASON".equals(employeeType)) {
            if (hoursWorked <= 40.0) {
                return seasonal * rate;
            } else {
                return (rate * seasonal) + 150.0;
            }
        } else if ("CONTRACT".equals(employeeType)) {
            return rate / contractWeeks;
        }

        return 0.0;
    }
}
