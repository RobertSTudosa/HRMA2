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
				" agency.user_credentials, agency.user_full_name, agency.user_id, agency.unique_reg_code, "
				+ " agency.reg_com_number, agency.legal_address, agency.web_address, agency.email, agency.phone_number,"
				+ " agency.last_agency_image_id, agency.short_description, agency.account_non_expired, agency.account_non_locked,"
				+ " agency.credentials_non_expired, agency.active, agency_likes_count " +
				" FROM agency " + 
				" left outer join user_accounts ON user_accounts.user_id = agency.user_id " +
				" WHERE user_accounts.user_id = ?1 ;")
	public Agency findAgencyFromUserId(long userId);
	
	public Agency findAgencyByagencyId(long agencyId);
	
	public void deleteAgencyByagencyId(long agencyId);
	
	@Query(nativeQuery= true, value= "select agency.agency_id, admin_name, agency_name, agency.user_birth_date," + 
			" agency.user_credentials, agency.user_full_name, agency.user_id, agency.unique_reg_code, "
			+ " agency.reg_com_number, agency.legal_address, agency.web_address, agency.email, agency.phone_number,"
			+ " agency.last_agency_image_id, agency.short_description, agency.account_non_expired, agency.account_non_locked,"
			+ " agency.credentials_non_expired, agency.active, agency_likes_count " +
			" FROM agency " + 
			" left outer join jobs ON jobs.the_agency_agency_id = agency.agency_id" +
			" WHERE jobs.job_id = ?1 ;")
	public Agency findAgencyByJobId(long jobId);
	
	
	
//	public Agency findAgencyByJobId(long jobId);

}
