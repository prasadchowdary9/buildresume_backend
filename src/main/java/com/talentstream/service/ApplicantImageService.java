package com.talentstream.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantImage;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantImageRepository;
import com.talentstream.repository.RegisterRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.InputStreamResource;
@Service
public class ApplicantImageService {
 
    private final Path root = Paths.get("applicantprofileimages");
 
    @Autowired
    private ApplicantImageRepository applicantImageRepository;
 
    @Autowired
    private RegisterRepository applicantService;
 
    public ApplicantImageService() throws IOException {
 
    }
 
    @Value("${aws.s3.bucketName}")
    private String bucketName;
 
    @Value("${aws.accessKey}")
    private String accessKey;
 
    @Value("${aws.secretKey}")
    private String secretKey;
    
    @Value("${aws.region}")
    private String region;
 
    
 
    public String uploadImage(long applicantId, MultipartFile imageFile) {
    	
        if (imageFile.getSize() > 1 * 1024 * 1024) {
            throw new CustomException("File size should be less than 1MB.", HttpStatus.BAD_REQUEST);
        }
        String contentType = imageFile.getContentType();
        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
            throw new CustomException("Only JPG and PNG file types are allowed.", HttpStatus.BAD_REQUEST);
        }
   
      	  Applicant applicant = applicantService.findById(applicantId); // Assuming findById in RegisterRepository
      	  if (applicant == null) {
     
      		  throw new CustomException("Applicant not found for ID: " + applicantId, HttpStatus.NOT_FOUND);
      		  
      	  } else {
   
      	    // Logic for S3 upload
      	    String objectKey =  String.valueOf(applicantId)+".jpg"; // Generate unique object key
   
      	    try {
      	      AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
      	          .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
      	          .withRegion(Regions.US_WEST_2)
      	          .build();
   
      	      s3Client.putObject(
      	          new PutObjectRequest(bucketName, objectKey, imageFile.getInputStream(), createObjectMetadata(imageFile))
      	      );
      	      
   
      	      return objectKey; 
   
      	    } catch (AmazonServiceException ase) {
      	     
      	      throw new RuntimeException("Failed to upload image to S3", ase);
      	    } catch (IOException e) {
      	      
      	      throw new RuntimeException("Error processing image file", e);
      	    }
      	  }
      	}
   
      	private ObjectMetadata createObjectMetadata(MultipartFile imageFile) throws IOException {
      	  ObjectMetadata objectMetadata = new ObjectMetadata();
      	  objectMetadata.setContentType(imageFile.getContentType());
      	  objectMetadata.setContentLength(imageFile.getSize());
      	  return objectMetadata;
      	}

 
      	public ResponseEntity<Resource> getProfilePicByApplicantId(long applicantId) {
      	    try {
      	        
      	       
      	            String objectKey = String.valueOf(applicantId)+".jpg";

      	            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
      	                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
      	                    .withRegion(Regions.US_WEST_2)
      	                    .build();

      	            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, objectKey));
      	            S3ObjectInputStream inputStream = s3Object.getObjectContent();

      	            MediaType mediaType;
      	            if (objectKey.toLowerCase().endsWith(".png")) {
      	                mediaType = MediaType.IMAGE_PNG;
      	            } else if (objectKey.toLowerCase().endsWith(".jpg") || objectKey.toLowerCase().endsWith(".jpeg")) {
      	                mediaType = MediaType.IMAGE_JPEG;
      	            } else {
      	                throw new RuntimeException("Unsupported image file format for applicant ID: " + applicantId);
      	            }

      	            InputStreamResource resource = new InputStreamResource(inputStream);

      	            return ResponseEntity.ok()
      	                    .contentType(mediaType)
      	                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectKey + "\"")
      	                    .body(resource);

      	    } catch (Exception e) {
      	        String errorMessage = "Internal Server Error";
      	        InputStreamResource errorResource = new InputStreamResource(new ByteArrayInputStream(errorMessage.getBytes()));
      	        System.out.println(e.getMessage());
      	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      	                .contentType(MediaType.TEXT_PLAIN)
      	                .body(errorResource);
      	    }
      	}

 
}
    