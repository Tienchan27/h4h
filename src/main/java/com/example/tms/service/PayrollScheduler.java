package com.example.tms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PayrollScheduler {
    private static final Logger log = LoggerFactory.getLogger(PayrollScheduler.class);

    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Ho_Chi_Minh")
    public void runMonthlyPayrollPlaceholder() {
        log.info("Monthly payroll scheduler triggered");
    }
}
