package com.bzbees.hrma.api.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/admin")
public class AdminApiController {
	
	
	@GetMapping("/dashboard")
	public String showDashboard(Model model, Authentication auth) {
		
		
		return "dashboard";
	}
	

}
