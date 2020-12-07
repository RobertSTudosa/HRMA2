package com.bzbees.hrma.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class HomeController {
	
	@Value("${version}")
	private String ver;
	
	@GetMapping("/home")
	public String displayHome (Model model, Authentication auth) {
		
		System.out.println("User is : " + auth.getName());
		model.addAttribute("localVerNumber", ver);
		
		return "home";
	}
	




	

}
