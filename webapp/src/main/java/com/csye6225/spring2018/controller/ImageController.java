package com.csye6225.spring2018.controller;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.csye6225.spring2018.User;
import com.csye6225.spring2018.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Controller
public class ImageController {

    @Autowired
    private UserRepository userRepository;

    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Value("${endpointUrl}")
    private String endpointUrl;
    @Value("${bucketName}")
    private String bucketName;
    @Value("${image.default.link}")
    private String defaultLocation;

    @Autowired
    Environment env;

    @PostMapping("/uploadPhoto")
    public String uploadPhoto(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null){
            return "index";
        }
        String existPic = String.valueOf(session.getAttribute("fileUrl"));
        logger.info("Attempting to save file by using post method");
            for (User u : userRepository.findAll()) {
                if (u.getEmail().equals(session.getAttribute("email"))) {
                    if(env.getProperty("app.profile.path").equals("aws")) {
                        if (!existPic.equals(defaultLocation)) {
                            logger.info("Deleting existing image");
                            deleteFileFromS3Bucket(existPic);
                        }
                        logger.info("Uploading to S3");
                        String fileUrl = uploadFileToS3Bucket(multipartFile);
                        logger.info("Upload method complete");
                        u.setPhoto_location(fileUrl);
                        session.setAttribute("fileUrl", fileUrl);
                        session.setAttribute("uploadMessage", "You successfully uploaded '" + multipartFile.getOriginalFilename() + "'");
                    }

                    else {
                        try {
                            File file = new File(existPic);
                            if (!existPic.equals("/home/temp/google.png")) {
                                if (file.exists()) {
                                    file.delete();
                                }
                            }
                            String fileUrl = "/home/temp/" + u.getId() + multipartFile.getOriginalFilename();
                            multipartFile.transferTo(file);
                            u.setPhoto_location(fileUrl);
                            session.setAttribute("fileUrl", fileUrl);
                            session.setAttribute("uploadMessage", "You successfully uploaded '" + multipartFile.getOriginalFilename() + "'");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return "editProfile";
    }

    @PostMapping("/deletePhoto")
    public String deleteImagePost(HttpServletRequest request){

        HttpSession session = request.getSession(false);
        if(session == null){
            return "index";
        }
        String email = String.valueOf(session.getAttribute("email"));
        String fileUrl = String.valueOf(session.getAttribute("fileUrl"));
        String destination = defaultLocation;
        for (User u : userRepository.findAll()) {
            if (u.getEmail().equals(email)) {
                if (env.getProperty("app.profile.path").equals("aws")) {
                    String deleteMsg = deleteFileFromS3Bucket(fileUrl);
                    u.setPhoto_location(destination);
                    session.setAttribute("fileUrl", destination);
                    session.setAttribute("deleteMessage", deleteMsg);
                } else {
                    File file = new File(fileUrl);
                    if (!fileUrl.equalsIgnoreCase("/home/temp/google.png")) {
                        file.delete();
                    }
                    u.setPhoto_location(destination);
                    session.setAttribute("fileUrl", destination);
                    session.setAttribute("deleteMessage", "You have successfully deleted the photo");
                }
                session.setAttribute("uploadMessage", "");
            }

        }
        return "editProfile";
    }

    private String deleteFileFromS3Bucket(String fileUrl){
        try {
            String bucket = bucketName.substring(0, bucketName.length() - ".s3.amazonaws.com".length());
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            s3.deleteObject(new DeleteObjectRequest(bucket, fileName));
            return "Successfully deleted";
        }catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        return "Delete Unsuccessful";
    }

    private String uploadFileToS3Bucket(MultipartFile multipartFile){
        try{
            String bucket = bucketName.substring(0, bucketName.length() - ".s3.amazonaws.com".length());
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            String fileUrl = "";
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            fileUrl = endpointUrl + "/" + bucket + "/" + fileName;
            s3.putObject(new PutObjectRequest(bucket, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
            file.delete();
            return fileUrl;
        }catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (Exception e) {
        e.printStackTrace();
    }
        return "default";
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File("/home/ubuntu/temp/" +file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }
}
