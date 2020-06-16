package ru.tuchanet.sftpclient;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootApplication
public class SftpClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SftpClientApplication.class, args);
	}
	
	
	@Bean(name = "messageSource")
	public ResourceBundleMessageSource getMessageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	    messageSource.setBasename("messages/messages");
	    messageSource.setDefaultEncoding("UTF-8");
	    return messageSource;
	}
	
	@Bean
	public LocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setDefaultLocale(Locale.forLanguageTag("ru"));
	    return (LocaleResolver) slr;
	}
	
}
