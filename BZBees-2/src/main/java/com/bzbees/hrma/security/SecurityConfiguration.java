package com.bzbees.hrma.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
			.usersByUsernameQuery("select username, password, active " + 
									" from user_accounts where username = ? ")
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
            .antMatchers("/","/**").permitAll()
            .and()
            .formLogin().loginPage("/login").permitAll().defaultSuccessUrl("/", true)
            .and()
            .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login");
    }

}
