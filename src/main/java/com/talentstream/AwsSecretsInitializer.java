package com.talentstream;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
public class AwsSecretsInitializer {

    @Autowired
    private AwsSecretsManagerUtil awsSecretsManagerUtil;

    @PostConstruct
    public void init() {
        String secrets = awsSecretsManagerUtil.getSecret();
        JSONObject jsonObject = new JSONObject(secrets);

        String url=jsonObject.getString("SPRING_DATASOURCE_URL");
        String username=jsonObject.getString("SPRING_DATASOURCE_USERNAME");
        String password=jsonObject.getString("SPRING_DATASOURCE_PASSWORD");
        
        System.out.print(url);
        System.out.print(username);
        System.out.print(password);
        
            System.setProperty("SPRING_DATASOURCE_URL", jsonObject.getString("SPRING_DATASOURCE_URL"));
            System.setProperty("SPRING_DATASOURCE_USERNAME", jsonObject.getString("SPRING_DATASOURCE_USERNAME"));
            System.setProperty("SPRING_DATASOURCE_PASSWORD", jsonObject.getString("SPRING_DATASOURCE_PASSWORD"));
        
    }
}

