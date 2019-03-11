package com.codingforhappy.config;

import com.codingforhappy.sms.persistence.SMSPersistenceOperation;
import com.codingforhappy.sms.sender.JuheShortMessageSenderAdapter;
import com.codingforhappy.sms.sender.ShortMessageSender;
import com.codingforhappy.sms.service.ShortMessageService;
import com.codingforhappy.sms.service.ShortMessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:sms.properties")
public class SMSServiceConfig {

    @Value("${SMS.APPKEY}")
    String appKey;

    @Value("${SMS.SIGNATURE}")
    String signature;

    @Value("${SMS.REGISTER_TEMP}")
    String registerTempID;

    @Bean
    public ShortMessageService getShortMessageService(
            SMSPersistenceOperation persistenceOperation, ShortMessageSender shortMessageSender) {
        ShortMessageServiceImpl shortMessageService = new ShortMessageServiceImpl(persistenceOperation, shortMessageSender);
        shortMessageService.setSmsSignature(signature);
        shortMessageService.setRegisterTempID(registerTempID);
        return shortMessageService;
    }

    @Bean
    public ShortMessageSender getShortMessageSender() {
        return new JuheShortMessageSenderAdapter(appKey);
    }
}
