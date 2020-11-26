package com.bzbees.hrma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bzbees.hrma.dao.PersonRepository;
import com.bzbees.hrma.dao.UserRepository;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/login")
public class SecurityController {
	
	
	
	
	
	@GetMapping()
	public String displayLoginForm () {
		
		return "user/login";
	}
	

	
	
	
	

	
}
