package com.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/file")
public class UploadService {
    private final String UPLOAD_FOLDER = System.getenv("FILE_PATH");
    private final String _BASE_PATH = System.getProperty("jboss.server.data.dir") + UPLOAD_FOLDER;
    @GET
    @Path("/ping")
    public Response ping() {
        System.out.println(System.getenv().get("JBOSS_HOME"));
        return Response.ok().entity("Service online").build();
    }
    @POST
    @Path("/post")
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput data){
        String fileName = "";
        int statusCode = 202;
        String responseMessage = "Uploaded";
        Map<String,List<InputPart>> uploadForm = data.getFormDataMap();
        List<InputPart> inputParts= uploadForm.get("uploadedFile");
        for(int i = 0;i < inputParts.size() && statusCode == 202;i++){
            try {
                InputPart inputPart = inputParts.get(i);
                MultivaluedMap<String,String> headers = inputPart.getHeaders();
                fileName = getFileName(headers);
                if(fileName.equals("unknown")){
                    statusCode = 400;
                    responseMessage = "Unknown file name!";
                    break;
                }
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                fileName = _BASE_PATH + Long.toString(timestamp.getTime()) + fileName;
                System.out.println(fileName);
                if(!writeFile(bytes, fileName)){
                    statusCode = 400;
                    responseMessage = "Invalid file type!";
                }
            } catch (IOException e) {
                statusCode = 500;
                responseMessage = "Internal server error!";
                e.printStackTrace();
            }
        }
        return Response.status(statusCode).entity(statusCode == 200?"Post Success! at "+fileName:responseMessage).build();
    }
    public String getFileName(MultivaluedMap<String,String> header){
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for(String fileName: contentDisposition){
            System.out.println(fileName);
            if(fileName.trim().startsWith("filename")){
                String[] name = fileName.split("=");
                String finalName = name[1].trim().replaceAll("\"", "");
                return finalName;
            }
        }
        return "unknown";
    }
    public boolean writeFile(byte[] content,String fileName) throws IOException{
        File file = new File(fileName);
        String mimetype = URLConnection.guessContentTypeFromName(file.getName());
        if(!mimetype.contains("image")){
            return false;
        }
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
        return true;
    }
}
