package com.cronjob;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

@Singleton
@Startup
public class TestCronjob {
    private final String UPLOAD_FOLDER = System.getenv("FILE_PATH");
    private final String _BASE_PATH = System.getProperty("jboss.server.data.dir") + UPLOAD_FOLDER;
    @Resource
    TimerService timerService;

    @PostConstruct
    public void initialize(){
        TimerConfig config = new TimerConfig();
        config.setInfo("timer1");
        config.setPersistent(false);
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.second(30).minute(59).hour(23).start(Calendar.getInstance().getTime());
        timerService.createCalendarTimer(schedule,config);
    }

    @Timeout
    public void logTesting() throws IOException{
        System.out.println(System.getenv("CRONJOB_DATE_OF_MONTH"));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        File file = new File(_BASE_PATH+Long.toString(timestamp.getTime())+".txt");
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(!file.exists()){
            file.createNewFile();
            System.out.println();
        }
    }
}
