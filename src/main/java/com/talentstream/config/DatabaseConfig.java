package com.talentstream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import javax.annotation.PostConstruct;
import com.talentstream.AwsSecretsManagerUtil;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {

     @Autowired
   private AwsSecretsManagerUtil awsSecretsManagerUtil;
 
    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
      
       hikariConfig.setJdbcUrl("jdbc:postgresql://aws-0-ap-south-1.pooler.supabase.com:6543/postgres?pgbouncer=true");
        hikariConfig.setUsername(awsSecretsManagerUtil.getDbUsername());
        hikariConfig.setPassword(awsSecretsManagerUtil.getDbPassword());
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setMaxLifetime(60000);
 
        return new HikariDataSource(hikariConfig);

        }
}
