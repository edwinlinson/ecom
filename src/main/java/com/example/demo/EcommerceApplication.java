package com.example.demo;

import com.example.demo.Configuration.TwilioConfig;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class EcommerceApplication {
	@Autowired
	TwilioConfig twilioConfig;
	@PostConstruct
	public void initTwilio(){
		Twilio.init(System.getenv("twilio.account-sid"),System.getenv("twilio.auth_token"));
	}

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

}
