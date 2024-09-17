package com.talentstream;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSecretsManagerUtil {

	public static String getSecret() {

		  String secretName = "/secret/myS3SecretKey";
		  String secrets = System.getenv("AWS_ACCESS_KEY_ID");
		  System.out.println("Secretes  "+secrets);
		  JSONObject jsonObject = new JSONObject(secrets);
	        String accessKey = jsonObject.getString("AWS_ACCESS_KEY_ID");
	        System.out.println("accessKey  "+accessKey);
	        String secretKey = jsonObject.getString("AWS_SECRET_ACCESS_KEY");
	        System.out.println("secretKey  "+secretKey);
	        String region1 = jsonObject.getString("AWS_REGION");
	        System.out.println("region1  "+region1);
	        Region region = Region.of(region1);
	        
	        
	        if (accessKey == null || secretKey == null) {
	            System.err.println("AWS credentials are not set in environment variables.");
	            return null;
	        }
		  
//		// Directly get environment variables without parsing them as JSON
//		  System.out.println("debug -1");
//		    String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
//		    System.out.println(accessKey+"debug -2");
//		    String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
//		    String region1 = System.getenv("AWS_REGION");
//		    System.out.println(region1+" debug -3");
//		 // Create an AWS Region object
//		    Region region = Region.of("us-west-2");
//		    
//		    if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
//		        System.err.println("AWS credentials are not set in environment variables.");
//		        return null;
//		    }

		    
		    

	        // Create a Secrets Manager client
	        try (SecretsManagerClient client = SecretsManagerClient.builder()
	                .region(region)
	                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
	                .build()) {

	            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
	                    .secretId(secretName)
	                    .build();

	            GetSecretValueResponse getSecretValueResponse;

	            try {
	                getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
	            } catch (Exception e) {
	                System.err.println("Error retrieving secret: " + e.getMessage());
	                throw e;
	            }

	            String secret = getSecretValueResponse.secretString();
	           
	            return secret;
	        } catch (Exception e) {
	            System.err.println("An error occurred: " + e.getMessage());
	            return null;
	        }
    }

    
}
