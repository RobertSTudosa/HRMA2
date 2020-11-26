package com.bzbees.hrma.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.services.PersonService;

@Controller
@RequestMapping("/person")
public class PersonController {
	
	@Autowired
	PersonService persServ;
	
	

	@GetMapping("/newPerson") 
		public String displayRegisterForm (Model model) {
			Person person = new Person();
			model.addAttribute("person", person);
		
			return "register";
		}
	
	@PostMapping("savePerson") 
	public String createNewUser (Model model, Person person) {
		persServ.save(person);
		return "user/profile";
	}

}
