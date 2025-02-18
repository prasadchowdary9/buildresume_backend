package com.talentstream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import javax.annotation.PostConstruct;
import com.talentstream.AwsSecretsManagerUtil;

@Configuration
public class DatabaseConfig {

    private final Environment environment;
    private final AwsSecretsManagerUtil secretsManagerUtil;

    public DatabaseConfig(Environment environment, AwsSecretsManagerUtil secretsManagerUtil) {
        this.environment = environment;
        this.secretsManagerUtil = secretsManagerUtil;
    }

    @PostConstruct
    public void init() {
        System.setProperty("DB_USERNAME", secretsManagerUtil.getDbUsername());
        System.setProperty("DB_PASSWORD", secretsManagerUtil.getDbPassword());
    }
}