package ru.tuchanet.sftpclient;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SftpClientSecurityConfiguration extends WebSecurityConfigurerAdapter {
    
	 @Autowired
	 PasswordEncoder passwordEncoder;
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
		
		String user = prop.getProperty("ssh.user");
		String pass = prop.getProperty("ssh.pass");
		
		// Configure authentication with ssh user and password
		auth
			.inMemoryAuthentication()
			.passwordEncoder(passwordEncoder)
			.withUser(user).password(passwordEncoder.encode(pass)).roles("USER");
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/login").permitAll()
				.antMatchers("/webjars/**").permitAll()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/img/**").permitAll()
				.antMatchers("/**").hasRole("USER")
			.and().formLogin()
	            .loginPage("/login")
	            .defaultSuccessUrl("/list")
	            .failureUrl("/login?error=true")
	            .permitAll()			
            .and().logout()
	            .logoutSuccessUrl("/login?logout=true")
	            .invalidateHttpSession(true)
	            .permitAll()			;
	}
	
}
