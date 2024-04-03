package com.talentstream.controller;
 
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.talentstream.entity.Applicant;
import com.talentstream.repository.RegisterRepository;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.InputStream;
@RestController
@RequestMapping("/resume")
public class PdfController {
//    private final String BUCKET_NAME = "tsresume";
//    private final String FILE_KEY = "docs-getmoto-org-en-latest.pdf"; // e.g., "folder/example.pdf"
    //Candidate.png
    //docs-getmoto-org-en-latest.pdf
	private final String BUCKET_NAME = "talentstream-resume";
    private final String FILE_KEY1 = ""; // e.g., "folder/example.pdf"
    @Autowired
    private RegisterRepository registerRepo;

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable long id) {
    	String accessKeyId = "AKIAWDMD3L7M32MQTC5C";
        String secretAccessKey = "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";
    	//Regions.ap-south-1
//    	String accessKeyId = "AKIARLZHDIM276LV2UHE";
//        String secretAccessKey = "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";
        //AKIARLZHDIM276LV2UHE
        //uKql2llorc+UCmm980xlq/sJAjTC1+1JIEl/HywN
        //resumebuilder1
        //Saheed_Resume.pdf
        //Regions.US_WEST_2
        Applicant applicant=registerRepo.findById(id);
        String resumeId= applicant.getResumeId();
        String FILE_KEY=resumeId+".pdf";
        System.out.println(FILE_KEY);
        try {
        	 BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
             AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                     .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                     .withRegion(Regions.AP_SOUTH_1) // Replace with your desired AWS region
                     .build();
            S3Object s3Object = s3Client.getObject(BUCKET_NAME, FILE_KEY);
            InputStream inputStream = s3Object.getObjectContent();
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", FILE_KEY);
            byte[] content = inputStream.readAllBytes();
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}