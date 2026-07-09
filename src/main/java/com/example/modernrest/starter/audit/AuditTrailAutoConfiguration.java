package com.example.modernrest.starter.audit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(AuditTrailProperties.class)
public class AuditTrailAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuditTrailService auditTrailService(AuditTrailProperties properties) {
        return new AuditTrailService(properties);
    }
}
