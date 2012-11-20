package ca.setc.services;

import ca.setc.soa.SoaException;
import junit.framework.Assert;
import org.junit.Test;

public class PayrollTest {
    @Test
    public void payCheckMaker_shouldCalculatePay_whenFullTime() throws SoaException {
        double actual = Payroll.payCheckMaker("FULL", 39.0, 52000.0, 0.0,0);
        Assert.assertTrue("Expect 1000.0 +/- 0.1. Actual: " + actual, Math.abs(1000.0 - actual) < 0.1 );
    }

    @Test
    public void payCheckMaker_shouldCalculatePay_whenPartTime() throws SoaException {
        Double actual = Payroll.payCheckMaker("HOUR", 39.0, 10.0, 0.0,0);
        Assert.assertTrue("Expect 390.0 +/- 0.1. Actual: " + actual, Math.abs(390.0 - actual) < 0.1 );
    }

    @Test
    public void payCheckMaker_shouldCalculatePay_whenPartTimeWithOvertime() throws SoaException {
        Double actual = Payroll.payCheckMaker("HOUR", 50.0, 10.0, 0.0,0);
        Assert.assertTrue("Expect 550.0 +/- 0.1. Actual: " + actual, Math.abs(550.0 - actual) < 0.1 );
    }

    @Test
    public void payCheckMaker_shouldCalculatePay_whenContract() throws SoaException {
        Double actual = Payroll.payCheckMaker("CONTRACT", 40.0, 50000.0, 0.0,10);
        Assert.assertTrue("Expect 5000.0 +/- 0.1. Actual: " + actual, Math.abs(5000.0 - actual) < 0.1 );
    }


    @Test
    public void payCheckMaker_shouldCalculatePay_whenSeasonal() throws SoaException {
        Double actual = Payroll.payCheckMaker("SEASON", 39.0, 10.0, 15.0,10);
        Assert.assertTrue("Expect 150.0 +/- 0.1. Actual: " + actual, Math.abs(150.0 - actual) < 0.1 );
    }


    @Test
    public void payCheckMaker_shouldCalculatePay_whenSeasonalWithOvertime() throws SoaException {
        Double actual = Payroll.payCheckMaker("SEASON", 41.0, 10.0, 15.0,10);
        Assert.assertTrue("Expect 300.0 +/- 0.1. Actual: " + actual, Math.abs(300.0 - actual) < 0.1 );
    }
}
