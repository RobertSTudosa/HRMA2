package com.bzbees.hrma.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@Value("${version}")
	private String ver;
	
	@GetMapping("/")
	public String displayHome (Model model) {
		
		model.addAttribute("localVerNumber", ver);
		
		return "home";
	}
	




	

}
