package com.bzbees.hrma.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	BCryptPasswordEncoder bCryptEncoder;
	
	// implement an authenticationProvider to be called on necessary controllers 
//	@Autowired
//	CustomAuthenticationProvider authenticationProvider;
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		auth
//		.authenticationProvider(authenticationProvider) // <---- this is how to call a custom auth provider
			.jdbcAuthentication()	
			.dataSource(dataSource)
			.usersByUsernameQuery("select username, password, active " + 
									" from user_accounts where username collate latin1_general_cs = ? ")
			.authoritiesByUsernameQuery("select  user_accounts.username, userrole.permission from userrole " + 
					" join  user_role on user_role.role_id= userrole.role_id " + 
					" join user_accounts on user_accounts.user_id = user_role.user_id "
					+ " where username = ? ")
			.passwordEncoder(bCryptEncoder);//used to decrypt/decode the password
	}
	
	
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/home").hasAuthority("USER")
            .antMatchers("/","/**","/css/**","/js/**" ).permitAll()
            .and()
            .formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/", true)
            .and()
            .rememberMe().tokenValiditySeconds(2925000)
        	.and()
            .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/")
            .invalidateHttpSession(true).deleteCookies("JSESSIONID");         
    }
	
	@Bean
	@Override 
	public AuthenticationManager authenticationManagerBean() 
			throws Exception { 		
		return super.authenticationManagerBean(); 	
		}
	

}
