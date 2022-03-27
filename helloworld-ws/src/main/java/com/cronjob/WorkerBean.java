package com.cronjob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

@Singleton
public class WorkerBean {
    private final String UPLOAD_FOLDER = System.getenv("FILE_PATH");
    private final String _BASE_PATH = System.getProperty("jboss.server.data.dir") + UPLOAD_FOLDER;

    @Lock(LockType.READ)
    public void deleteUnusedFile() throws IOException {
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
