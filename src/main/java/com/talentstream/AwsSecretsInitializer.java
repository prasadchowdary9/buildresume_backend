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

        for (String key : jsonObject.keySet()) {
            System.setProperty(key, jsonObject.getString(key));
        }
    }
}

