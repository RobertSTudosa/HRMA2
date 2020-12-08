package com.bzbees.hrma.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.User;

@Repository
public interface UserRepository extends CrudRepository <User, Long> {
	
	@Override
	public List<User> findAll();
	
	
	//create a left outer join to retrieve the user entity via person id. join tables user_accounts with persons
	@Query(nativeQuery= true, value= "select user_accounts.user_id, username, user_accounts.email, password, active, account_non_expired, account_non_locked, credentials_non_expired "  
						+ " FROM user_accounts "  
						+ " left outer join persons ON user_accounts.user_id = persons.person_id " 
						+ " WHERE persons.person_id = ?1 ;")
	public User findUserFromPersonId(long personId);
	
	@Query(nativeQuery= true, value = "select user_accounts.user_id, username, user_accounts.email, password, active, " 
						+ " account_non_expired, account_non_locked, credentials_non_expired, person_id "
						+ " FROM user_accounts "					 
						+ " WHERE user_accounts.username = ?1 ;")
	public User findUserByUsername(String username);

}
