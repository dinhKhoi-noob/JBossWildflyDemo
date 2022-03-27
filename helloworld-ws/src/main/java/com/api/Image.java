package com.api;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/image")
public class Image {
    private final String UPLOAD_FOLDER = System.getenv("FILE_PATH");
    private final String _BASE_PATH = System.getProperty("jboss.server.data.dir") + UPLOAD_FOLDER;

    @GET
    @Path("/{src}")
    @Produces("image/png")
    public Response renderImage(@PathParam("src") String imageSource){
        File file = new File(_BASE_PATH+imageSource);
        if(file.exists()){
            String mt = new MimetypesFileTypeMap().getContentType(file);
            return Response.ok(file, mt).build();
        }
        else{
            return Response.status(400).entity("Not found").build();
        }
    }
}
