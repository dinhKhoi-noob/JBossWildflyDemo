package com.cronjob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
public class CleanerUnusedFile{

    private final String _BASE_PATH = System.getProperty("jboss.server.data.dir") + "/upload/";
    private final String CRONJOB_DATE_OF_MONTH = System.getenv("CRONJOB_DATE_OF_MONTH");
    private final String CRONJOB_DATE_OF_WEEK = System.getenv("CRONJOB_DATE_OF_WEEK");
    private final String CRONJOB_HOUR = System.getenv("CRONJOB_HOUR");
    private final String CRONJOB_MINUTE = System.getenv("CRONJOB_MINUTE");

    @Resource
    TimerService timerService;

    @PostConstruct
    public void initialize(){
        TimerConfig config = new TimerConfig();
        config.setInfo("timer1");
        config.setPersistent(false);
        ScheduleExpression schedule = new ScheduleExpression();
        schedule
        .dayOfMonth(CRONJOB_DATE_OF_MONTH)
        .dayOfWeek(CRONJOB_DATE_OF_WEEK)
        .second(59)
        .minute(CRONJOB_MINUTE)
        .hour(CRONJOB_HOUR)
        .start(Calendar.getInstance().getTime());
        timerService.createCalendarTimer(schedule,config);
    }

    @Timeout
    public void logTesting() throws IOException{
        File directory = new File(_BASE_PATH);
        List<String> removedFiles = new ArrayList<String>();
        if(!directory.getParentFile().exists()){
            directory.getParentFile().mkdirs();
        }
        if(!directory.exists()){
            directory.createNewFile();
        }
        else{
            File[] fileList = new File[(int) directory.length()];
            fileList = directory.listFiles();
            for(File file: fileList){
                String fileName = file.getName();
                BasicFileAttributes attr = Files.readAttributes(file.toPath(),BasicFileAttributes.class);
                Date createdFileDate = new Date(attr.creationTime().toMillis());
                Date comparisonDate = Date.from(ZonedDateTime.now().minusMonths(1).toInstant());
                if(createdFileDate.before(comparisonDate)){
                    file.delete();
                    removedFiles.add(fileName);
                }
            }
        }
        System.out.println("Removed files: "+removedFiles);
        System.out.println("File after execute remove cron job: "+directory.listFiles());
    }
}
