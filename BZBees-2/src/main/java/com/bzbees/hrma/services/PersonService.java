package com.bzbees.hrma.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.PersonRepository;
import com.bzbees.hrma.entities.Person;

@Service
public class PersonService {
	
	@Autowired 
	PersonRepository persRepo;
	
	public Person save(Person person) {
		return persRepo.save(person);
	}
	
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
	public boolean checkAvailability (Person person) {
		

		if (person.getAvailability().after(new Date())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkBirthDate (Person person) {
		
		long nowT = new Date().getTime();
		nowT -= 18*24*60*60*1000;
		Date adult18 = new Date(nowT);
		if (person.getBirthDate().before(adult18)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Person findPersonById (long id) {
		return persRepo.findPersonByPersonId(id);
	}
	
	public List<Person> getAll () {
		return (List<Person>) persRepo.findAll();
		
	}
	
	public Person findPersonByUserId(long userId) {
		return persRepo.findPersonFromUserId(userId);
	}
	
	
	
}
