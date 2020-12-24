package com.bzbees.hrma.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class HomeController {
	
	@Value("${version}")
	private String ver;
	
	@GetMapping("/")
	public String displayHome ( Model model, Authentication auth) {
		
		String message ="";
		model.addAttribute("message", message);
		
		
//		System.out.println("Name of the user : " + auth.getName());
		model.addAttribute("localVerNumber", ver);
		
		return "home";
	}
	




	

}
