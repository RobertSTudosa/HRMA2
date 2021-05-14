package com.bzbees.hrma;

import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.bzbees.hrma.services.UserService;

@SpringBootApplication
//@ComponentScan({"com.bzbess.hrma.controllers","com.bzbess.hrma.dao","com.bzbess.hrma.dto",
//	"com.bzbess.hrma.entities", "com.bzbess.hrma.services", "com.bzbess.hrma.logging"})
public class BzBees1Application {
	
	@Autowired
	UserService userServ;
	
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(BzBees1Application.class, args);
	}

}
