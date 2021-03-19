package com.bzbees.hrma.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.dao.PersonRepository;
import com.bzbees.hrma.dao.UserRepository;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.entities.UserRole;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	UserRepository userAccountRepo;
	
	@Autowired
	PersonRepository persRepo;
	
	public User save (User userAccount) {
		return userAccountRepo.save(userAccount);
	}
	
	public User saveUserAccountAndPerson(User userAccount, Person person) {
		
		persRepo.save(person);
		return userAccountRepo.save(userAccount);
		
	}
	
	public List<User> getAll() {

		return (List<User>)  userAccountRepo.findAll();
	}
	
	public Optional<User> findUserById(long userId) {
		return userAccountRepo.findById(userId);
	}
	
	
	public User findUserByPersonId (long personId) {
		return userAccountRepo.findUserFromPersonId(personId);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			User user = userAccountRepo.findUserByUsername(username);
//			return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),user.getRoles());
		return user;

	}
	
	public User findUserByEmailAddress(String userEmail) {
		return userAccountRepo.findByEmailIgnoreCase(userEmail);
	}
	
	public List<User> getAllAffiliatedUsersByAgencyId(long agencyId) {
		return userAccountRepo.getAffiliatedUsersByAgencyID(agencyId);
	}
	
	public List<User> getAllPendingUsersByAgencyId(long agencyId) {
		return userAccountRepo.getPendingUsersByAgencyID(agencyId);
	}
	
	


}
