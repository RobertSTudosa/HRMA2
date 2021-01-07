package com.bzbees.hrma.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.User;

@Repository
public interface AgencyRepository extends CrudRepository<Agency, Long>{
	
	//create a left outer join to retrieve the agency entity via agency id. join tables user_accounts with agency
		@Query(nativeQuery= true, value= "select agency.agency_id, admin_name, agency_name, agency.user_birth_date," + 
				" agency.user_credentials, agency.user_full_name, agency.user_id " +
				" FROM agency " + 
				" left outer join user_accounts ON user_accounts.user_id = agency.user_id " +
				" WHERE user_accounts.user_id = ?1 ;")
		public Agency findAgencyFromUserId(long userId);
	
	public Agency findAgencyByagencyId(long agencyId);

}
