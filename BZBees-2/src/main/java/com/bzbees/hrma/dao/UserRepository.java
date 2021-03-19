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
	@Query(nativeQuery= true, value= "select user_accounts.user_id, username, user_accounts.email, password, user_accounts.active, " +
 						" user_accounts.account_non_expired, user_accounts.account_non_locked, "
 						+ "user_accounts.credentials_non_expired, user_accounts.one_agency_agency_id, "
 						+ "user_accounts.person_id, pending_agency_agency_id"  
						+ " FROM user_accounts "  
						+ " left outer join persons ON user_accounts.user_id = persons.person_id " 
						+ " WHERE persons.person_id = ?1 ;")
	public User findUserFromPersonId(long personId);
	
	@Query(nativeQuery= true, value = "select user_accounts.user_id, username, user_accounts.email, password, user_accounts.active, " +
 						" user_accounts.account_non_expired, user_accounts.account_non_locked, "
 						+ "user_accounts.credentials_non_expired, user_accounts.one_agency_agency_id, "
 						+ "user_accounts.person_id, pending_agency_agency_id " 
						+ " FROM user_accounts "					 
						+ " WHERE user_accounts.username = ?1 ;")
	public User findUserByUsername(String username);
	
	
	@Query(nativeQuery= true, value = "select user_accounts.user_id, username, user_accounts.email, password, user_accounts.active, " +
			 			" user_accounts.account_non_expired, user_accounts.account_non_locked, "
			 			+ "user_accounts.credentials_non_expired, user_accounts.one_agency_agency_id, "
			 			+ "user_accounts.person_id, pending_agency_agency_id" + 
			 			" FROM user_accounts " + 
			 			" left outer join agency ON user_accounts.one_agency_agency_id = agency.agency_id " + 
						" WHERE agency.agency_id = ?1 ;")
	public List<User> getAffiliatedUsersByAgencyID(long agencyId);
	
	@Query(nativeQuery= true, value = "select user_accounts.user_id, username, user_accounts.email, password, user_accounts.active, " +
 			" user_accounts.account_non_expired, user_accounts.account_non_locked, "
 			+ "user_accounts.credentials_non_expired, user_accounts.one_agency_agency_id, "
 			+ "user_accounts.person_id, pending_agency_agency_id" + 
 			" FROM user_accounts " + 
 			" left outer join agency ON user_accounts.pending_agency_agency_id = agency.agency_id " + 
			" WHERE agency.agency_id = ?1 ;")
	public List<User> getPendingUsersByAgencyID(long agencyId);

	
	
	public User findByEmailIgnoreCase(String email);

}
