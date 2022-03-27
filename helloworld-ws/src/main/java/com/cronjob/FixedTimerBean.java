package com.cronjob;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;

@Singleton
public class FixedTimerBean {

    @EJB
    private WorkerBean workerBean;

    // @Lock(LockType.READ)
    // @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    // public void atSchedule() throws InterruptedException, IOException {
    //     workerBean.doTimerWork();
    // }
    // private static final String CRON_DATE_OF_MONTH = System.getenv("CRON_DATE_OF_MONTH");
    @Lock(LockType.READ)
    @Schedules ({
        @Schedule(dayOfMonth="Last"),
        @Schedule(dayOfWeek="Fri", hour="23")
     })
    public void cleaner() throws InterruptedException, IOException {
        workerBean.deleteUnusedFile();
    }
}